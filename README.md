# SAP Cloud SDK Training Sandbox
## Description
This repository is created to conduct an SAP Cloud SDK training with the goal to implement a sample Business Partner Address Manager Application based on Spring Boot and the SAP Cloud SDK from SAP.
It contains the structure and definition of one SAP Cloud SDK project with an skeleton application. This application will be enchanced by the participants during the training. 
## Preparation
Please setup the local development and infrastructure environment beforehand: 
### Tools
1. Download and Install [Java SDK 8](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html "´Java SDK 8").
2. Download and Install the latest [Eclipse](https://www.eclipse.org/downloads/?FEATURED_STORY "Eclipse") version.
3. Download and Install the latest [node.js](https://nodejs.org/en/download/ "node.js") version.

### Clone Repository
Please clone latest master branch from this repository and import it into the Eclipse IDE environment
## Project Structure
The sample SAP Cloud SDK project has the following structure
- **application**
The actual source code of the Java Spring Boot application. 
- **cx-server**
The SAP Cloud SDK provides a fully configured CI/CD pipeline with predefined Jenkin configuration. 
- **integration-tests**
Contains fully integrated end-end JUnit tests starting from simulating REST calls. 
- **mock-server**
During the training we will have no real access to an S/4HANA system. Therefore, to test the backend connectivity we will use an OData mock server based on a a simple Node.js application. The server makes it possible to test the SAP S/4HANA integration capabilities of the SAP Cloud SDK without access to an SAP S/4HANA system. The server hosts an OData v2 mock service that mimics the business partner API of SAP S/4HANA Cloud to a limited extent.
- **unit-tests**
Contains all application related to JUnit tests.
