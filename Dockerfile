FROM central-docker-release.artif.apps.corp/ubi9:latest

USER root

ADD https://artif.apps.corp/artifactory/central-generic-ext-local/ca/ca-bundle.crt /etc/pki/ca-trust/source/anchors/ca-bundle.crt
RUN update-ca-trust
ADD https://artif.apps.corp/artifactory/central-generic-ext-local/repo/ubi.repo /etc/yum.repos.d/ubi.repo

RUN dnf install -y \
      git \
      bash \
      rsync \
    && dnf clean all \
    && rm -rf /var/cache/dnf

ENV SCRIPT_MODE=""

ENTRYPOINT ["sleep", "infinity"]



