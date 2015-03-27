# gpullr-backend
pull request administration tool - backend

1. [Introduction] (#introduction)
-  [Development] (#development)
 -  [Gradle tasks] (#gradle-tasks)
 -  [Login using CURL] (#login-using-curl)
 -  [Updating schema.sql] (#updating-schema)
 -  [Jenkins] (#jenkins)
- [Deployment] (#deployment)
 - [Running the application] (#running-the-application)
 - [Profiles] (#profiles)
- [Technologies] (#technologies)

# Introduction

The purpose of gpullR is to provide a handy tool for visualization and organization of pull requests and the process of code review. This project provides the backend and its underlying business logic. It stores data like all concerned users, repositories, pull requests and so on. The backend provides a RESTful interface for information exchange with the frontend(s).

# Development
## Gradle tasks
To run the application, type
`./gradlew run`

To build the application, type
`./gradlew build`

## Login using curl
**Login:** `curl -b cookies.txt -c cookies.txt -v -XPOST http://127.0.0.1:8888/users/login/95374`

**check session:** `curl -b cookies.txt -c cookies.txt -v http://127.0.0.1:8888/users/me`

## Updating schema
The application stores its local data in a file-based H2 database, accessed via JPA (Hibernate) through Spring Data
JPA (see technologies).

To avoid trouble when entity fields change, the feature to auto-generate the database schema has been deactivated (see method `createJpaVendorAdapter()` - option `setGenerateDdl(true|false)` in [com.devbliss.gpullr.repository.PersistenceConfig](https://github.com/devbliss/gpullr-backend/blob/master/src/main/java/com/devbliss/gpullr/repository/PersistenceConfig.java)).

Instead, the database schema is generated with the commands in [schema.sql](https://github.com/devbliss/gpullr-backend/blob/master/src/main/resources/schema.sql). It is automatically applied by Spring on every application startup.
Thus, all commands adding things to the database schema must use the `IF NOT EXISTS` otherwise it will crash.

If you have to add, change or remove a field in an entity, simply add the respective `ALTER TABLE` statement to `schema.sql`.
If you are not familiar enough with SQL DDL, you may temporarily switch on the generate-ddl-feature (see above) on your local machine, remove the database file, start the application, and connect to the database (e.g. by double-clicking the `h2.jar` which can be found in the downloadable distribution of H2 and pretty likely in your gradle dependency cache directory and connecting with the web console) in order to dump the schema with the following SQL command (in the web console use the database url `jdbc:h2:file:/path/to/your/database` and blank username and password).
Make sure to rename `schema.sql` to any other name before temporarily switching on the generate-ddl-feature otherwise SpringData will still try to apply it before the auto-generation gets into action, which may cause conflicts.

`SCRIPT NODATA NOPASSWORDS NOSETTINGS TO '/tmp/schema-export-h2.sql' CHARSET 'UTF-8';`

 and copy-paste the relevant parts to `schema.sql`. Don't forget to deactivate generate-ddl afterwards again!

## Jenkins
[Jenkins view] (http://jenkins.devbliss.com/view/gPullR)

# Deployment
## Running the application
The `build.gradle` is configured to build a fatJar, when executing `gradle build` (or `./gradlew build`). This jar is all you need to deploy gpullR backend. You can run it, executing `java -jar gpullr-backend-{version}.jar -Dspring.profiles.active={profile} -Xms64m -Xmx256m`.

## Profiles
The application supports several profiles, which are supposed to ease the handling of different environments. Supported profiles are:

* test
* dev
* prod

# Technologies
The project depends on the following key technologies:

* [Spring Boot] (http://projects.spring.io/spring-boot/) (including web, jetty, actuator, data-jpa)
* [Hibernate] (http://hibernate.org/)
* [H2 Database Enging] (http://www.h2database.com/html/main.html)
* [Apache HttpClient] (http://hc.apache.org/httpclient-3.x/) for communication with [Github] (https://github.com/)
