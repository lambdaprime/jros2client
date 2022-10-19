# Build

Building module locally and making changes to it (this is optional and not intended for users).

## With Eclipse

- Build eclipse projects:

``` bash
gradle eclipse
```

- Import them into Eclipse

# Release steps

- Close version in gradle.properties
- Run `gradle clean build javadoc`
- Publish
- Open next SNAPSHOT version
- Commit changes
- Push
- Create new release in GitHub (for changelog use `git log --format=%s`)
- Upload documentation to website
