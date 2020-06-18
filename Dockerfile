FROM ubuntu:latest

WORKDIR /dash

COPY . /dash

ENV TZ=Europe/London
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN DEBIAN_FRONTEND="noninteractive" apt-get update && apt-get -y install -qq --force-yes cron moreutils ant xvfb default-jdk libgstreamer1.0-0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad gstreamer1.0-plugins-ugly gstreamer1.0-libav gstreamer1.0-doc gstreamer1.0-tools

COPY perf-cron /etc/cron.d/perf-cron

RUN chmod 0744 /dash/test.sh

RUN chmod 0644 /etc/cron.d/perf-cron

RUN touch /var/log/cron.log

RUN crontab /etc/cron.d/perf-cron

CMD cron && tail -f /var/log/cron.log