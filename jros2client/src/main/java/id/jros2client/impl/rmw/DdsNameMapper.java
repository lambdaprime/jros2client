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

    private static final String ACTION_GOAL = "ActionGoal";
    private static final String ACTION_RESULT = "ActionResult";
    private static final String ACTION_GET_RESULT = "ActionGetResult";
    private static final String SERVICE_REQUEST = "ServiceRequest";
    private static final String SERICE_RESPONSE = "ServiceResponse";

    /** Returns DDS topic name */
    public <M extends Message> String asFullyQualifiedDdsTopicName(
            RosName topicName, MessageDescriptor<M> messageDescriptor) {
        var rosAbsoluteTopicName = topicName.toGlobalName();
        var interfaceType = messageDescriptor.readInterfaceType();
        var rosName = messageDescriptor.readName();
        var lastName = rosName.lastName();
        switch (interfaceType) {
            case MESSAGE:
                return "rt" + rosAbsoluteTopicName;
            case ACTION:
                {
                    if (lastName.endsWith(ACTION_GOAL))
                        return "rq" + rosAbsoluteTopicName + "/_action/send_goalRequest";
                    if (lastName.endsWith(ACTION_GET_RESULT))
                        return "rq" + rosAbsoluteTopicName + "/_action/get_resultRequest";
                    if (lastName.endsWith(ACTION_RESULT))
                        return "rr" + rosAbsoluteTopicName + "/_action/get_resultReply";
                    throw new IllegalArgumentException(
                            "Not a valid message name " + lastName + " for an Action");
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
        var lastName = rosName.lastName();
        var path = rosName.path();
        var interfaceType = messageDescriptor.readInterfaceType();
        switch (interfaceType) {
            case MESSAGE:
                return String.format("%s::msg::dds_::%s_", path.get(0), path.get(1));
            case ACTION:
                {
                    if (lastName.endsWith(ACTION_GOAL))
                        lastName = lastName.replace(ACTION_GOAL, "_SendGoal_Request_");
                    else if (lastName.endsWith(ACTION_GET_RESULT))
                        lastName = lastName.replace(ACTION_GET_RESULT, "_GetResult_Request_");
                    else if (lastName.endsWith(ACTION_RESULT))
                        lastName = lastName.replace(ACTION_RESULT, "_GetResult_Response_");
                    else
                        throw new IllegalArgumentException(
                                "Not a valid message name " + lastName + " for an Action");
                    return String.format("%s::action::dds_::%s", path.get(0), lastName);
                }
            case SERVICE:
                {
                    if (lastName.endsWith(SERVICE_REQUEST))
                        lastName = lastName.replace(SERVICE_REQUEST, "_Request_");
                    else if (lastName.endsWith(SERICE_RESPONSE))
                        lastName = lastName.replace(SERICE_RESPONSE, "_Response_");
                    else
                        throw new IllegalArgumentException(
                                "Not a valid message name " + lastName + " for a Service");
                    return String.format("%s::srv::dds_::%s", path.get(0), lastName);
                }
            default:
                throw new UnsupportedOperationException("ROS interface type " + interfaceType);
        }
    }
}
