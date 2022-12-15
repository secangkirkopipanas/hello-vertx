#!/bin/bash

# Go to application directory
cd "$(dirname "$0")"

# Run application
# java $JAVA_OPTS -Dapp=$APP_NAME -jar /opt/$APP_NAME/$APP_NAME-$APP_VERSION.jar
java ${JAVA_OPTIONS} -Dapp=${APP_NAME} -jar ${APP_NAME}-${APP_VERSION} ${JAVA_ARGS}