FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:356a4d3f412d8bcf336b834f1e8c3d7c3f089d3cff69be1383e9d314ec7d30ce
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]