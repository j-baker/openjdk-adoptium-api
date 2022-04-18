FROM docker.io/azul/zulu-openjdk-alpine:17.0.2-17.32.13
EXPOSE 8080
RUN apk update && apk add bash
CMD ["service/bin/init.sh", "console"]
ADD *.tgz /
RUN mv /openjdk-adoptium-api-* /server
WORKDIR /server
