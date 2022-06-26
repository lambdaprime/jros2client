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
package id.jros2client.impl;

import id.jrosclient.utils.RosNameUtils;
import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadataAccessor;

/**
 * Mapper for ROS2 topic names.
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
    private MessageMetadataAccessor metadataAccessor = new MessageMetadataAccessor();
    private RosNameUtils rosNameUtils;

    public DdsNameMapper(RosNameUtils rosNameUtils) {
        this.rosNameUtils = rosNameUtils;
    }

    /** Returns DDS topic name */
    public <M extends Message> String asFullyQualifiedDdsTopicName(
            Class<M> messageClass, String topicName) {
        var rosAbsoluteTopicName = rosNameUtils.toAbsoluteName(topicName);
        var interfaceType = metadataAccessor.getInterfaceType(messageClass);
        var name = metadataAccessor.getName(messageClass);
        switch (interfaceType) {
            case MESSAGE:
                return "rt" + rosAbsoluteTopicName;
            case ACTION:
                {
                    if (name.endsWith(ACTION_GOAL))
                        return "rq" + rosAbsoluteTopicName + "/_action/send_goalRequest";
                    if (name.endsWith(ACTION_GET_RESULT))
                        return "rq" + rosAbsoluteTopicName + "/_action/get_resultRequest";
                    if (name.endsWith(ACTION_RESULT))
                        return "rr" + rosAbsoluteTopicName + "/_action/get_resultReply";
                    throw new IllegalArgumentException(
                            "Not a valid message name " + name + " for an Action");
                }
            case SERVICE:
                {
                    if (name.endsWith(SERVICE_REQUEST))
                        return "rq" + rosAbsoluteTopicName + "Request";
                    if (name.endsWith(SERICE_RESPONSE))
                        return "rr" + rosAbsoluteTopicName + "Reply";
                    else
                        throw new IllegalArgumentException(
                                "Not a valid message name " + name + " for a Service");
                }
            default:
                throw new UnsupportedOperationException("ROS interface type " + interfaceType);
        }
    }

    /** Returns DDS topic type */
    public <M extends Message> String asFullyQualifiedDdsTypeName(Class<M> messageClass) {
        var path = rosNameUtils.getMessageName(messageClass);
        var name = path.getName(1).toString();
        var interfaceType = metadataAccessor.getInterfaceType(messageClass);
        switch (interfaceType) {
            case MESSAGE:
                return String.format("%s::msg::dds_::%s_", path.getName(0), path.getName(1));
            case ACTION:
                {
                    if (name.endsWith(ACTION_GOAL))
                        name = name.replace(ACTION_GOAL, "_SendGoal_Request_");
                    else if (name.endsWith(ACTION_GET_RESULT))
                        name = name.replace(ACTION_GET_RESULT, "_GetResult_Request_");
                    else if (name.endsWith(ACTION_RESULT))
                        name = name.replace(ACTION_RESULT, "_GetResult_Response_");
                    else
                        throw new IllegalArgumentException(
                                "Not a valid message name " + name + " for an Action");
                    return String.format("%s::action::dds_::%s", path.getName(0), name);
                }
            case SERVICE:
                {
                    if (name.endsWith(SERVICE_REQUEST))
                        name = name.replace(SERVICE_REQUEST, "_Request_");
                    else if (name.endsWith(SERICE_RESPONSE))
                        name = name.replace(SERICE_RESPONSE, "_Response_");
                    else
                        throw new IllegalArgumentException(
                                "Not a valid message name " + name + " for a Service");
                    return String.format("%s::srv::dds_::%s", path.getName(0), name);
                }
            default:
                throw new UnsupportedOperationException("ROS interface type " + interfaceType);
        }
    }
}
