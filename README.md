# Ping-Server
Required Software
MongoDB: Windows v3.0.12
https://www.mongodb.com/dr/fastdl.mongodb.org/win32/mongodb-win32-x86_64-2008plus-ssl-3.0.12-signed.msi/download
JVM or Java IDE(IntelliJ used)

Installation
  1. Install MongoDB.
    Windows Installation Guide: https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/
  2. Extract "Pings.jar" and "log4j2.xml" to a folder where you would like to run the Ping Server from, or open project src in IDE to run with parameters.

Running Instructions
  1. Run MongoDB:
    From cmd run "C:\path\to\mongod.exe --dbpath C:\path\to\db"
  2. Run Jar from command line (with optional parameters):
    "java -jar C:\path\to\Pings.jar" + " optional parameters"
  3. Run any tests required.

Parameter Options
[-h 'hostname'] [-p 'serverPort'] [-dh 'databaseHost'] [-dp 'databasePort']

Not specifying a parameter results in default values to be used, as follows.
Hostname: 127.0.0.1
Port: 3000
Database Hostname:	127.0.0.1
Database Port: 27017

Libraries Used
Commons CLI
Used to make parameter options simple and flexible.
Joda Time
Used in place of standard Time library, as it was easier to handle epoch, ISO, and time-zones.
Mongo Java Driver
Required for communications with the MongoDB database
JSON Simple
Found to be much easier than the javax json library, after using both.
Apache log4j
Good logging framework to control application output.
  
Data Persistence
A combination of a MongoDB database and memory has been used to implement data persistence. MongoDB was chosen over something like SQL to allow for simple expansion to the relational state. In the current implementation, the whole database is loaded into memory, but has been written with caching in mind, which can be implemented in the future. This combination means that data persistence is available, but control over the memory to serve data is also available. This could be used in conjunction with server nodes, that cache stored and accessed pings local to that node, meaning memory is reduced, but speed is kept high. All data can still be served and stored on the MongoDB database remotely.
