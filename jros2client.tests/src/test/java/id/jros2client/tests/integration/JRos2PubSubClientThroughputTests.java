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

import id.jros2client.JRos2ClientFactory;
import id.jrosclient.JRosClient;
import id.jrosclient.tests.integration.JRosPubSubClientThroughputTests;
import id.opentelemetry.exporters.extensions.ElasticsearchMetricsExtension;
import id.xfunction.logging.XLogger;
import java.time.Duration;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({ElasticsearchMetricsExtension.class})
public class JRos2PubSubClientThroughputTests extends JRosPubSubClientThroughputTests {
    private static final JRos2ClientFactory factory = new JRos2ClientFactory();

    static {
        Supplier<JRosClient> clientFactory = factory::createClient;
        init(
                /**
                 * Send 83 packages where each package size is 60kb (total data 5mb). Expected time
                 * - less than 10secs
                 */
                new TestCase(
                        "test_publish_multiple_60kb_messages",
                        clientFactory,
                        Duration.ofSeconds(10),
                        60_000,
                        83,
                        Duration.ZERO,
                        83),
                new TestCase(
                        "test_publish_single_message_over_5mb",
                        clientFactory,
                        Duration.ofSeconds(10),
                        5_123_456,
                        1,
                        Duration.ZERO,
                        1),

                /**
                 * Constantly publish messages over 5mb for period of 1 minute. Expect Subscriber to
                 * receive at least 80 messages
                 */
                new TestCase(
                        "test_throutput",
                        clientFactory,
                        Duration.ofMinutes(1),
                        5_123_456,
                        Integer.MAX_VALUE,
                        Duration.ofMillis(300),
                        80));
    }

    @BeforeAll
    public static void setupAll() {
        XLogger.load("jros2client-test-logging.properties");
    }
}
