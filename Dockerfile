FROM eclipse-temurin:17

WORKDIR /application

COPY target/*.jar app.jar

EXPOSE 8080
EXPOSE 5005

ENTRYPOINT ["java","-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","/application/app.jar"]