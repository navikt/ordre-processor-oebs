FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:56d53e70250c5f9ce8f3e67e79a84e95dd40223e8bf6744e1174dcf2aafce59d
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]