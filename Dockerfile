FROM openjdk:17
EXPOSE 8080
ADD target/latin-kiril-apachi-poi.jar latin-kiril-apachi-poi.jar
ENTRYPOINT ["java", "-jar", "/latin-kiril-apachi-poi.jar"]