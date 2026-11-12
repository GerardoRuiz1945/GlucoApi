# Usamos Java 17 (más compatible con NetBeans actual)
FROM tomcat:9.0-jdk17-openjdk-slim

# Limitamos la memoria RAM para que Render no mate el proceso (Max 256MB)
ENV JAVA_OPTS="-Xms128m -Xmx256m"

RUN rm -rf /usr/local/tomcat/webapps/*
COPY ./dist/API_HealthBlood.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]