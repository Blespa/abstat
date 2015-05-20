FROM ubuntu:14.04

RUN \
	apt-get install -y wget software-properties-common && \
	sudo add-apt-repository ppa:webupd8team/java && \
	echo "deb http://packages.comsode.eu/debian wheezy main" > /etc/apt/sources.list.d/odn.list && \
	wget -O - http://packages.comsode.eu/key/odn.gpg.key | apt-key add - && \
	apt-get update && \
	apt-get install -y virtuoso-opensource=7.2 && \
	apt-get install -y nginx && \
	echo oracle-java7-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
	apt-get install -y oracle-jdk7-installer git wget bzip2 unzip
RUN \
	apt-get install -y gawk time curl

ENV install_dir /startup

COPY . $install_dir

RUN mv $install_dir/nginx.conf /etc/nginx/
RUN cp $install_dir/virtuoso.ini /etc/virtuoso-opensource-7/
RUN mv $install_dir/fct_dav.vad /usr/share/virtuoso-opensource-7/vad/ && chmod 644 /usr/share/virtuoso-opensource-7/vad/fct_dav.vad

RUN mkdir /schema-summaries/
VOLUME /schema-summaries/

WORKDIR startup
CMD ["./run-all.sh"]

EXPOSE 80

