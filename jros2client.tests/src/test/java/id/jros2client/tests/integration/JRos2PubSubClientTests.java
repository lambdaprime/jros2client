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

import id.jros2client.JRos2ClientConfiguration;
import id.jros2client.JRos2ClientFactory;
import id.jros2client.tests.MetricsExtension;
import id.jrosclient.JRosClient;
import id.jrosclient.tests.integration.JRosPubSubClientTests;
import id.xfunction.logging.XLogger;
import java.util.function.Supplier;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test that Publisher and Subscriber of JRos2Client can interact with each other.
 *
 * @author lambdaprime intid@protonmail.com
 */
@ExtendWith({MetricsExtension.class})
public class JRos2PubSubClientTests extends JRosPubSubClientTests {

    private static final JRos2ClientFactory factory = new JRos2ClientFactory();

    static {
        Supplier<JRosClient> clientFactory = factory::createClient;
        var defaultConfig = new JRos2ClientConfiguration.Builder().build();
        init(
                new TestCase(
                        "test_jros2client",
                        clientFactory,
                        defaultConfig
                                .rtpsTalkConfiguration()
                                .spdpDiscoveredParticipantDataPublishPeriod(),
                        defaultConfig.rtpsTalkConfiguration().historyCacheMaxSize()));
    }

    @BeforeAll
    public static void setupAll() {
        XLogger.load("jros2client-test-logging.properties");
    }
}
