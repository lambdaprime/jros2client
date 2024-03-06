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
import id.jros2messages.sensor_msgs.ImageMessage;
import id.jrosclient.TopicSubscriber;

/**
 * Subscribes to ROS image_publisher node.
 *
 * <p>Start image_publisher:
 *
 * <pre>
 * apt install -y ros-humble-image-publisher
 * ros2 launch image_publisher image_publisher_file.launch.py
 * </pre>
 *
 * @author lambdaprime intid@protonmail.com
 */
public class ImagesSubscriberApp {

    public static void main(String[] args) throws Exception {
        var client = new JRos2ClientFactory().createClient();
        var topicName = "/camera/image_raw";
        // register a new subscriber
        client.subscribe(
                new TopicSubscriber<>(ImageMessage.class, topicName) {
                    @Override
                    public void onNext(ImageMessage item) {
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
