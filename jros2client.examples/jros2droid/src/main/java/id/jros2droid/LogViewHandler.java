package id.jros2droid;

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
