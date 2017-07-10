FROM frolvlad/alpine-oraclejdk8:cleaned


MAINTAINER NeoGames, Ben (OutdatedVersion)

RUN apk update \
     && apk upgrade \
     && apk add --no-cache --update redis mariadb bash curl openssl git ca-certificates \
     && mkdir /mc \
     && adduser -D -h /mc mc \
     && chown -R mc:mc /mc


USER mc
ENV  USER mc
ENV  HOME /mc


# Minecraft port
EXPOSE 25567
WORKDIR /mc


COPY start-network.sh /start-network.sh

CMD ["/bin/bash", "/start-network.sh"]
