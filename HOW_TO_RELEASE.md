# How to release

- make a release branch
- update the hard-coded version in the driver class of the `hpo-case-annotator-app` module
- update `pom.xml` versions using `versions:set` goal of the `versions-maven-plugin`
- create a release commit, open a release PR wrt. `master`, ensure the CI passes, and merge the PR 
- tag the merge commit with the version (e.g. `v0.1.2`)
- create a GitHub release for the tag, build distribution ZIPs for Linux, Mac, and Windows, and include the ZIPs assets
- merge `master` into `development` and set `SNAPSHOT` tags using `versions:set`
