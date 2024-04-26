/*
 * Copyright 2024 jrosclient project
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
package id.jrosdroid;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Filter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class LogViewHandler extends StreamHandler {

    public static List<String> logs = new CopyOnWriteArrayList<String>();

    @Override
    public synchronized void publish(LogRecord record) {
        var levelValue = getLevel().intValue();
        if (record == null) return;
        if (record.getLevel().intValue() < levelValue) return;
        final Filter filter = getFilter();
        if (filter != null && !filter.isLoggable(record)) return;
        logs.add(getFormatter().format(record));
    }
}
