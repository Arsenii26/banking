From tomcat:9.0.34-jdk11-openjdk
VOLUME /tmp
EXPOSE 9000
ADD target/*.jar app.jar
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar" ]
