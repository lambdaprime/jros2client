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
package io.lambdaprime.jros2droid;

import static io.lambdaprime.jros2droid.Constants.TAG;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This activity checks if all permissions are available and if not it
 * requests user to grant them.
 * 
 * @author lambdaprime intid@protonmail.com
 */
public class RequestPermissionsActivity extends Activity {

    private static final int REQUEST_CODE = 123;

    private Class<? extends Activity> targetActivity;
    private String[] permissions;

    public RequestPermissionsActivity(Class<? extends Activity> targetActivity,
        String[] permissions) {
        this.targetActivity = targetActivity;
        this.permissions = permissions;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkAndRequestPermissions(permissions)) {
            permissionsGranted();
        }
    }

    /**
     * Called before control is passed to target activity
     */
    protected void onPermissionsGranted() {

    }

    /**
     * Checks if app has specified permissions and:
     * - if it does return true
     * - if it does not then return false and async ask user to grant permissions
     * @return true if all permissions are already present
     */
    private boolean checkAndRequestPermissions(String[] permissions) {
        if (Build.VERSION.SDK_INT >= 23)
            return !requestPermissions(permissions);
        return true;
    }

    /**
     * Is called when user granted ALL requested permissions (including overlay permissions)
     */
    private void permissionsGranted() {
        Log.i(TAG, "All permissions are set");
        onPermissionsGranted();
        startActivity(new Intent(this, targetActivity));
        finish();
    }

    private boolean requestPermissions(String[] permissions) {
        List<String> missing = new ArrayList<>();
        for (String permission: permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED)
                missing.add(permission);
        }
        if (missing.isEmpty())
            return false;
        Log.i(TAG, "Missing permissions " + missing);
        requestPermissions(missing.toArray(new String[0]), REQUEST_CODE);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            var denied = 
            IntStream.range(0, grantResults.length).filter(i -> grantResults[i] == PackageManager.PERMISSION_DENIED)
                .mapToObj(i -> permissions[i])
                .toList();
            if (!denied.isEmpty()) {
                Toast.makeText(this, "Setup failed, no permissions granted for: " + denied, Toast.LENGTH_SHORT).show();
            } else {
                    permissionsGranted();
            }
        } else {
            Toast.makeText(this, "Setup failed, wrong request code", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}
