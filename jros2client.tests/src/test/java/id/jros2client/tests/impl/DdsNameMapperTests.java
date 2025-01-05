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

import id.jros2client.impl.rmw.DdsNameMapper;
import id.jroscommon.RosName;
import id.jrosmessages.MessageDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class DdsNameMapperTests {

    @Test
    public void test_asFullyQualifiedDdsTopicName() throws Exception {
        var mapper = new DdsNameMapper();

        Assertions.assertEquals(
                "rq/hello/_action/send_goalRequest",
                mapper.asFullyQualifiedDdsTopicName(
                        new RosName("hello"),
                        new MessageDescriptor<>(ActionTestMessages.TestActionGoalMessage.class)));
        Assertions.assertEquals(
                "rr/hello/_action/get_resultReply",
                mapper.asFullyQualifiedDdsTopicName(
                        new RosName("hello"),
                        new MessageDescriptor<>(ActionTestMessages.TestActionResultMessage.class)));
        Assertions.assertEquals(
                "rq/helloRequest",
                mapper.asFullyQualifiedDdsTopicName(
                        new RosName("hello"),
                        new MessageDescriptor<>(
                                ServiceTestMessages.TestServiceRequestMessage.class)));
        Assertions.assertEquals(
                "rr/helloReply",
                mapper.asFullyQualifiedDdsTopicName(
                        new RosName("hello"),
                        new MessageDescriptor<>(
                                ServiceTestMessages.TestServiceResponseMessage.class)));
    }

    @Test
    public void test_asFullyQualifiedDdsTypeName() throws Exception {
        var mapper = new DdsNameMapper();

        Assertions.assertEquals(
                "test::action::dds_::Test_SendGoal_Request_",
                mapper.asFullyQualifiedDdsTypeName(
                        new MessageDescriptor<>(ActionTestMessages.TestActionGoalMessage.class)));
        Assertions.assertEquals(
                "test::action::dds_::Test_GetResult_Response_",
                mapper.asFullyQualifiedDdsTypeName(
                        new MessageDescriptor<>(ActionTestMessages.TestActionResultMessage.class)));
        Assertions.assertEquals(
                "test::srv::dds_::Test_Request_",
                mapper.asFullyQualifiedDdsTypeName(
                        new MessageDescriptor<>(
                                ServiceTestMessages.TestServiceRequestMessage.class)));
        Assertions.assertEquals(
                "test::srv::dds_::Test_Response_",
                mapper.asFullyQualifiedDdsTypeName(
                        new MessageDescriptor<>(
                                ServiceTestMessages.TestServiceResponseMessage.class)));
    }
}
