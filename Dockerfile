FROM ubuntu:latest
LABEL authors="Brad"

ENTRYPOINT ["top", "-b"]