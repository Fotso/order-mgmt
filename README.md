### BTC/USDT and ETH/USDT order management

This Java-based application establishes a robust WebSocket server to stream real-time order book data from Binance for two major trading pairs: BTC/USDT and ETH/USDT. Utilizing the Jakarta WebSocket API, it provides a live view of market dynamics, presenting the top 50 bids and asks, continuously updated in real-time.

##### Features
WebSocket Communication: Handles live streaming data efficiently through WebSockets, ensuring minimal latency.
Real-Time Updates: Processes and displays updates for two key cryptocurrency pairs, BTC/USDT and ETH/USDT, with data refreshed every 10 seconds.
Concurrency and Thread Safety: Synchronized access to order book modifications protects data integrity under concurrent operations.
Error Handling: Implements robust error handling to manage and log connectivity and data processing issues effectively.
##### Client-Side Interaction
The front-end is built with HTML, CSS, and JavaScript, which connects to the WebSocket server to fetch and display the order book data dynamically. Users can see the latest bids and asks, with totals calculated for both, enhancing their trading decisions with up-to-date market data.

#### Installing and Running the Application with Maven

##### Prerequisites:

- Java JDK 17 must be installed and configured on your machine.
- Maven must be installed. You can verify it by running mvn -v in your terminal.
- (Optional) Apache Tomcat 10.1.19 installed and configured for deployment.

##### Steps:

- Clone the repository or download the source code:


```sh
# clone the project
$ git clone https://github.com/Fotso/order-mgmt.git

# now go to your order-mgmt repository
$ cd order-mgmt/

#install the dependencies
$ mvn clean install
```

- Deploy the WAR file to Tomcat:
Copy the generated WAR file from the target directory to your Tomcat's webapps directory.
Start/Restart the Tomcat server. You can start Tomcat by running ./bin/startup.sh (or startup.bat on Windows) from your Tomcat directory.


- Access the application:
Open a web browser and navigate to **http://localhost:8080/order-mgmt/** to view the application.


#### Setting Up and Running the Application in Eclipse
##### Prerequisites:

-  Eclipse IDE for Enterprise Java Developers must be installed.
- Tomcat 10.1.19 server must be set up in Eclipse.

#### Steps:
##### 1. Import the project into Eclipse:
- Open Eclipse and go to File > Import.
- Choose Existing Maven Projects under Maven.
- Browse to the root directory of your project where the pom.xml file is located and select it.
- Click Finish to import the project into Eclipse.

##### 2. Configure the server:
- Go to the Servers tab in Eclipse. If no servers are set up, you’ll see a link to create a new server.
- Click on New server and select Apache > Tomcat v10.1 Server.
- Browse to your Tomcat installation directory and finish the setup.

##### 3. Add the project to the Tomcat server:
- Right-click the Tomcat server in the Servers tab and select Add and Remove….
- Add your project to the configured server and click Finish.

##### 4. Start the server and deploy the project:
- Right-click the Tomcat server in the Servers tab and choose Start.
- This action will build the project, deploy the WAR file to Tomcat, and start the server.

##### 5. Access the application:
- Once the server starts, you can access the application by opening a web browser and navigating to http://localhost:8080/order-mgmt/.