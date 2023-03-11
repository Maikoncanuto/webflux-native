FROM ghcr.io/graalvm/graalvm-ce:22.3.1 AS build-native

ENV DOCKER_CONTENT_TRUST 1
ENV MAVEN_VERSION 3.9.0
ENV MAVEN_DIR /opt/maven
ENV RUNTIME_BUILD /opt/build

RUN gu install native-image

WORKDIR $MAVEN_DIR

RUN curl -fsSLO https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz
RUN tar -zxvf apache-maven-$MAVEN_VERSION-bin.tar.gz
RUN rm *.tar.gz
RUN ln -s $MAVEN_DIR/apache-maven-$MAVEN_VERSION/bin/mvn /usr/bin/mvn

WORKDIR /development

COPY src src
COPY pom.xml .

RUN mvn clean package -DskipTests -Pnative native:compile-no-fork

RUN mv ./target/ $RUNTIME_BUILD

FROM alpine:3.17.2 AS runtime

ENV DOCKER_CONTENT_TRUST 1
ENV IMAGE_USER nonroot
ENV IMAGE_GROUP nonroot
ENV APP_RUNTIME_NAME api-document-validation
ENV HOME /runtime

RUN apk --no-cache update && apk upgrade && apk --no-cache add --clean-protected gcompat && rm -rf /var/cache/apk/*

RUN addgroup -S $IMAGE_GROUP && adduser -G $IMAGE_GROUP -s /runtime -D $IMAGE_USER

USER $IMAGE_USER

WORKDIR $HOME

COPY --from=build-native --chown=$IMAGE_USER:$IMAGE_GROUP /opt/build/$APP_RUNTIME_NAME $HOME/

EXPOSE 8080 7000

CMD ["./run-app"]




