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
import id.jros2client.JRos2ClientConfiguration;
import id.jros2client.JRos2ClientFactory;
import id.jrosclient.TopicSubscriber;
import id.jrosmessages.std_msgs.StringMessage;

/**
 * Subscribes to ROS topic
 *
 * @author lambdaprime intid@protonmail.com
 */
public class SubscriberApp {

    public static void main(String[] args) throws Exception {
        var configBuilder = new JRos2ClientConfiguration.Builder();
        // use configBuilder to override default parameters (network interface, RTPS settings etc)
        var client = new JRos2ClientFactory().createClient(configBuilder.build());
        var topicName = "/helloRos";
        // register a new subscriber with default QOS policies
        // users can redefine QOS policies using overloaded version of subscribe() method
        client.subscribe(
                new TopicSubscriber<>(StringMessage.class, topicName) {
                    @Override
                    public void onNext(StringMessage item) {
                        System.out.println(item);
                        // request next message
                        getSubscription().get().request(1);
                    }
                });

        // usually we need to close client once we are done
        // but here we keep it open so that subscriber will keep
        // printing messages indefinitely
    }
}
