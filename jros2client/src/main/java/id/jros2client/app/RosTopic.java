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
package id.jros2client.app;

import id.jros2client.JRos2ClientConfiguration;
import id.xfunction.cli.ArgumentParsingException;
import id.xfunction.function.Unchecked;
import java.util.LinkedList;
import java.util.List;

/** @author lambdaprime intid@protonmail.com */
public class RosTopic {

    public RosTopic(JRos2ClientConfiguration config) {}

    public void execute(List<String> positionalArgs) {
        var rest = new LinkedList<>(positionalArgs);
        if (rest.isEmpty()) throw new ArgumentParsingException();
        var cmd = rest.removeFirst();
        switch (cmd) {
            case "echo":
                Unchecked.run(() -> echo(rest));
                break;
            case "list":
                Unchecked.run(() -> list());
                break;
            default:
                throw new ArgumentParsingException();
        }
    }

    private void list() throws Exception {
        throw new UnsupportedOperationException();
    }

    private void echo(LinkedList<String> rest) throws Exception {
        throw new UnsupportedOperationException();
    }
}
