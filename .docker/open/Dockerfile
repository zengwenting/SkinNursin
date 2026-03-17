FROM eclipse-temurin:17-jdk-alpine

ENV GRADLE_VERSION 7.5.1

WORKDIR /usr/local

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
    && apk update \
    && apk add nodejs \
    && apk add npm \
    && apk add curl unzip \
    # 【修改点】下面是修复后的官方源下载命令
    && curl -A "Mozilla/5.0" -L --retry 5 --connect-timeout 30 https://services.gradle.org/distributions/gradle-7.5.1-bin.zip -o gradle.zip \
    && unzip gradle.zip \
    && rm gradle.zip

ENV PATH "$PATH:/usr/local/gradle-${GRADLE_VERSION}/bin/"

WORKDIR /app

ENV SPRING_PROFILE test

CMD echo "==============> Current evironment: ${SPRING_PROFILE}" \
    && rm -f -r ./api/open/build \
    && gradle :api-open:build \
    && cd ./api/open/build/libs \
    && java -jar api-open-1.0-SNAPSHOT.jar --spring.profiles.active=${SPRING_PROFILE}


