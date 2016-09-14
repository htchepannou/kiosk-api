#!/bin/bash

echo TRAVIS_BRANCH=$TRAVIS_BRANCH

if ["$TRAVIS_BRANCH" = "master"]; then

    wget http://roboconf.net/resources/build/settings.xml
    mvn clean deploy -q --settings settings.xml

fi
