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
