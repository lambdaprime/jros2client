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

- Run `gradle clean build -b android/build.gradle`
- Close version in gradle.properties
- Run `gradle clean build javadoc`
- Publish
- Open next SNAPSHOT version
- Update CHANGELOG.md with new release (for changelog generation use `git log --format=%s`)
- Commit changes
- Push
- Make sure that new release did not make any changes to existing examples and "jros2droid". If it did then update corresponding examples in the documentation.
- Upload documentation to website
- Update "bootstrap" project