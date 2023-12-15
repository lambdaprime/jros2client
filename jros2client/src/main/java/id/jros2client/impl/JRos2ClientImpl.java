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
import id.jros2client.JRos2ClientConfiguration;
import id.jros2client.impl.rmw.DdsNameMapper;
import id.jros2client.impl.rmw.RmwConstants;
import id.jrosclient.RosVersion;
import id.jrosclient.TopicPublisher;
import id.jrosclient.exceptions.JRosClientException;
import id.jrosmessages.Message;
import id.xfunction.concurrent.flow.TransformPublisher;
import id.xfunction.concurrent.flow.TransformSubscriber;
import id.xfunction.logging.TracingToken;
import id.xfunction.logging.XLogger;
import id.xfunction.util.LazyService;
import java.util.EnumSet;
import java.util.concurrent.Flow.Subscriber;
import pinorobotics.rtpstalk.RtpsTalkClient;

/**
 * Main class of the library which allows to interact with ROS.
 *
 * <p>Each instance of JRosClient acts as a separate ROS node and listens to its own ports.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class JRos2ClientImpl extends LazyService implements JRos2Client {

    private DdsNameMapper rosNameMapper;
    private RtpsTalkClient rtpsTalkClient;
    private TracingToken tracingToken;
    private XLogger logger;
    private MessageUtils messageUtils;
    private JRos2ClientConfiguration config;

    public JRos2ClientImpl(JRos2ClientConfiguration config, ObjectsFactory factory) {
        this.config = config;
        this.messageUtils = factory.createMessageUtils();
        rosNameMapper = factory.createNameMapper();
        rtpsTalkClient = factory.createRtpsTalkClient(config.rtpsTalkConfiguration());
        tracingToken = new TracingToken("" + hashCode());
        logger = XLogger.getLogger(getClass(), tracingToken);
    }

    @Override
    public <M extends Message> void subscribe(
            String topic, Class<M> messageClass, Subscriber<M> subscriber)
            throws JRosClientException {
        startLazy();
        logger.fine("Subscribing to {0} type {1}", topic, messageClass.getName());
        var messageName = rosNameMapper.asFullyQualifiedDdsTypeName(messageClass);
        topic = rosNameMapper.asFullyQualifiedDdsTopicName(topic, messageClass);
        var transformer =
                new TransformSubscriber<>(subscriber, messageUtils.deserializer(messageClass));
        rtpsTalkClient.subscribe(
                topic, messageName, RmwConstants.DEFAULT_SUBSCRIBER_QOS, transformer);
    }

    @Override
    public <M extends Message> void publish(TopicPublisher<M> publisher)
            throws JRosClientException {
        startLazy();
        logger.fine(
                "Publishing to {0} type {1}",
                publisher.getTopic(), publisher.getMessageClass().getName());
        var messageClass = publisher.getMessageClass();
        var messageName = rosNameMapper.asFullyQualifiedDdsTypeName(messageClass);
        var topic = rosNameMapper.asFullyQualifiedDdsTopicName(publisher.getTopic(), messageClass);
        var transformer = new TransformPublisher<>(publisher, messageUtils.serializer());
        rtpsTalkClient.publish(
                topic,
                messageName,
                RmwConstants.DEFAULT_PUBLISHER_QOS,
                RmwConstants.DEFAULT_WRITER_SETTINGS,
                transformer);
    }

    @Override
    public <M extends Message> void unpublish(String topic, Class<M> messageClass)
            throws JRosClientException {
        new UnsupportedOperationException().printStackTrace();
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

    /**
     * @hidden exclude from javadoc
     */
    @Override
    protected void onStart() {
        logger.fine("onStart");
    }

    /**
     * @hidden exclude from javadoc
     */
    @Override
    protected void onClose() {
        logger.fine("Close");
        rtpsTalkClient.close();
    }

    @Override
    public JRos2ClientConfiguration getConfiguration() {
        return config;
    }
}
