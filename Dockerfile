FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:9b76e51fe513a1e053ea863e4ff9d267ffd0edd9cc91f4fcf2f4cbfaf527699d
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]