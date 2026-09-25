FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:68464060263e63d2f00d73b06b0a53195bb8093723b4738cc9283d39fc721035
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]