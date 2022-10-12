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
package id.jros2client.impl;

import id.jros2client.impl.rmw.DdsNameMapper;
import id.jros2messages.MessageSerializationUtils;
import id.jrosclient.utils.RosNameUtils;
import pinorobotics.rtpstalk.RtpsTalkClient;
import pinorobotics.rtpstalk.RtpsTalkConfiguration;

/**
 * This factory is a single point for managing all dependencies.
 *
 * @author lambdaprime intid@protonmail.com
 */
public class ObjectsFactory {

    private static ObjectsFactory instance = new ObjectsFactory();

    public RtpsTalkClient createRtpsTalkClient(RtpsTalkConfiguration config) {
        return new RtpsTalkClient(config);
    }

    public static ObjectsFactory getInstance() {
        return instance;
    }

    public static void setInstance(ObjectsFactory objectsFactory) {
        instance = objectsFactory;
    }

    public MessageUtils createMessageUtils() {
        return new MessageUtils(new MessageSerializationUtils());
    }

    public DdsNameMapper createNameMapper() {
        return new DdsNameMapper(new RosNameUtils());
    }
}
