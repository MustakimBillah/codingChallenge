FROM openjdk:8
ADD target/coding-challenge.jar coding-challenge.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","coding-challenge.jar"]