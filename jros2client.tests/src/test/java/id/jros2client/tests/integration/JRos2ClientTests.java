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

import id.jros2client.JRos2ClientFactory;
import id.jrosclient.JRosClient;
import id.jrosclient.TopicSubmissionPublisher;
import id.jrosclient.TopicSubscriber;
import id.jrosmessages.std_msgs.StringMessage;
import id.xfunction.lang.XThread;
import id.xfunction.logging.XLogger;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JRos2ClientTests {

    private static final JRos2ClientFactory factory = new JRos2ClientFactory();
    private static Ros2Commands ros2Commands;
    private static JRosClient client;

    @BeforeAll
    public static void setupAll() {
        XLogger.load("jros2client-test-logging.properties");
    }

    @BeforeEach
    public void setup() throws MalformedURLException {
        client = factory.createJRosClient();
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
        var publisher = new TopicSubmissionPublisher<>(StringMessage.class, "chatter");
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
        proc.outputAsync(true);
        for (int i = 1; i < actual.size(); i++) {
            Assertions.assertTrue(actual.get(i - 1) < actual.get(i));
        }
    }

    private List<Integer> reduceByFirst(List<Integer> buf) {
        var first = buf.get(0).intValue();
        return buf.stream().map(i -> i - first).collect(toList());
    }
}
