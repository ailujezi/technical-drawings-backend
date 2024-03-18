# Technical Drawings Backend
This is the repository for the "Technical Drawings Backend" Java Spring Boot project.
The application is intended to be run along with a matching frontend application that consumes the provided API.

You can use the "Technical Drawings Frontend" Angular frontend application for this purpose.

## Prerequisites

Before you begin, ensure you have met the following requirements:
- JDK 21 or later

You can use any JDK build, e.g. Temurin: https://adoptium.net/de/temurin/releases/

## Project Setup
Once you have the prerequisites installed, you can set up the project on your local machine.

1. Clone the repository or download the project to your local machine.
2. Navigate to the project directory in the command prompt or terminal.

## Running the Application
Use the following command inside the project directory on a Linux/WSL system:
```
./gradlew bootRun
```
If you are using a Windows system, use this command instead:
```
gradlew.bat bootRun
```

This will start the application on the default port 8080.
After starting the application, the OpenAPI documentation can be found here:
```
http://localhost:8080/api-doc/index.html
```

## Production Build
To create a production-ready build of your application, use:
```
./gradlew build
```

This will compile, test, and package the application into a runnable JAR file inside the `build/libs` directory which can be deployed on the production environment.

## Configuration
- Application configuration can be adjusted in the `application.properties` in the `config` directory.
- If the file does not exist yet, create it by copying the example `application.properties.dist` file inside the directory.
