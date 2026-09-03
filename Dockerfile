FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:db850b45bf6aa633db1325af7f97eb8279f088869a7c0b1cd659ed32c8c91cc2
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]