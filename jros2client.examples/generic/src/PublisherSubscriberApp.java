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
import id.jros2client.JRos2Client;
import id.jros2client.JRos2ClientFactory;
import id.jrosclient.JRosClient;
import id.jrosclient.TopicSubmissionPublisher;
import id.jrosclient.TopicSubscriber;
import id.jrosmessages.std_msgs.StringMessage;
import id.xfunction.cli.CommandLineInterface;

/**
 * Example covers how to use jrosclient for Subscriber and Publisher to same topic inside single
 * application.
 *
 * <p>Each instance of {@link JRos2Client} creates a new RTPS participant, which starts to listen
 * certain RTPS ports. Currently {@link JRos2Client} does not allow same RTPS participant to publish
 * and subscribe to same topic at the same time. Instead, both Subscriber and Publisher need to run
 * under different participants. To achieve this, two separate instances of {@link JRos2Client} can
 * be used as shown in this example.
 *
 * <p>By default, subscriber always runs on its own thread after it was subscribed with {@link
 * JRosClient#subscribe(String, Class, java.util.concurrent.Flow.Subscriber)}. Which means, it is
 * possible on same thread run Publisher and then register Subscriber as shown in the example.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class PublisherSubscriberApp {

    public static void main(String[] args) throws Exception {
        var cli = new CommandLineInterface();
        try (var pubClient = new JRos2ClientFactory().createClient();
                var subClient = new JRos2ClientFactory().createClient()) {
            String topicName = "/helloRos";
            var publisher = new TopicSubmissionPublisher<>(StringMessage.class, topicName);
            // register a new publisher for a new topic with ROS
            pubClient.publish(publisher);
            // register a new subscriber
            subClient.subscribe(
                    new TopicSubscriber<>(StringMessage.class, topicName) {
                        @Override
                        public void onNext(StringMessage item) {
                            System.out.println(item);
                            // request next message
                            getSubscription().get().request(1);
                        }
                    });
            while (!cli.wasEnterKeyPressed()) {
                publisher.submit(new StringMessage().withData("Hello ROS"));
                System.out.println("Published");
                Thread.sleep(1000);
            }
        }
    }
}
