# Base container image
FROM openjdk:18-slim

# Defining all build arguments
ARG app_name=hello-vertx
ARG app_version=latest
ARG jar_file=container/resources/$app_name*.jar

# Installing prerequisites
RUN apt -o Acquire::Check-Valid-Until=false -o Acquire::Check-Date=false update && \
    apt install -y procps && \
    rm -rf /var/lib/apt/lists/*

# Preparing application directory
RUN mkdir -pv /opt/$app_name/conf

WORKDIR /opt/$app_name

# Preparing the artifact
COPY container/resources/app.sh /opt/$app_name/
COPY main/resources/conf/application.json /opt/$app_name/conf/

# Preparing the artifact
COPY container/resources/app.sh /opt/$app_name/
COPY container/resources/$app_name*.jar /opt/$app_name/
RUN chmod +x /opt/$app_name/*.sh

# Defining all environment variables
ENV APP_NAME="$app_name"
ENV APP_VERSION="$app_version"
ENV JAVA_OPTS="-Xms512M -Xmx1G"

# Run command to start the application
ENTRYPOINT [ "./app.sh" ]
