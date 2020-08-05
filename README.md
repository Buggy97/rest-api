# REST-API
Rest api for android app in order to save users personal info and the movies liked or tracked.
User are authenticated using an email/username and password and are provided with a JWT token that needs to be provided with every authorized request.

### Technologies used
The API was entirely developed using Java and Spring-boot. Authentication tokens are made using JWT and persistence is done using Hibernate that saves the data on a MySQL database. The entire project is built using Maven. 

### Deployment
Both the API and the DBMS are deployed on Azure. The reason why i used it is mainly because i got free credits on it and the deployment with Maven was quite simple.
