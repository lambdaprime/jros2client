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
import id.opentelemetry.exporters.extensions.ElasticsearchMetricsExtension;
import id.xfunction.concurrent.flow.FixedCollectorSubscriber;
import id.xfunction.lang.XThread;
import id.xfunction.logging.XLogger;
import id.xfunctiontests.AssertRunCommand;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pinorobotics.rtpstalk.RtpsTalkConfiguration;

/**
 * @author lambdaprime intid@protonmail.com
 */
@ExtendWith({ElasticsearchMetricsExtension.class})
public class JRos2ClientTests {

    private static final JRos2ClientFactory factory = new JRos2ClientFactory();
    private static Ros2Commands ros2Commands;
    private static JRos2Client client;

    static Stream<Ros2Commands> dataProvider() {
        return Stream.of(
                new Ros2Commands(RmwImplementation.FASTDDS),
                new Ros2Commands(RmwImplementation.CYCLONEDDS));
    }

    @BeforeAll
    public static void setupAll() {
        XLogger.load("jros2client-test-logging.properties");
    }

    @BeforeEach
    public void setup() throws MalformedURLException {
        client = factory.createClient();
    }

    @AfterEach
    public void clean() throws Exception {
        System.out.println("Closing client");
        client.close();
        System.out.println("Terminating ROS2 commands");
        ros2Commands.close();
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test_subscribe_happy(Ros2Commands commands) throws Exception {
        ros2Commands = commands;
        var future = new CompletableFuture<List<Integer>>();
        client.subscribe(
                new TopicSubscriber<>(StringMessage.class, "chatter") {
                    private List<Integer> buf = new ArrayList<>();

                    @Override
                    public void onNext(StringMessage item) {
                        super.onNext(item);
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
        ros2Commands.runTalker().forwardOutputAsync(true);
        Assertions.assertEquals("[0, 1, 2, 3, 4]", future.get().toString());
    }

    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test_publish_happy(Ros2Commands commands) throws Exception {
        ros2Commands = commands;
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
     *   <li>messages over 1mb are fragmented (total bytes expected 1,555,248)
     *   <li>publisher can support multiple subscribers: first subscriber is rqt with
     *       DURABILITY=BEST_EFFORT, second subscriber is jrosclient with DURABILITY=RELIABLE.
     *   <li>when history cache runs out we clean it up after RELIABLE Readers ack the changes
     * </ul>
     *
     * <p>To complete the test user suppose to close rqt window only when image is displayed
     */
    @ParameterizedTest
    @MethodSource("dataProvider")
    public void test_publish_message_over_1Mb(Ros2Commands commands) throws Exception {
        ros2Commands = commands;
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
            proc.stderrThrow();
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
