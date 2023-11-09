# Maritime Resource Registry (MRR)

The MRR is a registry for all maritime resources that are identified by a Maritime Resource Name (MRN).

## Prerequisites

* Java 21
* Maven 3.8.1+
* Redis
* Neo4J

## Build

To build the application run the following command:

```sh
mvn clean install
```

## Run

After you have built the application you can run it using the following command:

```sh
java -jar target/mrr-0.0.1-SNAPSHOT.jar
```

## Configuration

If you need to change the configuration of the application there are two ways you can do that.\
The first way is that you can change the content of [application.properties](/src/main/resources/application.properties). Note that if you do this, you will need to rebuild the application using the procedure described above.\
The second way is that you can change the configuration externally by making your own application.properties file and point to it when you run the application using the following command:

```sh
java -jar target/mrr-0.0.1-SNAPSHOT.jar --spring.config.location=<path to your custom application.properties>
```
