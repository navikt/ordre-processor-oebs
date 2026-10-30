FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:52909a688717062ec7196452d0a9f673f2fb3f05ffabe467b811e9a02fc78c16
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]