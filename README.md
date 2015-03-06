# gpullr-backend
pull request administration tool - backend

# Getting started

## Gradle tasks
To run the application, type
`./gradlew run`

To build the application, type
`./gradlew build`


## verify the login
### using curl
#### login
curl -b cookies.txt -c cookies.txt -v -XPOST http://127.0.0.1:8888/users/login/95374

#### check session
curl -b cookies.txt -c cookies.txt -v http://127.0.0.1:8888/users/me

## Persistence
The application stores its local data in a file-based H2 database, accessed
via JPA (Hibernate) through Spring Data JPA.
To avoid trouble when entity fields change, the feature to auto-generate the database schema has been deactivated (see method `createJpaVendorAdapter()` - option `setGenerateDdl(true|false)` in [com.devbliss.gpullr.repository.PersistenceConfig](https://github.com/devbliss/gpullr-backend/blob/master/src/main/java/com/devbliss/gpullr/repository/PersistenceConfig.java)).

Instead, the database schema is generated with the commands in [schema.sql](https://github.com/devbliss/gpullr-backend/blob/master/src/main/resources/schema.sql). It is automatically applied by Spring on every application startup.
Thus, all commands adding things to the database schema must use the `IF NOT EXISTS` otherwise it will crash.

If you have to add, change or remove a field in an entity, simply add the respective `ALTER TABLE` statement to `schema.sql`.
If you are not familiar enough with SQL DDL, you may temporarily switch on the generate-ddl-feature on your local machine, remove the database file, start the application, stop it after the gpullrDb.mv.db has been recreated and connect to the database using the H2 web console.

Run the `h2.jar` (obtainable via http://www.h2database.com/html/download.html -> Jar File) and connect with Generic H2 (Embedded), JDBC URL "jdbc:h2:/path/to/gpullr-backend/gpullrDb" and blank user name and password.

Then dump the schema with the following SQL command:
`SCRIPT NODATA NOPASSWORDS NOSETTINGS TO '/tmp/schema-export-h2.sql' CHARSET 'UTF-8';`
 and copy-paste the relevant parts to `schema.sql`.
 
 Don't use the export directly as it is missing important stuff like `IF NOT EXISTS`.
 
 Don't forget to deactivate generate-ddl afterwards again!
