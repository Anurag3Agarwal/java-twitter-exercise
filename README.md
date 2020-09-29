# java-twitter-exercise

## Requirements

For building and running the application you need:

- [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org)

## Running the application locally

You need to enter your twitter consumer key and secret in the application.properties, also the searchString can be configured in the same file.

There are several ways to run this application on your local machine. One way is to execute the `main` method in the Application class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

The sorted detailed output is printed in console logs but you can get a sneak peek in the browser as well.
