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

import static org.junit.jupiter.params.provider.Arguments.arguments;

import id.jros2client.impl.rmw.DdsNameMapper;
import id.jroscommon.RosName;
import id.jrosmessages.MessageDescriptor;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class DdsNameMapperTests {

    private static Stream<Arguments> provideFullyQualifiedDdsTopicNameArguments() {
        return Stream.of(
                arguments(
                        new MessageDescriptor<>(ActionTestMessages.TestActionGoalMessage.class),
                        "rq/hello/_action/send_goalRequest"),
                arguments(
                        new MessageDescriptor<>(ActionTestMessages.TestActionResultMessage.class),
                        "rr/hello/_action/get_resultReply"),
                arguments(
                        new MessageDescriptor<>(
                                ActionTestMessages.TestActionGoalRequestMessage.class),
                        "rq/hello/_action/cancel_goalRequest"),
                arguments(
                        new MessageDescriptor<>(ActionTestMessages.TestActionStatusMessage.class),
                        "rt/hello/_action/status"),
                arguments(
                        new MessageDescriptor<>(ActionTestMessages.TestActionFeedbackMessage.class),
                        "rt/hello/_action/feedback"),
                arguments(
                        new MessageDescriptor<>(
                                ServiceTestMessages.TestServiceRequestMessage.class),
                        "rq/helloRequest"),
                arguments(
                        new MessageDescriptor<>(
                                ServiceTestMessages.TestServiceResponseMessage.class),
                        "rr/helloReply"));
    }

    @ParameterizedTest
    @MethodSource("provideFullyQualifiedDdsTopicNameArguments")
    void test_topicNames(MessageDescriptor<?> descriptor, String expected) {
        var mapper = new DdsNameMapper();
        var name = new RosName("hello");
        Assertions.assertEquals(expected, mapper.asFullyQualifiedDdsTopicName(name, descriptor));

        Assertions.assertEquals(name, mapper.toRosTopicName(expected));
    }

    private static Stream<Arguments> provideFullyQualifiedDdsTypeNameArguments() {
        return Stream.of(
                arguments(
                        new MessageDescriptor<>(ActionTestMessages.TestActionGoalMessage.class),
                        "test::action::dds_::Test_SendGoal_Request_"),
                arguments(
                        new MessageDescriptor<>(ActionTestMessages.TestActionResultMessage.class),
                        "test::action::dds_::Test_GetResult_Response_"),
                arguments(
                        new MessageDescriptor<>(
                                ActionTestMessages.TestActionGoalRequestMessage.class),
                        "action_msgs::srv::dds_::CancelGoal_Request_"),
                arguments(
                        new MessageDescriptor<>(ActionTestMessages.TestActionStatusMessage.class),
                        "action_msgs::msg::dds_::GoalStatusArray_"),
                arguments(
                        new MessageDescriptor<>(ActionTestMessages.TestActionFeedbackMessage.class),
                        "test::action::dds_::Test_FeedbackMessage_"),
                arguments(
                        new MessageDescriptor<>(
                                ServiceTestMessages.TestServiceRequestMessage.class),
                        "test::srv::dds_::Test_Request_"),
                arguments(
                        new MessageDescriptor<>(
                                ServiceTestMessages.TestServiceResponseMessage.class),
                        "test::srv::dds_::Test_Response_"));
    }

    @ParameterizedTest
    @MethodSource("provideFullyQualifiedDdsTypeNameArguments")
    void test_typeNames(MessageDescriptor<?> descriptor, String expected) {
        var mapper = new DdsNameMapper();
        Assertions.assertEquals(expected, mapper.asFullyQualifiedDdsTypeName(descriptor));
        var messageMetadata = mapper.toMessageMetadata(expected);
        Assertions.assertEquals(descriptor.readInterfaceType(), messageMetadata.interfaceType());
        Assertions.assertEquals(descriptor.readName(), messageMetadata.name());
    }
}
