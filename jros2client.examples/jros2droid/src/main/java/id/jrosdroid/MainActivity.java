/*
 * Copyright 2024 jrosclient project
 * 
 * Website: https://github.com/lambdaprime/jrosclient
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

import static id.jrosdroid.Constants.TAG;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import id.jros2client.JRos2ClientConfiguration;
import id.jros2client.JRos2ClientFactory;
import id.jros2droid.R;
import id.jrosclient.JRosClient;
import id.jrosclient.TopicSubmissionPublisher;
import id.jrosclient.TopicSubscriber;
import id.jrosmessages.std_msgs.StringMessage;
import id.xfunction.function.Unchecked;
import id.xfunction.lang.XThread;
import id.xfunction.logging.XLogger;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

/**
 * @author lambdaprime intid@protonmail.com
 */
public class MainActivity extends Activity {

    private static final XLogger LOGGER = XLogger.getLogger(MainActivity.class);
    private JRosClient client = new JRos2ClientFactory().createClient();
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.description) instanceof TextView view) {
            view.setMovementMethod(LinkMovementMethod.getInstance());
        }
        var topicNameView = (EditText) findViewById(R.id.topicName);
        setupLogging();
        var config = new JRos2ClientConfiguration.Builder().build();
        ((Button) findViewById(R.id.subscribe))
                .setOnClickListener(
                        view -> {
                            stop();
                            client = new JRos2ClientFactory().createClient(config);
                            executor = Executors.newSingleThreadExecutor();
                            executor.submit(
                                    () -> {
                                        client.subscribe(
                                                new TopicSubscriber<>(
                                                        StringMessage.class,
                                                        topicNameView.getText().toString()) {
                                                    @Override
                                                    public void onNext(StringMessage item) {
                                                        Log.i(TAG, "received " + item);
                                                        LOGGER.info(item.toString());
                                                        // request next message
                                                        getSubscription().get().request(1);
                                                    }
                                                });
                                    });
                        });
        ((Button) findViewById(R.id.publish))
                .setOnClickListener(
                        view -> {
                            stop();
                            client = new JRos2ClientFactory().createClient(config);
                            var publisher =
                                    new TopicSubmissionPublisher<>(
                                            StringMessage.class,
                                            topicNameView.getText().toString());
                            executor = Executors.newSingleThreadExecutor();
                            executor.submit(
                                    () -> {
                                        // register a new publisher for a new topic with ROS
                                        client.publish(publisher);
                                        while (!executor.isShutdown()) {
                                            var message =
                                                    new StringMessage()
                                                            .withData("Hello from jrosclient");
                                            LOGGER.info(message.toString());
                                            publisher.submit(message);
                                            XThread.sleep(1000);
                                        }
                                    });
                        });
        ((Button) findViewById(R.id.stop)).setOnClickListener(view -> stop());
    }

    private void stop() {
        LOGGER.info("Stopping...");
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // avoid NetworkOnMainThreadException as close may perform network communication
        var future = new CompletableFuture<Void>();
        new Thread(
                        () -> {
                            client.close();
                            future.complete(null);
                        })
                .start();
        Unchecked.run(future::get);
    }

    private void setupLogging() {
        try {
            var outputFolder = getExternalFilesDir(null);
            Log.i(TAG, "Creating folder for log files: " + outputFolder);
            Files.createDirectories(outputFolder.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        var loggingResource = "logging-jrosclient.properties";
        var inputStream = this.getClass().getClassLoader().getResourceAsStream(loggingResource);
        if (inputStream == null) {
            Log.i(TAG, "resource not found: " + loggingResource);
            return;
        }
        Unchecked.run(() -> LogManager.getLogManager().readConfiguration(inputStream));
        var logView = (ListView) findViewById(R.id.logView);
        var cursor = new LogCursor();
        var adapter =
                new SimpleCursorAdapter(
                        this,
                        R.layout.item,
                        cursor,
                        new String[] {"message"},
                        new int[] {R.id.message},
                        CursorAdapter.FLAG_AUTO_REQUERY);
        logView.setAdapter(adapter);
        new Thread(
                        () -> {
                            while (true) {
                                if (cursor.isStale()) {
                                    logView.post(
                                            () -> {
                                                cursor.requery();
                                                logView.setSelection(cursor.getCount());
                                            });
                                }
                                XThread.sleep(1000);
                            }
                        })
                .start();
    }
}
