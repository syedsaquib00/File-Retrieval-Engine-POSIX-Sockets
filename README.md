# File-Retrieval-Engine-POSIX-Sockets

This project implements a distributed indexing and searching system in Java, allowing multiple clients to index and search a dataset of text files efficiently. The system is designed to distribute the indexing workload among multiple clients and manage search queries through a central server.

### Table of Contents

- [Project Structure](#project-structure)
- [Building the Program](#building-the-program)
- [Running the Program](#running-the-program)
- [Performance Evaluation](#performance-evaluation)

### Project Structure

The project directory is organized as follows:

```
.
├── client
|   ├── ClientApp.java
|   ├── AppInterface.java
│   ├── ClientConnection.java
│   ├── ClientProcessingEngine.java
├── common
│   ├── IndexStore.java
│   ├── MessageProtocol.java
│   ├── SearchResult.java
├── server
|   ├── AppInterface.java
|   ├── ServerProcessingEngine.java
|   ├── DispatcherThread.java
│   ├── Server.java
│   ├── WorkerThread.java
├── datasets
│   ├── Dataset1
│   ├── Dataset2
│   ├── Dataset3
│   ├── Dataset4
│   ├── Dataset5
├── README.md
├── POM.xml
```

- **client/**: Contains the client-side code.
  - `ClientConnection.java`: Manages the connection to the server.
  - `ClientProcessingEngine.java`: Handles indexing and searching operations on the client side.
  
- **common/**: Contains shared classes between the client and server.
  - `IndexStore.java`: Manages the indexing data structure.
  - `MessageProtocol.java`: Defines message formats for communication between clients and the server.
  - `SearchResult.java`: Represents a search result.
  
- **server/**: Contains the server-side code.
  - `Server.java`: Manages incoming client connections and processes their requests.
  - `WorkerThread.java`: Handles individual client connections.
  
- **datasets/**: Contains sample datasets used for indexing and searching.
- **README.md**: The project documentation file.
- **build.gradle**: The Gradle build script.

### Building the Program

This project uses Gradle as the build tool. To build the project, follow these steps:

1. **Install Gradle**: Ensure that Gradle is installed on your system. You can download it from [here](https://gradle.org/install/).

2. **Clone the Repository**: Clone the repository to your local machine.
   ```bash
   git clone <repository-url>
   cd <repository-directory>
   ```

3. **Build the Project**: Run the following command to build the project.
   ```bash
   gradle build
   ```

### Running the Program

#### Step 1: Start the Server

Run the server before starting any clients. Use the following command to start the server:
```bash
java -cp build/classes/java/main server.Server <port>
```
Replace `<port>` with the desired port number, for example:
```bash
java -cp build/classes/java/main server.Server 8080
```

#### Step 2: Start the Client(s)

After the server is running, start the client(s). Use the following command to start a client:
```bash
java -cp build/classes/java/main client.ClientProcessingEngine <serverIp> <port>
```
Replace `<serverIp>` with the IP address of the server and `<port>` with the port number on which the server is listening, for example:
```bash
java -cp build/classes/java/main client.ClientProcessingEngine 127.0.0.1 8080
```

#### Step 3: Perform Indexing

To index a dataset, use the following command in the client:
```java
ClientProcessingEngine engine = new ClientProcessingEngine();
engine.connect("127.0.0.1", 8080);
engine.index("<datasetPath>");
```
Replace `<datasetPath>` with the path to the dataset folder, for example:
```java
engine.index("datasets/Dataset1");
```

#### Step 4: Perform Searching

To perform a search query, use the following command in the client:
```java
engine.search("<query>");
```
Replace `<query>` with the search query, for example:
```java
engine.search("example AND search");
```

#### Step 5: Disconnect the Client

To disconnect the client from the server, use the following command:
```java
engine.quit();
```

### Performance Evaluation

To evaluate the performance of the indexing operation, follow these steps:

1. **Run the Program with Different Client Configurations**: Run the program with different numbers of clients (1, 2, 4, and 8 clients) and measure the wall time it takes to index each dataset.
2. **Measure Throughput**: Calculate the throughput in MB/s by dividing the total dataset size by the total indexing execution time.
3. **Record the Results**: Record the results in a table format.
