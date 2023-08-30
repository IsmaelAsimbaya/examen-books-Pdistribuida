gradlew clean
gradlew quarkusBuild

podman build -f books/Dockerfile -t docker.io/ciasimbaya/app-books
podman build -f authors/Dockerfile -t docker.io/ciasimbaya/app-authors
