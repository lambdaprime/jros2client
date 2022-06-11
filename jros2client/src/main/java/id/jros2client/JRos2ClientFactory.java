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

import id.jros2client.impl.JRos2Client;
import id.jros2client.impl.ObjectsFactory;
import id.jrosclient.JRosClient;

/**
 * Factory methods for {@link JRos2Client}
 *
 * @author lambdaprime intid@protonmail.com
 */
public class JRos2ClientFactory {

    private static final ObjectsFactory objectsFactory = new ObjectsFactory();

    /** Create client with default configuration */
    public JRosClient createJRosClient() {
        return createSpecializedJRos2Client();
    }

    /** Creates client with given configuration */
    public JRosClient createJRosClient(JRos2ClientConfiguration config) {
        return createSpecializedJRos2Client(config);
    }

    /** @hidden visible for testing */
    public JRosClient createJRosClient(
            JRos2ClientConfiguration config, ObjectsFactory objectsFactory) {
        return new JRos2Client(config, objectsFactory);
    }

    /**
     * Create specialized ROS2 client with default configuration.
     *
     * <p>Specialized ROS clients ideally should be avoided since they make your code to rely on
     * specific version of ROS which means that it will not work across all other ROS versions.
     */
    public JRos2Client createSpecializedJRos2Client() {
        return new JRos2Client(objectsFactory.createConfig(), objectsFactory);
    }

    /**
     * Creates specialized ROS2 client with given configuration
     *
     * <p>Specialized ROS clients ideally should be avoided since they make your code to rely on
     * specific version of ROS which means that it will not work across all other ROS versions.
     */
    public JRos2Client createSpecializedJRos2Client(JRos2ClientConfiguration config) {
        return new JRos2Client(config, objectsFactory);
    }
}
