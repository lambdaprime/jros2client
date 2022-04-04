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
package id.jros2client;

import id.jros2client.impl.ObjectsFactory;
import id.jros2client.impl.Ros2NameUtils;
import id.jrosclient.core.JRosClient;
import id.jrosclient.core.RosVersion;
import id.jrosclient.core.TopicPublisher;
import id.jrosclient.core.TopicSubscriber;
import id.jrosclient.core.utils.RosNameUtils;
import id.jrosclient.core.utils.TextUtils;
import id.jrosmessages.Message;
import id.jrosmessages.MessageSerializationUtils;
import id.jrosmessages.MetadataAccessor;
import id.xfunction.concurrent.SameThreadExecutorService;
import id.xfunction.concurrent.flow.TransformProcessor;
import id.xfunction.logging.XLogger;
import java.io.IOException;
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

    private static final ObjectsFactory objectsFactory = new ObjectsFactory();
    private static final Ros2NameUtils rosNameUtils = new Ros2NameUtils(new RosNameUtils());

    private final Logger LOGGER = XLogger.getLogger(this);

    private String masterUrl;
    private MetadataAccessor metadataAccessor = new MetadataAccessor();
    private JRos2ClientConfiguration configuration;
    private TextUtils textUtils;
    private RtpsTalkClient rtpsTalkClient;
    private MessageSerializationUtils serializationUtils;

    /** Default constructor which creates a client with default configuration */
    public JRos2Client() {
        this(objectsFactory.createConfig());
    }

    /** Constructor which creates a client with given client configuration */
    public JRos2Client(JRos2ClientConfiguration config) {
        this(config, objectsFactory);
    }

    /** @hidden visible for testing */
    public JRos2Client(JRos2ClientConfiguration config, ObjectsFactory factory) {
        textUtils = factory.createTextUtils(config);
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
        var topicType = rosNameUtils.toDdsMessageType(metadataAccessor.getType(messageClass));
        topic = rosNameUtils.toDdsTopic(topic);
        Function<byte[], M> deserializer = input -> serializationUtils.read(input, messageClass);
        var transformer =
                new TransformProcessor<>(deserializer, new SameThreadExecutorService(), 1);
        transformer.subscribe(subscriber);
        rtpsTalkClient.subscribe(topic, topicType, transformer);
    }

    @Override
    public <M extends Message> void publish(TopicPublisher<M> publisher) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public <M extends Message> void publish(
            String topic, Class<M> messageClass, Publisher<M> publisher) throws Exception {
        throw new UnsupportedOperationException();
    }

    @Override
    public void unpublish(String topic) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() {
        rtpsTalkClient.close();
    }

    @Override
    public RosVersion getSupportedRosVersion() {
        return RosVersion.ROS2;
    }
}
