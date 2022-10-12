/*
 * Copyright 2020 jrosclient project
 * 
 * Website: https://github.com/lambdaprime/jros2client
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package id.jros2client.tests.integration;

import static java.util.stream.Collectors.toList;

import id.jros2client.JRos2Client;
import id.jros2client.JRos2ClientConfiguration;
import id.jros2client.JRos2ClientFactory;
import id.jros2messages.MessageSerializationUtils;
import id.jros2messages.sensor_msgs.ImageMessage;
import id.jrosclient.TopicSubmissionPublisher;
import id.jrosclient.TopicSubscriber;
import id.jrosmessages.std_msgs.StringMessage;
import id.xfunction.AssertRunCommand;
import id.xfunction.concurrent.flow.FixedCollectorSubscriber;
import id.xfunction.lang.XThread;
import id.xfunction.logging.XLogger;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pinorobotics.rtpstalk.RtpsTalkConfiguration;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class JRos2ClientTests {

    private static final JRos2ClientFactory factory = new JRos2ClientFactory();
    private static Ros2Commands ros2Commands;
    private static JRos2Client client;

    @BeforeAll
    public static void setupAll() {
        XLogger.load("jros2client-test-logging.properties");
    }

    @BeforeEach
    public void setup() throws MalformedURLException {
        client = factory.createClient();
        ros2Commands = new Ros2Commands();
    }

    @AfterEach
    public void clean() throws Exception {
        client.close();
        ros2Commands.close();
    }

    @Test
    public void test_subscribe_happy() throws Exception {
        var future = new CompletableFuture<List<Integer>>();
        client.subscribe(
                new TopicSubscriber<>(StringMessage.class, "chatter") {
                    private List<Integer> buf = new ArrayList<>();

                    @Override
                    public void onNext(StringMessage item) {
                        System.out.println(item.data);
                        var n = Integer.parseInt(item.data.substring("Hello World: ".length()));
                        buf.add(n);
                        if (buf.size() == 5) {
                            getSubscription().get().cancel();
                            future.complete(reduceByFirst(buf));
                            return;
                        }
                        getSubscription().get().request(1);
                    }
                });
        ros2Commands.runTalker().forwardOutputAsync();
        Assertions.assertEquals("[0, 1, 2, 3, 4]", future.get().toString());
    }

    @Test
    public void test_publish_happy() throws Exception {
        var topicName = "/chatter";
        var publisher = new TopicSubmissionPublisher<>(StringMessage.class, topicName);
        client.publish(publisher);
        var proc = ros2Commands.runListener();
        ForkJoinPool.commonPool()
                .execute(
                        () -> {
                            int c = 0;
                            while (true) {
                                publisher.submit(new StringMessage("" + c++));
                                XThread.sleep(100);
                            }
                        });
        var actual =
                proc.stderrAsStream()
                        .peek(System.out::println)
                        .limit(10)
                        .map(
                                line ->
                                        Integer.parseInt(
                                                line.replaceAll(".*I heard: \\[(\\d*)\\]", "$1")))
                        .collect(toList());
        for (int i = 1; i < actual.size(); i++) {
            Assertions.assertTrue(actual.get(i - 1) < actual.get(i));
        }
        new AssertRunCommand(
                        "ros2",
                        "topic",
                        "info",
                        "--spin-time",
                        ""
                                + client.getConfiguration()
                                                .rtpsTalkConfiguration()
                                                .spdpDiscoveredParticipantDataPublishPeriod()
                                                .toSeconds()
                                        * 2,
                        "-v",
                        "--no-daemon",
                        topicName)
                .withOutputConsumer(System.out::println)
                .assertOutputFromResource("test_publish_happy")
                .withWildcardMatching()
                .assertReturnCode(0)
                .run();
    }

    /**
     * Test that:
     *
     * <ul>
     *   <li>messages over 1mb are fragmented
     *   <li>publisher can support multiple subscribers with different DURABILITY (rqt has
     *       BEST_EFFORT)
     *   <li>when history cache runs out we clean it up after RELIABLE Readers ack the changes
     * </ul>
     *
     * <p>To complete the test user suppose to close rqt window only when image is displayed
     */
    @Test
    public void test_publish_message_over_1Mb() throws Exception {
        var message =
                new MessageSerializationUtils()
                        .read(
                                Files.readAllBytes(
                                        Paths.get("samples/ImageMessage/seattle_ImageMessage.bin")),
                                ImageMessage.class);
        var proc = ros2Commands.runRqt();
        try (var publisherClient =
                        factory.createClient(
                                new JRos2ClientConfiguration.Builder()
                                        .rtpsTalkConfiguration(
                                                new RtpsTalkConfiguration.Builder()
                                                        .historyCacheMaxSize(3)
                                                        .build())
                                        .build());
                var subscriberClient = factory.createClient(); ) {
            String topic = "testTopic1";
            var publisher = new TopicSubmissionPublisher<>(ImageMessage.class, topic);
            publisherClient.publish(publisher);
            var collector = new FixedCollectorSubscriber<>(new ArrayList<ImageMessage>(), 8);
            subscriberClient.subscribe(topic, ImageMessage.class, collector);
            ForkJoinPool.commonPool()
                    .execute(
                            () -> {
                                while (!collector.getFuture().isDone()) {
                                    publisher.submit(message);
                                }
                            });
            proc.await();
            var actual = collector.getFuture().get();
            Assertions.assertEquals(8, actual.size());
            Assertions.assertEquals(message, actual.get(0));
        }
    }

    private List<Integer> reduceByFirst(List<Integer> buf) {
        var first = buf.get(0).intValue();
        return buf.stream().map(i -> i - first).collect(toList());
    }
}
