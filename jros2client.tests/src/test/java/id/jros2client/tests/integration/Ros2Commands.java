/*
 * Copyright 2022 jros2client project
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

/** @author lambdaprime intid@protonmail.com */
public class Ros2Commands implements AutoCloseable {

    private List<XProcess> procs = new ArrayList<>();

    public XProcess runTalker() {
        var proc = new XExec("ros2 run demo_nodes_cpp talker").run();
        procs.add(proc);
        return proc;
    }

    public XProcess runListener() {
        var proc = new XExec("ros2 run demo_nodes_cpp listener").run();
        procs.add(proc);
        return proc;
    }

    @Override
    public void close() {
        procs.forEach(XProcess::destroyAllForcibly);
    }
}
