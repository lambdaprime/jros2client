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

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Configuration parameters of JRosClient
 *
 * @author lambdaprime intid@protonmail.com
 */
public class JRos2ClientConfiguration {

    public static final int START_TCP_ROS_SERVER_PORT = 1235;
    public static final int START_NODE_SERVER_PORT = 1234;
    public static final String HOST_NAME = "localhost";

    private static final AtomicInteger nextTcpRosServerPort =
            new AtomicInteger(START_TCP_ROS_SERVER_PORT);
    private static final AtomicInteger nextNodeServerPort =
            new AtomicInteger(START_NODE_SERVER_PORT);

    private int tcpRosServerPort = nextTcpRosServerPort.addAndGet(2);
    private int nodeServerPort = nextNodeServerPort.addAndGet(2);
    private String hostName = HOST_NAME;
    private String callerId = "jrosclient-" + UUID.randomUUID();
    private int maxMessageLoggingLength = -1;

    /**
     * Port for TCPROS.
     *
     * <p>TCPROS is a transport layer responsible for publishing messages.
     *
     * <p>This is a port to which other ROS nodes connect once they subscribe to any topic published
     * through JRosClient.
     *
     * <p>JRosClient by default tries to use any available port starting from {@link
     * START_TCP_ROS_SERVER_PORT}
     */
    public int getTcpRosServerPort() {
        return tcpRosServerPort;
    }

    public void setTcpRosServerPort(int tcpRosServerPort) {
        this.tcpRosServerPort = tcpRosServerPort;
    }

    /**
     * Port for running Node server (XMLRPC server).
     *
     * <p>This server is used to negotiate connections with other ROS nodes and communicate with the
     * Master.
     *
     * <p>JRosClient by default tries to use any available port starting from {@link
     * START_NODE_SERVER_PORT}
     */
    public int getNodeServerPort() {
        return nodeServerPort;
    }

    public void setNodeServerPort(int nodeServerPort) {
        this.nodeServerPort = nodeServerPort;
    }

    /**
     * Name of the host where TCPROS server and Node server will be running on. This host name
     * should belong to the host where jrosclient is used and to which other ROS nodes can
     * communicate.
     *
     * <p>Default value is {@link HOST_NAME}
     */
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    /**
     * Each instance of JRosClient acts as a separate ROS node and has a unique calledId which it
     * reports to other ROS nodes.
     */
    public String getCallerId() {
        return callerId;
    }

    /**
     * Allows to limit length of logged variable length objects.
     *
     * <p>Example of such objects are ROS messages which may contain a lot of data which quickly
     * fill up the logs. Setting maximum length will truncate logging of such objects.
     *
     * <p>Default value is -1 which means that truncation is off.
     */
    public void setMaxMessageLoggingLength(int length) {
        this.maxMessageLoggingLength = length;
    }

    /** @see setMaxMessageLoggingLength */
    public int getMaxMessageLoggingLength() {
        return maxMessageLoggingLength;
    }

    public String getNodeApiUrl() {
        return String.format("http://%s:%d", getHostName(), getNodeServerPort());
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }
}
