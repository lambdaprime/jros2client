Application which demonstrates **jros2client** usage under Android.

Additionally it is used for **jros2client** Android integration tests.

# Prereq

- Android 14

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
adb shell cat /storage/emulated/0/Android/data/io.lambdaprime.jros2droid/files/jros2client-debug.log
```
