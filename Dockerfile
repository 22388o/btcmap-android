FROM openjdk:8

ENV SDK_URL="https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip"
ENV ANDROID_HOME="/usr/local/android-sdk"
ENV ANDROID_VERSION=28
ENV ANDROID_BUILD_TOOLS_VERSION=28.0.3

RUN mkdir "$ANDROID_HOME" .android \
    && cd "$ANDROID_HOME" \
    && curl -o sdk.zip $SDK_URL \
    && unzip sdk.zip \
    && rm sdk.zip \
    && yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses

RUN $ANDROID_HOME/tools/bin/sdkmanager --update

RUN $ANDROID_HOME/tools/bin/sdkmanager "build-tools;${ANDROID_BUILD_TOOLS_VERSION}" \
    "platforms;android-${ANDROID_VERSION}" \
    "platform-tools"

COPY . /src
WORKDIR /src
RUN ./gradlew build
