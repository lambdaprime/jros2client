Application which demonstrates **jros2client** usage under Android.

Additionally it is used for **jros2client** Android integration tests.

# Prereq

- Android 14

# Android emulator

Android emulator does not support [multicast](https://developer.android.com/studio/run/emulator-networking#networkinglimitations) which means that **jros2client** would not be able to detect ROS nodes.

# Build

```
gradle assembleDebug installDebug
```

# Import to eclipse

```
gradle eclipse
```

Make sure to remove JRE from the dependencies because all Java classes will be available from android.jar (from Android SDK).

# Logs

```
adb shell cat /storage/emulated/0/Android/data/id.jros2droid/files/jros2client-debug.log
```
