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
curl -b cookies.txt -v -XPOST http://127.0.0.1:8888/users/login/95374

#### check session
curl -b cookies.txt -v http://127.0.0.1:8888/users/me