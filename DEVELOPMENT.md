# Build

Building module locally and making changes to it (this is optional and not intended for users).

## With Gradle

``` bash
gradle clean build
```

## With Eclipse

- Build Eclipse projects:

``` bash
gradle eclipse
```

- Import them into Eclipse

# Release steps

- Update [Android dependencies](android/gradle.properties) and run `gradle clean build -b android/build.gradle`
- Close version in gradle.properties
- Run `gradle clean build javadoc` (on Humble then Jazzy)
- Publish to staging, close repository
- Update [jros2droid](jros2client.examples/jros2droid/build.gradle) to new jros2client version and build it
- Install in Android
- Run `gradle :jros2client:createRelease -PaddJRosDroid`
- Publish
- Open next SNAPSHOT version
- Update [CHANGELOG.md](jros2client/release/CHANGELOG.md) with new release (for changelog generation use `git log --format=%s`)
- Commit changes
- Push
- Make sure that new release did not make any changes to existing examples and "jros2droid". If it did then update corresponding examples in the documentation.
- Upload documentation to website
- Update "bootstrap" project