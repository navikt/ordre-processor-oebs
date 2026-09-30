FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:8ba6bd80d2eccee4464639f718ff8c4f6d1b1e2b90c403bb2a1c51f02e9478b5
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]