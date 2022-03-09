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

import java.util.Properties;

/** @author lambdaprime intid@protonmail.com */
public class Settings {

    private static final Settings instance = new Settings();

    private int awaitTcpRosClientInSecs;

    public Settings() {
        update(System.getProperties());
    }

    public static Settings getInstance() {
        return instance;
    }

    public int getAwaitTcpRosClientInSecs() {
        return awaitTcpRosClientInSecs;
    }

    @Override
    public String toString() {
        var buf = new StringBuilder();
        buf.append("awaitTcpRosClientInSecs: " + awaitTcpRosClientInSecs + "\n");
        return buf.toString();
    }

    public void update(Properties properties) {
        awaitTcpRosClientInSecs =
                Integer.parseInt(properties.getProperty("awaitTcpRosClientInSecs", "5"));
    }
}
