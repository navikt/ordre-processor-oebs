FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:af529f1b0f8b1e046858318418edc7daa81355f5351c2863adde304d345ee150
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]