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
import id.jros2client.JRos2ClientFactory;
import id.jrosclient.TopicSubscriber;
import id.jrosmessages.Message;
import id.jrosmessages.MessageMetadata;
import id.xfunction.cli.CommandLineInterface;
import java.util.Arrays;

/**
 * In Java <a
 * href="https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Character.html#SIZE">size
 * of char is 2 bytes</a> whereas in ROS2 it is <a
 * href="https://docs.ros.org/en/rolling/Concepts/Basic/About-Interfaces.html#field-types">the same
 * as in C++</a> and <a href="https://en.cppreference.com/w/cpp/language/types#Properties">equals to
 * 1 byte</a>. To prevent possible data loss when converting Java char (2 bytes) to ROS char (1
 * byte), jros2client maps both ROS built-in types "byte" and "char" into single Java primitive type
 * "byte"
 *
 * <p>This example demonstrates how ROS message "custom_msgs/CharMessage" with "char" fields suppose
 * to be mapped into jrosclient message {@link CharMessage}
 *
 * <p>Use this example together with <a
 * href="https://github.com/lambdaprime/jros2client/tree/main/jros2client.examples/generic/ws2/src/custom_msgs">custom_msgs
 * node</a>.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class CharMessageSubscriberApp {

    public static void main(String[] args) throws Exception {
        var cli = new CommandLineInterface();
        String topic = "/charMessage";
        var client = new JRos2ClientFactory().createClient();
        // register a new subscriber
        client.subscribe(
                new TopicSubscriber<>(CharMessage.class, topic) {
                    @Override
                    public void onNext(CharMessage item) {
                        System.out.println(item);
                        // request next message
                        getSubscription().get().request(1);
                    }
                });

        // usually we need to close client once we are done
        // but here we keep it open so that subscriber will keep
        // printing messages indefinitely
    }

    /** Example of custom message definition */
    @MessageMetadata(
            name = CharMessage.NAME,
            fields = {"letter", "data"})
    public static class CharMessage implements Message {

        static final String NAME = "custom_msgs/CharMessage";

        public byte letter;

        public byte[] data = new byte[0];

        @Override
        public String toString() {
            return String.format("letter: %s, data: %s", letter, Arrays.toString(data));
        }
    }
}
