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
package id.jros2client;

import id.jros2client.qos.PublisherQos;
import id.jros2client.qos.SubscriberQos;
import id.jrosclient.JRosClient;
import id.jrosclient.RosVersion;
import id.jrosclient.TopicPublisher;
import id.jrosclient.TopicSubscriber;
import id.jrosclient.exceptions.JRosClientException;
import id.jroscommon.RosName;
import id.jrosmessages.Message;
import id.jrosmessages.MessageDescriptor;
import java.util.EnumSet;
import java.util.concurrent.Flow.Subscriber;

/**
 * ROS2 client.
 *
 * @see JRos2ClientFactory
 * @author lambdaprime intid@protonmail.com
 */
public interface JRos2Client extends JRosClient {

    JRos2ClientConfiguration getConfiguration();

    /** Subscribe to ROS topic with specific QOS parameters */
    <M extends Message> void subscribe(
            RosName topic,
            MessageDescriptor<M> messageDescriptor,
            SubscriberQos subscriberQos,
            Subscriber<M> subscriber)
            throws JRosClientException;

    /** Publish to ROS topic with specific QOS parameters */
    <M extends Message> void publish(PublisherQos publisherQos, TopicPublisher<M> publisher)
            throws JRosClientException;

    /** Subscribe to ROS topic with specific QOS parameters */
    default <M extends Message> void subscribe(
            SubscriberQos subscriberQos, TopicSubscriber<M> subscriber) throws JRosClientException {
        subscribe(
                subscriber.getTopic(),
                subscriber.getMessageDescriptor(),
                subscriberQos,
                subscriber);
    }

    /** {@inheritDoc} */
    @Override
    EnumSet<RosVersion> getSupportedRosVersion();

    /**
     * {@inheritDoc}
     *
     * <p>By default uses {@link SubscriberQos#DEFAULT_SUBSCRIBER_QOS}
     */
    @Override
    default <M extends Message> void subscribe(
            RosName topic, MessageDescriptor<M> messageDescriptor, Subscriber<M> subscriber)
            throws JRosClientException {
        subscribe(topic, messageDescriptor, SubscriberQos.DEFAULT_SUBSCRIBER_QOS, subscriber);
    }

    /**
     * {@inheritDoc}
     *
     * <p>By default uses {@link SubscriberQos#DEFAULT_SUBSCRIBER_QOS}
     */
    @Override
    default <M extends Message> void subscribe(TopicSubscriber<M> subscriber)
            throws JRosClientException {
        subscribe(SubscriberQos.DEFAULT_SUBSCRIBER_QOS, subscriber);
    }

    /**
     * Subscribe to notifications about all ROS2 information about publishers and subscribers that
     * have been discovered by the <b>jros2client</b>.
     */
    void subscribe(Subscriber<Ros2DiscoveryInfo> subscriber);

    /**
     * {@inheritDoc}
     *
     * <p>Simplified version of {@link #publish(PublisherQos, TopicPublisher)} with {@link
     * PublisherQos#DEFAULT_PUBLISHER_QOS}
     */
    @Override
    default <M extends Message> void publish(TopicPublisher<M> publisher)
            throws JRosClientException {
        publish(PublisherQos.DEFAULT_PUBLISHER_QOS, publisher);
    }

    /** {@inheritDoc} */
    @Override
    <M extends Message> void unpublish(RosName topic, MessageDescriptor<M> messageDescriptor)
            throws JRosClientException;

    /** {@inheritDoc} */
    @Override
    boolean hasPublisher(RosName topic);

    /** {@inheritDoc} */
    @Override
    void close();
}
