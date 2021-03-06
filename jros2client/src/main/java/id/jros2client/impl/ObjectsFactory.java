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
package id.jros2client.impl;

import id.jros2client.JRos2ClientConfiguration;
import id.jros2messages.MessageSerializationUtils;
import pinorobotics.rtpstalk.RtpsTalkClient;

/** This factory is a single point for managing all dependencies. */
public class ObjectsFactory {

    private static ObjectsFactory instance = new ObjectsFactory();

    public JRos2ClientConfiguration createConfig() {
        return new JRos2ClientConfiguration();
    }

    public RtpsTalkClient createRtpsTalkClient() {
        return new RtpsTalkClient();
    }

    public static ObjectsFactory getInstance() {
        return instance;
    }

    public static void setInstance(ObjectsFactory objectsFactory) {
        instance = objectsFactory;
    }

    public MessageSerializationUtils createMessageSerializationUtils() {
        return new MessageSerializationUtils();
    }
}
