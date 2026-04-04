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
import id.jros2client.Ros2DiscoveryInfo;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscription;

/**
 * Discover new ROS2 topics
 *
 * @author lambdaprime intid@protonmail.com
 */
public class Ros2DiscoveryApp {

    public static void main(String[] args) throws Exception {
        var configBuilder = new JRos2ClientConfiguration.Builder();
        // use configBuilder to override default parameters (network interface, RTPS settings etc)
        var client = new JRos2ClientFactory().createClient(configBuilder.build());
        client.subscribe(
                new Flow.Subscriber<>() {
                    private Subscription subscription;

                    @Override
                    public void onSubscribe(Subscription subscription) {
                        this.subscription = subscription;
                        subscription.request(1);
                    }

                    @Override
                    public void onNext(Ros2DiscoveryInfo item) {
                        System.out.println(item);
                        // request next message
                        subscription.request(1);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onComplete() {}
                });

        // usually we need to close client once we are done
        // but here we keep it open so that subscriber will keep
        // printing new discovered topics
    }
}
