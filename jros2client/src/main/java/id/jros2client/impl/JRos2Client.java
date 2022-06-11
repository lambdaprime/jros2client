/*
 * Copyright 2020 jros2client project
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

import id.jros2client.JRos2ClientConfiguration;
import id.jros2messages.MessageSerializationUtils;
import id.jrosclient.JRosClient;
import id.jrosclient.RosVersion;
import id.jrosclient.TopicPublisher;
import id.jrosclient.TopicSubscriber;
import id.jrosclient.utils.RosNameUtils;
import id.jrosmessages.Message;
import id.xfunction.concurrent.SameThreadExecutorService;
import id.xfunction.concurrent.flow.TransformProcessor;
import id.xfunction.logging.XLogger;
import java.io.IOException;
import java.util.EnumSet;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.function.Function;
import java.util.logging.Logger;
import pinorobotics.rtpstalk.RtpsTalkClient;

/**
 * Main class of the library which allows to interact with ROS.
 *
 * <p>Each instance of JRosClient acts as a separate ROS node and listens to its own ports.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class JRos2Client implements JRosClient {

    private static final Ros2NameMapper rosNameMapper = new Ros2NameMapper(new RosNameUtils());

    private final Logger LOGGER = XLogger.getLogger(this);

    private String masterUrl;
    private JRos2ClientConfiguration configuration;
    private RtpsTalkClient rtpsTalkClient;
    private MessageSerializationUtils serializationUtils;

    public JRos2Client(JRos2ClientConfiguration config, ObjectsFactory factory) {
        rtpsTalkClient = factory.createRtpsTalkClient();
        serializationUtils = factory.createMessageSerializationUtils();
        configuration = config;
    }

    @Override
    public <M extends Message> void subscribe(TopicSubscriber<M> subscriber) throws Exception {
        subscribe(subscriber.getTopic(), subscriber.getMessageClass(), subscriber);
    }

    @Override
    public <M extends Message> void subscribe(
            String topic, Class<M> messageClass, Subscriber<M> subscriber) throws Exception {
        var messageName = rosNameMapper.asFullyQualifiedMessageName(messageClass);
        topic = rosNameMapper.asFullyQualifiedTopicName(messageClass, topic);
        Function<byte[], M> deserializer = input -> serializationUtils.read(input, messageClass);
        var transformer =
                new TransformProcessor<>(deserializer, new SameThreadExecutorService(), 1);
        transformer.subscribe(subscriber);
        rtpsTalkClient.subscribe(topic, messageName, transformer);
    }

    @Override
    public <M extends Message> void publish(TopicPublisher<M> publisher) throws Exception {
        publish(publisher.getTopic(), publisher.getMessageClass(), publisher);
    }

    @Override
    public <M extends Message> void publish(
            String topic, Class<M> messageClass, Publisher<M> publisher) throws Exception {
        var messageName = rosNameMapper.asFullyQualifiedMessageName(messageClass);
        topic = rosNameMapper.asFullyQualifiedTopicName(messageClass, topic);
        Function<M, byte[]> serializer = input -> serializationUtils.write(input);
        var transformer = new TransformProcessor<>(serializer, new SameThreadExecutorService(), 1);
        publisher.subscribe(transformer);
        rtpsTalkClient.publish(topic, messageName, transformer);
    }

    @Override
    public void unpublish(String topic) throws IOException {
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
