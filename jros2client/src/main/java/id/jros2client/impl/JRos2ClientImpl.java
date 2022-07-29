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
package id.jros2client.impl;

import id.jros2client.JRos2Client;
import id.jros2messages.MessageSerializationUtils;
import id.jrosclient.RosVersion;
import id.jrosclient.TopicPublisher;
import id.jrosmessages.Message;
import id.xfunction.concurrent.SameThreadExecutorService;
import id.xfunction.concurrent.flow.TransformProcessor;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.Flow.Subscriber;
import java.util.function.Function;
import pinorobotics.rtpstalk.RtpsTalkClient;
import pinorobotics.rtpstalk.messages.RtpsTalkDataMessage;

/**
 * Main class of the library which allows to interact with ROS.
 *
 * <p>Each instance of JRosClient acts as a separate ROS node and listens to its own ports.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class JRos2ClientImpl implements JRos2Client {

    private DdsNameMapper rosNameMapper;
    private RtpsTalkClient rtpsTalkClient;
    private MessageSerializationUtils serializationUtils;

    public JRos2ClientImpl(ObjectsFactory factory, DdsNameMapper namemapper) {
        rosNameMapper = namemapper;
        rtpsTalkClient = factory.createRtpsTalkClient();
        serializationUtils = factory.createMessageSerializationUtils();
    }

    @Override
    public <M extends Message> void subscribe(
            String topic, Class<M> messageClass, Subscriber<M> subscriber) throws Exception {
        var messageName = rosNameMapper.asFullyQualifiedDdsTypeName(messageClass);
        topic = rosNameMapper.asFullyQualifiedDdsTopicName(topic, messageClass);
        Function<RtpsTalkDataMessage, M> deserializer =
                rtpsMessage -> serializationUtils.read(rtpsMessage.data(), messageClass);
        var transformer =
                new TransformProcessor<>(deserializer, new SameThreadExecutorService(), 1);
        transformer.subscribe(subscriber);
        rtpsTalkClient.subscribe(topic, messageName, transformer);
    }

    @Override
    public <M extends Message> void publish(TopicPublisher<M> publisher) throws Exception {
        var messageClass = publisher.getMessageClass();
        var messageName = rosNameMapper.asFullyQualifiedDdsTypeName(messageClass);
        var topic = rosNameMapper.asFullyQualifiedDdsTopicName(publisher.getTopic(), messageClass);
        Function<M, RtpsTalkDataMessage> serializer =
                rosMessage -> new RtpsTalkDataMessage(serializationUtils.write(rosMessage));
        var transformer = new TransformProcessor<>(serializer, new SameThreadExecutorService(), 1);
        publisher.subscribe(transformer);
        rtpsTalkClient.publish(topic, messageName, transformer);
    }

    @Override
    public <M extends Message> void unpublish(String topic, Class<M> messageClass)
            throws IOException {
        new UnsupportedOperationException().printStackTrace();
    }

    @Override
    public void close() {
        rtpsTalkClient.close();
    }

    @Override
    public EnumSet<RosVersion> getSupportedRosVersion() {
        return EnumSet.of(RosVersion.ROS2);
    }

    @Override
    public boolean hasPublisher(String topic) {
        new UnsupportedOperationException().printStackTrace();
        return false;
    }

    @SuppressWarnings("exports")
    public RtpsTalkClient getRtpsTalkClient() {
        return rtpsTalkClient;
    }
}
