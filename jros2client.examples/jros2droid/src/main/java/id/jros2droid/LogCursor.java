package id.jros2droid;

import android.database.AbstractCursor;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class LogCursor extends AbstractCursor {

    @Override
    public String[] getColumnNames() {
        return new String[] {"_id", "message"};
    }

    @Override
    public int getCount() {
        return LogViewHandler.logs.size();
    }

    @Override
    public double getDouble(int arg0) {
        return 0;
    }

    @Override
    public float getFloat(int arg0) {
        return 0;
    }

    @Override
    public int getInt(int arg0) {
        return 0;
    }

    @Override
    public long getLong(int arg0) {
        return 0;
    }

    @Override
    public short getShort(int arg0) {
        return 0;
    }

    @Override
    public String getString(int colId) {
        return switch (colId) {
        case 1 -> LogViewHandler.logs.get(getPosition());
        default -> colId + " " + getPosition();
        };
    }

    @Override
    public boolean isNull(int arg0) {
        return false;
    }

}
