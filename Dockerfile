FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:a4aa830802741123c1ed3ad3c5b1417d1297aaedc1e5c8f1784fc4df7568bf97
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]