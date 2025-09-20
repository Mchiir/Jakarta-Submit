# Use official Tomcat 10.1 with JDK 21 (Temurin, Noble OS)
FROM tomcat:10.1-jdk21-temurin-noble

# Path to your WAR file built by Maven
ARG JAR_FILE=target/Jakarta-Submit-1.0-SNAPSHOT.war

# Copy WAR file into Tomcat webapps folder and rename as ROOT.war
COPY ${JAR_FILE} /usr/local/tomcat/webapps/ROOT.war

# Expose port 8080 for Tomcat
EXPOSE 8080

# Start Tomcat server
CMD ["catalina.sh", "run"]