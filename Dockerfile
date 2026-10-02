FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre@sha256:e0e635be7cf05aca9c40e52080b583b9e00d5032e74158d2011d055208a425ef
ENV TZ="Europe/Oslo"
COPY target/*.jar app.jar
CMD ["-jar","app.jar"]