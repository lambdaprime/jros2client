/*
 * Copyright 2022 jrosclient project
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
package id.jros2client.examples.diagnostics_otel;

import id.jros2client.JRos2ClientConfiguration;
import id.jros2client.JRos2ClientFactory;
import id.jros2messages.diagnostic_msgs.DiagnosticArrayMessage;
import id.jrosclient.TopicSubscriber;
import id.opentelemetry.exporters.ElasticsearchMetricExporter;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.metrics.DoubleGauge;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Demonstrates how to use diagnostic_msgs with jrosclient.
 *
 * @see <a href="http://portal2.atwebpages.com/jrosclient/diagnostics_otel">ROS diagnostics from
 *     Jetson with jrosclient and isaac_ros_jetson_stats</a>
 * @author lambdaprime intid@protonmail.com
 */
public class DiagnosticsOpenTelemetryApp {

    private static Meter meter;
    private static final Map<String, DoubleGauge> gauges = new HashMap<>();
    private static Predicate<String> isMegaBytes = Pattern.compile("\\d+M").asPredicate();
    private static Predicate<String> isGigaBytes = Pattern.compile("\\d+G").asPredicate();

    /** List of Jetson stats we will be sending to OpenTelemetry */
    private static final List<String> isaac_ros_jetson_stats =
            List.of(
                    "/jtop/CPU/jetson_stats/temp/cpu",
                    "/jtop/Board/pwmfan/PWM 0",
                    "/jtop/Board/pwmfan/RPM 0",
                    "/jtop/GPU/jetson_stats/gpu/gpu",
                    "/jtop/GPU/jetson_stats/temp/gpu",
                    "/jtop/GPU/gpu/Used",
                    "/jtop/Memory/ram/Use");

    private static void emitToOpenTelemetry(DiagnosticArrayMessage item) {
        for (var s : item.status) {
            var name = s.name.data;
            for (var pair : s.values) {
                var keyName = pair.key.data;
                var fullName = name + "/" + keyName;
                var gauge = gauges.get(fullName);
                if (gauge == null) continue;
                try {
                    var val = parseMeasurement(pair.value.data);
                    gauge.set(val);
                    System.out.println(fullName + "=" + val);
                } catch (Exception e) {
                    System.err.format(
                            "Could not parse double value from %s: %s", pair, e.getMessage());
                }
            }
        }
    }

    /**
     * isaac_ros_jetson_stats reports different measurements in different units: MB, GB, C
     * (temperature), %. This function converts such measurements to a valid numbers
     *
     * @throws NumberFormatException if conversion fails
     */
    private static double parseMeasurement(String val) {
        if (isMegaBytes.test(val)) {
            // convert MB to bytes
            return Double.parseDouble(val.replace("M", "")) * 1_000_000;
        } else if (isGigaBytes.test(val)) {
            // convert GB to bytes
            return Double.parseDouble(val.replace("G", "")) * 1_000_000_000;
        } else {
            return Double.parseDouble(val.replaceAll("(C|%|RPM)", ""));
        }
    }

    /** Setup OpenTelemetry to send metrics to Elasticsearch */
    private static void setupMetrics() {
        var elasticUrl =
                URI.create(
                        Optional.ofNullable(System.getenv("ELASTIC_URL"))
                                        .orElse("https://127.0.0.1:9200")
                                + "/diagnostics_otel");
        var metricReader =
                PeriodicMetricReader.builder(
                                new ElasticsearchMetricExporter(
                                        elasticUrl, Optional.empty(), Duration.ofSeconds(5), true))
                        .setInterval(Duration.ofSeconds(3))
                        .build();
        var sdkMeterProvider =
                SdkMeterProvider.builder().registerMetricReader(metricReader).build();
        OpenTelemetrySdk.builder().setMeterProvider(sdkMeterProvider).buildAndRegisterGlobal();
    }

    public static void main(String[] args) throws Exception {
        setupMetrics();

        meter = GlobalOpenTelemetry.getMeter(DiagnosticsOpenTelemetryApp.class.getSimpleName());
        isaac_ros_jetson_stats.stream()
                .forEach(stat -> gauges.put(stat, meter.gaugeBuilder(toMetricName(stat)).build()));

        var configBuilder = new JRos2ClientConfiguration.Builder();
        // use configBuilder to override default parameters (network interface, RTPS settings etc)
        var client = new JRos2ClientFactory().createClient(configBuilder.build());
        var topicName = "/diagnostics_agg";

        // register a new subscriber with default QOS policies
        // users can redefine QOS policies using overloaded version of subscribe() method
        client.subscribe(
                new TopicSubscriber<>(DiagnosticArrayMessage.class, topicName) {
                    @Override
                    public void onNext(DiagnosticArrayMessage item) {
                        System.out.println("New item received");
                        try {
                            emitToOpenTelemetry(item);
                        } finally {
                            // request next message
                            getSubscription().get().request(1);
                        }
                    }
                });

        // usually we need to close client once we are done
        // but here we keep it open so that subscriber will keep
        // printing messages indefinitely
    }

    /** Converts Jetson statistics name to OpenTelemetry metric name format. */
    private static String toMetricName(String statName) {
        return statName
                // remove leading "/"
                .substring(1)
                // replace rest of "/"
                .replaceAll("(/|\\s)", ".");
    }
}
