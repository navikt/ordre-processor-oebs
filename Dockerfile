FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:fff2d6b09c217822100ee86b10fe548a28521e2f62fb91c177f3b09c56e1f044
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]