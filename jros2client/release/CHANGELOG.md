# Version 10

- Fixing issue with RPM units parsing
- Adding diagnostics_otel
- Issue pinorobotics/jros2services#1 Fixing processing of empty messages

[jros2client-v10.0.zip](https://github.com/lambdaprime/jros2client/raw/main/jros2client/release/jros2client-v10.0.zip)

# Version 9

- Support for Jazzy
- Issue #12 Allow users to specify subscriber QOS parameters

[jros2client-v9.0.zip](https://github.com/lambdaprime/jros2client/raw/main/jros2client/release/jros2client-v9.0.zip)

# Version 8

- Updating to jros2messages v8 and rtpstalk v8

[jros2client-v8.0.zip](https://github.com/lambdaprime/jros2client/raw/main/jros2client/release/jros2client-v8.0.zip)

# Version 7

- Syncup jros2droid with jros1droid
- Renaming jros2droid package
- Updating dependencies
- Updating dependency libraries
- Add JRos2ClientConfiguration into examples
- Updating gradle files
- Include jros2droid APK in release archive
- Adding publish, subscribe functionality to jros2droid
- Adding spotless to jros2droid
- Add "fields" parameter to examples

[jros2client-v7.0.zip](https://github.com/lambdaprime/jros2client/raw/main/jros2client/release/jros2client-v7.0.zip)

# Version 6

- Updating jros2droid to show live logs
- Renaming jros2droid namespace
- Adding jros2droid example
- Adding spotless to examples
- Moving all examples under generic folder
- Updating jros2messages to v6.0-SNAPSHOT
- Issue #10 Updating rtpstalk to v7.0

[jros2client-v6.0.zip](https://github.com/lambdaprime/jros2client/raw/main/jros2client/release/jros2client-v6.0.zip)

# Version 5

- Updating jrosclient:8.0, jros2messages:5.0, rtpstalk:6.0
- Integrating opentelemetry-exporters-pack-junit

[jros2client-v5.0.zip](https://github.com/lambdaprime/jros2client/raw/main/jros2client/release/jros2client-v5.0.zip)

# Version 4

- Adding build support for Android
- Integrating metrics
- Issue #8 Enable pushMode by default
- Adding throughput tests
- Issue lambdaprime/jros2messages#1 Adding example for images
- Updating rtpstalk to 5.0-SNAPSHOT and integrating metrics to tests
- Issue #6 Updating jros2messages
- Issue #3 Adding examples for char type
- Issue #2 Adding example for pub/sub in same application

[jros2client-v4.0.zip](https://github.com/lambdaprime/jros2client/raw/main/jros2client/release/jros2client-v4.0.zip)

# Version 3

Previously [rtpstalk](https://github.com/pinorobotics/rtpstalk) supported only RTPS versions 2.2 and 2.3. [CycloneDDS 0.9.x (Papillons)](https://github.com/eclipse-cyclonedds/cyclonedds.git), which is [included into ROS2 Humble](https://www.ros.org/reps/rep-2000.html#humble-hawksbill-may-2022-may-2027), uses RTPS 2.1. Because `jrosclient` relies on `rtpstalk` for RTPS communication, it could not interact with ROS2 nodes running on `CycloneDDS RMW`.
Major change in `jrosclient` v3, is update to new version of `rtpstalk`, which now supports RTPS 2.1. This consequently allows `jrosclient` to interact with `ROS2 CycloneDDS RMW`.

- Adding tests for cyclonedds
- Updating to gradle 8.0.2 and adding @inheritDoc

[jros2client-v3.0.zip](https://github.com/lambdaprime/jros2client/raw/main/jros2client/release/jros2client-v3.0.zip)

# Previous versions

Changelog for previous versions were published in github Releases but then migrated here.
