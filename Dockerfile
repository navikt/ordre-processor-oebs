FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:836df8c4b10a33cc2f56a5c9ab04d2d489068e7cc3d2517ba7e862cb7909c41d
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]