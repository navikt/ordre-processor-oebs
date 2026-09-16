FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:ae80aad8b17c5d0dcf80bb1870642d66de610fac266810ae720d3bc853144db0
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]