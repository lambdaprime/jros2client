/*
 * Copyright 2022 jrosclient project
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
package id.jros2client.tests.integration;

import id.xfunction.lang.XExec;
import id.xfunction.lang.XProcess;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class Ros2Commands implements AutoCloseable {

    private List<XProcess> procs = new ArrayList<>();
    private Map<String, String> env;
    private RmwImplementation rmw;

    public Ros2Commands(RmwImplementation rmw) {
        this.rmw = rmw;
        this.env =
                switch (rmw) {
                    case FASTDDS -> Map.of("RMW_IMPLEMENTATION", "rmw_fastrtps_cpp");
                    case CYCLONEDDS -> Map.of("RMW_IMPLEMENTATION", "rmw_cyclonedds_cpp");
                };
    }

    public XProcess runTalker() {
        var proc =
                new XExec("ros2 run demo_nodes_cpp talker").withEnvironmentVariables(env).start();
        procs.add(proc);
        return proc;
    }

    /**
     * Listener with ROS2 default QoS profile (keeps only last 10 messages)
     * https://docs.ros.org/en/rolling/Concepts/About-Quality-of-Service-Settings.html
     */
    public XProcess runListener() {
        var proc =
                new XExec("ros2 run demo_nodes_cpp listener").withEnvironmentVariables(env).start();
        procs.add(proc);
        return proc;
    }

    public XProcess runRqt() {
        var proc = new XExec("rqt").withEnvironmentVariables(env).start();
        procs.add(proc);
        return proc;
    }

    @Override
    public void close() {
        procs.forEach(XProcess::destroyAllForcibly);
    }

    @Override
    public String toString() {
        return rmw.toString();
    }
}
