FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:e5c27cf7c47fbed5202adc198805cb9777d9889b6846f1c0fc84a1c0883a434c
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]