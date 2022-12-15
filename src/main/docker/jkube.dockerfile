# RHEL Universal Base Image (RHEL UBI) is a stripped down, OCI-compliant,
# base operating system image purpose built for containers. For more information
# see https://developers.redhat.com/products/rhel/ubi
#
FROM quay.io/jkube/jkube-java:0.0.17
MAINTAINER Robertus Lilik Haryanto <rharyant@redhat.com>
USER root

ARG VERSION

### Install prerequisites
RUN REPOLIST="ubi-8-baseos-rpms" \
    INSTALL_PKGS="libnl3 net-tools zip openssl hostname iproute procps curl" && \
    microdnf -y update --disablerepo "*" --enablerepo ${REPOLIST} --setopt=tsflags=nodocs && \
    microdnf -y install --disablerepo "*" --enablerepo ${REPOLIST} --setopt=tsflags=nodocs ${INSTALL_PKGS} && \
    microdnf clean all && rm -rf /var/cache/yum
