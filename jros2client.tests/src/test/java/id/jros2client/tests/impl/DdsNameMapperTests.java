/*
 * Copyright 2022 jrosclient project
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
package id.jros2client.tests.impl;

import id.jros2client.impl.DdsNameMapper;
import id.jrosclient.utils.RosNameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class DdsNameMapperTests {

    @Test
    public void test_subscribe_happy() throws Exception {
        var mapper = new DdsNameMapper(new RosNameUtils());

        Assertions.assertEquals(
                "rq/hello/_action/send_goalRequest",
                mapper.asFullyQualifiedDdsTopicName(
                        "hello", ActionTestMessages.TestActionGoalMessage.class));
        Assertions.assertEquals(
                "rr/hello/_action/get_resultReply",
                mapper.asFullyQualifiedDdsTopicName(
                        "hello", ActionTestMessages.TestActionResultMessage.class));

        Assertions.assertEquals(
                "rq/helloRequest",
                mapper.asFullyQualifiedDdsTopicName(
                        "hello", ServiceTestMessages.TestServiceRequestMessage.class));
        Assertions.assertEquals(
                "rr/helloReply",
                mapper.asFullyQualifiedDdsTopicName(
                        "hello", ServiceTestMessages.TestServiceResponseMessage.class));
    }
}
