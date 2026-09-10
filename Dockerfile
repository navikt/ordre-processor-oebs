FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:cdfa6aa5ea388b723e00804707ee8cfef3714a2ff3d7fd7cf817c53d3f601483
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]