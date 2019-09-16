# parking-api

## Build/Run 

* A deployable war file can be created in the target directory using the standard Maven command (`mvn package`) and then deployed to any compatible servlet container. 
* Alternatively, the application can be run using the included spring-boot-starter-tomcat dependency using the Maven command : `mvn spring-boot:run`
* Application can be accessed at http://localhost:8080/api/pairking

## Test
* All test cases are located in src/test/java and can run using the standard maven command (`mvn test`) in the projects root directory or with a run configuration of you favorite IDE.
* I have included the exported Postman collection file (ParkingAPI.postman_collection.json) in the projects root directory and this can be used for any integration testing. 

## Logging/Documentation 
* Application logging can be enabled by setting the logging level to DEBUG of the com.abuob.parking logger in the projects log4j2.yaml file
* I have included the Springfox Swagger 2 dependencies in the projects pom.xml file and have made some basic annotations in ParkingController.java. This can be accessed in the browser at http://localhost:8080/swagger-ui.html

