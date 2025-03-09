## Prerequisites
Before you begin, ensure you have the following installed:

Java JDK (version 17 or higher)

Maven (version 3.6 or higher)

MySQL
Postman (for API testing, optional)

To check the installed versions, run:

```bash

java -version
mvn -v
```

## Configuration
Database Configuration:

Open the application.properties file in the src/main/resources directory.

Update the database connection details:

properties
```bash
spring.datasource.url=jdbc:mysql://localhost:3306/your-database-name
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.jpa.hibernate.ddl-auto=update
```
