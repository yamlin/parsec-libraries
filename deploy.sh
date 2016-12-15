#!/usr/bin/env bash

set -ev

test "${TRAVIS_PULL_REQUEST}" == "false"
test "${TRAVIS_BRANCH}" == "master"

./gradlew  bintrayUpload -PbintrayUser=${env.BINTRAY_USER} -PbintrayKey=${env.BINTRAY_API_KEY}
