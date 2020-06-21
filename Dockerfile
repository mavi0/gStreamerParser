FROM frekele/ant:latest

WORKDIR /dash

COPY . /dash

ENV TZ=Europe/London
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN DEBIAN_FRONTEND="noninteractive" apt-get update && apt-get -y install -qq --force-yes moreutils ant xvfb libgstreamer1.0-0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good gstreamer1.0-plugins-bad gstreamer1.0-plugins-ugly gstreamer1.0-libav gstreamer1.0-doc gstreamer1.0-tools

RUN ant jar

CMD bash test.sh