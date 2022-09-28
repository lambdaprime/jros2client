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
package id.jros2client;

import id.jros2client.impl.DdsNameMapper;
import id.jros2client.impl.JRos2ClientImpl;
import id.jros2client.impl.ObjectsFactory;
import id.jrosclient.utils.RosNameUtils;

/**
 * Factory methods for {@link JRos2Client}
 *
 * @author lambdaprime intid@protonmail.com
 */
public class JRos2ClientFactory {

    private static final ObjectsFactory OBJECTS_FACTORY = new ObjectsFactory();
    private static final DdsNameMapper NAME_MAPPER = new DdsNameMapper(new RosNameUtils());

    /** Create client with default configuration */
    public JRos2Client createClient() {
        return createClient(new JRos2ClientConfiguration.Builder().build());
    }

    public JRos2Client createClient(JRos2ClientConfiguration config) {
        return new JRos2ClientImpl(config, OBJECTS_FACTORY, NAME_MAPPER);
    }
}
