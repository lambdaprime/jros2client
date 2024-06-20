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

import id.jros2client.qos.SubscriberQos;
import id.jrosclient.JRosClient;
import id.jrosclient.RosVersion;
import id.jrosclient.TopicPublisher;
import id.jrosclient.TopicSubscriber;
import id.jrosclient.exceptions.JRosClientException;
import id.jrosmessages.Message;
import java.util.EnumSet;
import java.util.concurrent.Flow.Subscriber;

/**
 * Client to ROS2.
 *
 * @see JRos2ClientFactory
 * @author lambdaprime intid@protonmail.com
 */
public interface JRos2Client extends JRosClient {

    JRos2ClientConfiguration getConfiguration();

    /** {@inheritDoc} */
    @Override
    EnumSet<RosVersion> getSupportedRosVersion();

    /**
     * {@inheritDoc}
     *
     * <p>By default uses {@link SubscriberQos#DEFAULT_SUBSCRIBER_QOS}
     */
    @Override
    <M extends Message> void subscribe(
            String topic, Class<M> messageClass, Subscriber<M> subscriber)
            throws JRosClientException;

    /** Subscribe with specific QOS parameters */
    <M extends Message> void subscribe(
            String topic,
            Class<M> messageClass,
            SubscriberQos subscriberQos,
            Subscriber<M> subscriber)
            throws JRosClientException;

    /**
     * {@inheritDoc}
     *
     * <p>By default uses {@link SubscriberQos#DEFAULT_SUBSCRIBER_QOS}
     */
    @Override
    <M extends Message> void subscribe(TopicSubscriber<M> subscriber) throws JRosClientException;

    /** Subscribe with specific QOS parameters */
    <M extends Message> void subscribe(SubscriberQos subscriberQos, TopicSubscriber<M> subscriber)
            throws JRosClientException;

    /** {@inheritDoc} */
    @Override
    <M extends Message> void publish(TopicPublisher<M> publisher) throws JRosClientException;

    /** {@inheritDoc} */
    @Override
    <M extends Message> void unpublish(String topic, Class<M> messageClass)
            throws JRosClientException;

    /** {@inheritDoc} */
    @Override
    boolean hasPublisher(String topic);

    /** {@inheritDoc} */
    @Override
    void close();
}
