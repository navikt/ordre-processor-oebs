FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:be9eff4be1654b9db1436fe9ae333b91f00de107ea966c4650f2f9b28813793f
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]