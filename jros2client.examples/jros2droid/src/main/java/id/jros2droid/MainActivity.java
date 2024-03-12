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

import static id.jros2droid.Constants.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import id.jros2client.JRos2ClientConfiguration;
import id.jros2client.JRos2ClientFactory;
import id.jrosclient.TopicSubscriber;
import id.jrosmessages.std_msgs.StringMessage;
import id.xfunction.function.Unchecked;
import id.xfunction.lang.XThread;
import id.xfunction.logging.XLogger;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;
import pinorobotics.rtpstalk.RtpsTalkConfiguration;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MainActivity extends Activity {

    private static final XLogger LOGGER = XLogger.getLogger(MainActivity.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        var logView = (ListView) findViewById(R.id.logView);
        var cursor = new LogCursor();
        logView.setAdapter(
                new SimpleCursorAdapter(
                        this,
                        R.layout.item,
                        cursor,
                        new String[] {"message"},
                        new int[] {R.id.message},
                        CursorAdapter.FLAG_AUTO_REQUERY));
        new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                int prev = cursor.getCount();
                                while (true) {
                                    if (cursor.getCount() != prev) {
                                        logView.post(
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        cursor.requery();
                                                        logView.setSelection(cursor.getCount());
                                                    }
                                                });
                                        prev = cursor.getCount();
                                    }
                                    XThread.sleep(1000);
                                }
                            }
                        })
                .start();
        setupLogging();
        Executors.newSingleThreadExecutor()
                .execute(
                        () -> {
                            run();
                        });
    }

    private void setupLogging() {
        try {
            var outputFolder = getExternalFilesDir(null);
            Log.i(TAG, "Creating folder for log files: " + outputFolder);
            Files.createDirectories(outputFolder.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        var loggingResource = "logging-jros2client.properties";
        var inputStream = this.getClass().getClassLoader().getResourceAsStream(loggingResource);
        if (inputStream == null) {
            Log.i(TAG, "resource not found: " + loggingResource);
            return;
        }
        Unchecked.run(() -> LogManager.getLogManager().readConfiguration(inputStream));
    }

    private void run() {
        var client =
                new JRos2ClientFactory()
                        .createClient(
                                new JRos2ClientConfiguration(
                                        new RtpsTalkConfiguration.Builder()
                                                .builtinEnpointsPort(8012)
                                                .userEndpointsPort(8013)
                                                .build()));
        //        var client = new JRos2ClientFactory().createClient();
        var topicName = "/helloRos";
        // register a new subscriber
        client.subscribe(
                new TopicSubscriber<>(StringMessage.class, topicName) {
                    @Override
                    public void onNext(StringMessage item) {
                        Log.i(TAG, "received " + item);
                        LOGGER.info(item.toString());
                        // request next message
                        getSubscription().get().request(1);
                    }
                });

        // usually we need to close client once we are done
        // but here we keep it open so that subscriber will keep
        // printing messages indefinitely

    }
}
