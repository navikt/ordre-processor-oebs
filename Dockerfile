FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:921867402ce9d552318f361e627bb39caf9622b06471ab0e813426270d9b2c0e
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]