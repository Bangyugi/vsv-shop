FROM openjdk:17

ARG JAR_FILE=target/vsv-shop.jar

COPY ${JAR_FILE} vsv-shop.jar

ENTRYPOINT ["java","-jar","vsv-shop.jar"]

EXPOSE 8080