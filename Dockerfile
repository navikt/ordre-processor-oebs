FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:f726508b3d7e9843fed7331e56a12f5debc891a8503fadaed8d9e6c1e3a53d8d
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]