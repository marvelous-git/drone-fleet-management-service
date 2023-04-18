## Instructions for Building
To build the application, run the following command: `mvn clean install`

## Instructions for Running
To run the Drone Fleet Management Service application, open it in your IDE and execute it from there.

## How to Access Swagger
To access Swagger, navigate to: `http://localhost:8080/swagger-ui/index.html`

## How to Access the In-Memory H2 Database
To access the in-memory H2 database, go to: `http://localhost:8080/h2-console`

**Assumptions Made:**

- If a drone is idle, it is assumed to be either charged or charging.
- If a drone is delivering or returning, it is assumed that the battery is discharging.

