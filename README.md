# gpullr-backend
pullrequest administration tool - backend

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
To avoid trouble when entity fields change, the feature to auto-generate the database schema has been deactivated (see Java code at `com.devbliss.gpullr.repository.PersistenceConfig#createJpaVendorAdapter()` - option `setGenerateDdl(...)`).

Instead, the database schema is generated with the commands in `schema.sql` which lives in `src/main/resources`. It is automatically applied by Spring on every application startup.
Thus, all commands adding things to the database schema must use the `IF NOT EXISTS` otherwise it will crash.

If you have to add, change or remove a field in an entity, simply add the respective `ALTER TABLE` statement to `schema.sql`.
If you are not familiar enough with SQL DDL, you may temporarily switch on the generate-ddl-feature on your local machine, remove the database file, start the application, and connect to the database (e.g. by double-clicking the `h2.jar` and connecting with the web console) in order to dump the schema with the following SQL command:

`SCRIPT NODATA NOPASSWORDS NOSETTINGS TO '/tmp/schema-export-h2.sql' CHARSET 'UTF-8';`

 and copy-paste the relevant parts to `schema.sql`. Don't forget to deactivate generate-ddl afterwards again!
