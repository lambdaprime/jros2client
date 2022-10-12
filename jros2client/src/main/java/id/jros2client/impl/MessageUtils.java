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

import id.jros2messages.MessageSerializationUtils;
import id.jrosmessages.Message;
import id.xfunction.logging.XLogger;
import java.util.Optional;
import java.util.function.Function;
import pinorobotics.rtpstalk.messages.RtpsTalkDataMessage;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MessageUtils {

    private static final XLogger LOGGER = XLogger.getLogger(MessageUtils.class);
    private MessageSerializationUtils serializationUtils;

    public MessageUtils(MessageSerializationUtils serializationUtils) {
        this.serializationUtils = serializationUtils;
    }

    public <M extends Message> Function<M, Optional<RtpsTalkDataMessage>> serializer() {
        return rosMessage ->
                Optional.of(new RtpsTalkDataMessage(serializationUtils.write(rosMessage)));
    }

    public <M extends Message> Function<RtpsTalkDataMessage, Optional<M>> deserializer(
            Class<M> messageClass) {
        return rtpsMessage -> {
            var data = rtpsMessage.data().orElse(null);
            if (data == null) {
                LOGGER.warning("Received empty RTPS Data message, ignoring");
                return Optional.empty();
            }
            return Optional.of(serializationUtils.read(data, messageClass));
        };
    }
}
