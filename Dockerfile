FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:3071f50277d545b4f3b58048262a43837bdebb1e12301ec69717d3bbed38b571
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]