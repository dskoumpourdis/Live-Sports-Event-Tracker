# Live-Sports-Event-Tracker Project

This is a live sports event tracking application.
The application uses:
*Java 21
Spring Boot 3.5.3
Kafka
Quartz*


## How to Run

* Clone this repository
* You can build the project and run the tests with ```mvn clean package```
* Once successfully built, you can run the service by one of these two methods:
```
        docker-compose up -d
and      
        java -jar target/tracker-0.0.1-SNAPSHOT.jar
or
        mvn spring-boot:run
```


### Create or update an event

```
{
    "eventId": "1234",
    "status": "LIVE"
}

RESPONSE: HTTP 200 (OK)
```





## Tests
All unit tests were generated using the diffblue AI plugin.
All kafka and scheduler tests were generated using Claude AI.