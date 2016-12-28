#!/bin/bash

if [ "$TRAVIS_BRANCH" == "master" ]; then
  mkdir target/deploy
  cp target/kiosk-api.jar target/deploy/.
fi
