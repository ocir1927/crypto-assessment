# Recommendation Service

The Recommendation Service is a Java application that provides recommendations for cryptocurrencies.

# About the assessment & implementation
### Requirements
- The read from file was made using a startup listener. After the application starts up, the service reads from prices/ folder and saves the data into crypto_price table. It also computes the min/max/oldest/newest/normalized range and also stores the timeframe on which these values were recorded(startDate, endDate).


- After loading a csv file, service stores the hash of this file, so we don't load it each time, avoiding duplicating data.


- The RecommendationController has all the endpoints that were in requirement.

### Things to consider
- For documentation, I've been using swagger, you can check the Documentation section to see where to access it.


- In order to add new cryptocurrencies, the service support file upload, check CryptoImportController and documentation.


- For safeguarding recommendation service, I've been using a simple validator based on cryptocurrency tickers loaded from app.properties. Depending on how often we want to add, remove new cryptos to our service, it might be a good idea to store those in a db/external configuration. For test purposes this should work out.


- If we want to check a bigger timeframe, we just need to feed CSV files containing all the data that we need, then our service stores the data and stores the computed min/max/oldest/newest/range as well as the timeframe on wich those were registered.

### Extra mile

- App is dockerized, just run docker-compose up and the service and its database should be up and running.
- A separate test database is as well created to help us run our tests.

- For rate limiting, I've been created my own RateLimitFilter, that stores the ip, requests count and the expiration datetime of rate limiting. It can be configured by: rate.limiter.duration and rate.limiter.requests in application.properties/ application-test.properties

### Other things

- Using flyway for DB versioning.
- Using Lombok for simplicity
- There are tests for IpRateLimiter, RecommendationService and CSVLoader(There could have been more tests, but I was time boxed)
- There are several ways to implement the assessment, I don't say I chose the best approach, but one that is simple and working as required. For a real scenario service with more data and more users, there are several things that I would have done differently.


## Technologies Used

- Java
- Spring Boot
- Maven
- PostgreSQL

## Prerequisites

Before running the Recommendation Service, ensure you have the following installed:

- Java Development Kit (JDK) 17 or later
- Maven (Tested with 3.8.x)
- Docker

## Getting Started

Follow the steps below to set up and run the Recommendation Service.

```bash
docker-compose up -d database recommendation-service
```
## Testing

```bash
docker-compose up -d database-test
mvn clean test
```
or alternatively run tests directly from your IDE.
By default, test profile is configured to run the tests on a localhost postgres db on port 5433.
Running
```bash
docker-compose up -d database-test
```
should solve that.

## Documentation

In order to access the documentation, start the service and go to:
[Swagger](http://localhost:8080/swagger-ui)

