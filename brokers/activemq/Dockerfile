FROM openjdk:8-jre-alpine
RUN wget -O activemq.tar.gz http://archive.apache.org/dist/activemq/5.15.9/apache-activemq-5.15.9-bin.tar.gz
RUN tar -xzf activemq.tar.gz 
CMD ["/bin/sh", "-c", "apache-activemq-5.15.9/bin/activemq console"]
# CMD "apache-activemq-5.15.9/bin/activemq start"
