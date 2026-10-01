FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:6cfc2c5e3791cc50d11fd4404844a276bec2dc970493db1118ff8170b7c00259
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]