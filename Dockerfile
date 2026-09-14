FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:1a95adef3a9c91bdf61565e4d357d32269bc2d6a302f78d20ff8f5d2b5848827
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]