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
package id.jros2client.impl.rmw;

import id.jroscommon.RosName;
import id.jrosmessages.Message;
import id.jrosmessages.MessageDescriptor;

/**
 * Mapper of ROS2 names (topics, types) to DDS names.
 *
 * <p>ROS naming differs between ROS versions on top of that it differs between the ROS topics,
 * services, actions, etc.
 *
 * <p>Ideally DDS logic should not be exposed from <b>jros2client</b> to its consumers (including to
 * jros2services, jros2actionlib, etc). This would allow those consumers to share ROS version
 * agnostic code in form of depending to <b>jrosclient</b>. For that purpose DDS mapping moved to
 * single place here.
 *
 * @see <a href="https://design.ros2.org/articles/topic_and_service_names.html">Topic and Service
 *     name mapping to DDS</a>
 * @author lambdaprime intid@protonmail.com
 */
public class DdsNameMapper {

    private static final String ACTION_SEND_GOAL_REQUEST = "ActionGoal";
    private static final String ACTION_SEND_GOAL_RESPONSE = "ActionGoalResult";
    private static final String ACTION_GET_RESULT_REQUEST = "ActionGetResult";
    private static final String ACTION_GET_RESULT_RESPONSE = "ActionResult";
    private static final String ACTION_FEEDBACK = "ActionGoalFeedback";
    private static final String ACTION_MSGS_CANCEL_GOAL_REQUEST =
            "action_msgs/CancelGoalServiceRequest";
    private static final String ACTION_MSGS_CANCEL_GOAL_RESPONSE =
            "action_msgs/CancelGoalServiceResponse";
    private static final String ACTION_MSGS_STATUS = "action_msgs/GoalStatusArray";
    private static final String SERVICE_REQUEST = "ServiceRequest";
    private static final String SERICE_RESPONSE = "ServiceResponse";

    /** Returns DDS topic name */
    public <M extends Message> String asFullyQualifiedDdsTopicName(
            RosName topicName, MessageDescriptor<M> messageDescriptor) {
        var rosAbsoluteTopicName = topicName.toGlobalName();
        var interfaceType = messageDescriptor.readInterfaceType();
        var rosName = messageDescriptor.readName();
        var fullname = rosName.toString();
        var lastName = rosName.lastName();
        switch (interfaceType) {
            case MESSAGE:
                return "rt" + rosAbsoluteTopicName;
            case ACTION:
                {
                    return switch (fullname) {
                        case ACTION_MSGS_CANCEL_GOAL_REQUEST ->
                                "rq" + rosAbsoluteTopicName + "/_action/cancel_goalRequest";
                        case ACTION_MSGS_CANCEL_GOAL_RESPONSE ->
                                "rr" + rosAbsoluteTopicName + "/_action/cancel_goalReply";
                        case ACTION_MSGS_STATUS -> "rt" + rosAbsoluteTopicName + "/_action/status";
                        default -> {
                            if (lastName.endsWith(ACTION_SEND_GOAL_REQUEST))
                                yield "rq" + rosAbsoluteTopicName + "/_action/send_goalRequest";
                            else if (lastName.endsWith(ACTION_SEND_GOAL_RESPONSE))
                                yield "rr" + rosAbsoluteTopicName + "/_action/send_goalReply";
                            else if (lastName.endsWith(ACTION_GET_RESULT_REQUEST))
                                yield "rq" + rosAbsoluteTopicName + "/_action/get_resultRequest";
                            else if (lastName.endsWith(ACTION_GET_RESULT_RESPONSE))
                                yield "rr" + rosAbsoluteTopicName + "/_action/get_resultReply";
                            else if (lastName.endsWith(ACTION_FEEDBACK))
                                yield "rt" + rosAbsoluteTopicName + "/_action/feedback";
                            throw new IllegalArgumentException(
                                    "Not a valid message name " + lastName + " for an Action");
                        }
                    };
                }
            case SERVICE:
                {
                    if (lastName.endsWith(SERVICE_REQUEST))
                        return "rq" + rosAbsoluteTopicName + "Request";
                    if (lastName.endsWith(SERICE_RESPONSE))
                        return "rr" + rosAbsoluteTopicName + "Reply";
                    else
                        throw new IllegalArgumentException(
                                "Not a valid message name " + lastName + " for a Service");
                }
            default:
                throw new UnsupportedOperationException("ROS interface type " + interfaceType);
        }
    }

    /** Returns DDS topic type */
    public <M extends Message> String asFullyQualifiedDdsTypeName(
            MessageDescriptor<M> messageDescriptor) {
        var rosName = messageDescriptor.readName();
        var fullname = rosName.toString();
        var lastName = rosName.lastName();
        var path = rosName.path();
        var interfaceType = messageDescriptor.readInterfaceType();
        switch (interfaceType) {
            case MESSAGE:
                return String.format("%s::msg::dds_::%s_", path.get(0), path.get(1));
            case ACTION:
                {
                    return switch (fullname) {
                        case ACTION_MSGS_CANCEL_GOAL_REQUEST ->
                                "action_msgs::srv::dds_::CancelGoal_Request_";
                        case ACTION_MSGS_CANCEL_GOAL_RESPONSE ->
                                "action_msgs::srv::dds_::CancelGoal_Response_";
                        case ACTION_MSGS_STATUS -> "action_msgs::msg::dds_::GoalStatusArray_";
                        default -> {
                            if (lastName.endsWith(ACTION_SEND_GOAL_REQUEST))
                                lastName =
                                        lastName.replace(
                                                ACTION_SEND_GOAL_REQUEST, "_SendGoal_Request");
                            else if (lastName.endsWith(ACTION_SEND_GOAL_RESPONSE))
                                lastName =
                                        lastName.replace(
                                                ACTION_SEND_GOAL_RESPONSE, "_SendGoal_Response");
                            else if (lastName.endsWith(ACTION_GET_RESULT_REQUEST))
                                lastName =
                                        lastName.replace(
                                                ACTION_GET_RESULT_REQUEST, "_GetResult_Request");
                            else if (lastName.endsWith(ACTION_GET_RESULT_RESPONSE))
                                lastName =
                                        lastName.replace(
                                                ACTION_GET_RESULT_RESPONSE, "_GetResult_Response");
                            else if (lastName.endsWith(ACTION_FEEDBACK))
                                lastName = lastName.replace(ACTION_FEEDBACK, "_FeedbackMessage");
                            else
                                throw new IllegalArgumentException(
                                        "Not a valid message name " + lastName + " for an Action");
                            yield String.format("%s::action::dds_::%s_", path.get(0), lastName);
                        }
                    };
                }
            case SERVICE:
                {
                    if (lastName.endsWith(SERVICE_REQUEST))
                        lastName = lastName.replace(SERVICE_REQUEST, "_Request");
                    else if (lastName.endsWith(SERICE_RESPONSE))
                        lastName = lastName.replace(SERICE_RESPONSE, "_Response");
                    else
                        throw new IllegalArgumentException(
                                "Not a valid message name " + lastName + " for a Service");
                    return String.format("%s::srv::dds_::%s_", path.get(0), lastName);
                }
            default:
                throw new UnsupportedOperationException("ROS interface type " + interfaceType);
        }
    }
}
