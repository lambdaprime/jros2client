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

import static pinorobotics.rtpstalk.RtpsTalkConfiguration.Builder.DEFAULT_DISCOVERY_PERIOD;
import static pinorobotics.rtpstalk.RtpsTalkConfiguration.Builder.DEFAULT_HEARTBEAT_PERIOD;

import id.jros2client.JRos2ClientConfiguration;
import id.jros2client.JRos2ClientFactory;
import id.jrosclient.tests.integration.JRosPubSubClientLatencyTests;
import id.opentelemetry.exporters.extensions.ElasticsearchMetricsExtension;
import id.xfunction.logging.XLogger;
import java.time.Duration;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import pinorobotics.rtpstalk.RtpsTalkConfiguration;

@ExtendWith({ElasticsearchMetricsExtension.class})
public class JRos2PubSubClientLatencyTests extends JRosPubSubClientLatencyTests {
    private static final JRos2ClientFactory factory = new JRos2ClientFactory();

    static {
        init(
                new TestCase(
                        "test_latency_small_queue",
                        () -> factory.createClient(),
                        // avoid measuring latency which is caused due to discovery protocol
                        DEFAULT_DISCOVERY_PERIOD.plus(DEFAULT_HEARTBEAT_PERIOD).plusSeconds(1),
                        // how long to run test
                        DEFAULT_DISCOVERY_PERIOD.plusSeconds(20),
                        60_000,
                        DEFAULT_HEARTBEAT_PERIOD.plusMillis(60),
                        1700),
                new TestCase(
                        "test_latency_big_queue",
                        () ->
                                factory.createClient(
                                        new JRos2ClientConfiguration(
                                                new RtpsTalkConfiguration.Builder()
                                                        .historyCacheMaxSize(1_000)
                                                        .build())),
                        // avoid measuring latency which is caused due to discovery protocol
                        DEFAULT_DISCOVERY_PERIOD.plus(DEFAULT_HEARTBEAT_PERIOD).plusSeconds(1),
                        // how long to run test
                        DEFAULT_DISCOVERY_PERIOD.plusSeconds(20),
                        60_000,
                        Duration.ofMillis(800),
                        14_000));
    }

    @BeforeAll
    public static void setupAll() {
        XLogger.load("jros2client-test-logging.properties");
    }
}
