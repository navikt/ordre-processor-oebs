FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:c5fad51e4a288ea864fd640c8852ca99b35b779e0cfd5cfd14c27227914e8f26
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]