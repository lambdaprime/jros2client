# Version 3

Previously [rtpstalk](https://github.com/pinorobotics/rtpstalk) supported only RTPS versions 2.2 and 2.3. [CycloneDDS 0.9.x (Papillons)](https://github.com/eclipse-cyclonedds/cyclonedds.git), which is [included into ROS2 Humble](https://www.ros.org/reps/rep-2000.html#humble-hawksbill-may-2022-may-2027), uses RTPS 2.1. Because `jrosclient` relies on `rtpstalk` for RTPS communication, it could not interact with ROS2 nodes running on `CycloneDDS RMW`.
Major change in `jrosclient` v3, is update to new version of `rtpstalk`, which now supports RTPS 2.1. This consequently allows `jrosclient` to interact with `ROS2 CycloneDDS RMW`.

- Adding tests for cyclonedds
- Updating to gradle 8.0.2 and adding @inheritDoc

[jros2client-v3.0.zip](https://github.com/lambdaprime/jros2client/raw/main/jros2client/release/jros2client-v3.0.zip)

# Previous versions

Changelog for previous versions were published in github Releases but then migrated here.
