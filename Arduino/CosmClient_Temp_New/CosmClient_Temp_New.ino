/*
  Cosm sensor client
 
 This sketch connects an analog sensor to Cosm (http://www.cosm.com)
 using a Wiznet Ethernet shield. You can use the Arduino Ethernet shield, or
 the Adafruit Ethernet shield, either one will work, as long as it's got
 a Wiznet Ethernet module on board.
 
 This example has been updated to use version 2.0 of the cosm.com API. 
 To make it work, create a feed with a datastream, and give it the ID
 sensor1. Or change the code below to match your feed.
 
 
 Circuit:
 * Analog sensor attached to analog in 0
 * Ethernet shield attached to pins 10, 11, 12, 13
 
 created 15 March 2010
 updated 14 May 2012
 by Tom Igoe with input from Usman Haque and Joe Saavedra
 
http://arduino.cc/en/Tutorial/CosmClient
 This code is in the public domain.
 
 */

#include <SPI.h>
#include <Ethernet.h>
#include <DHT.h>

#define APIKEY         "tyo6j7R2afMSlSm6dTpppVAotXSSAKxYL0gvMEF4Y2pDQT0g" // replace your Cosm api key here
#define FEEDID         119454 // replace your feed ID
#define USERAGENT      "My Project" // user agent is the project name

#define DHTPIN         3

dht DHT; //DHT22 

// assign a MAC address for the ethernet controller.
// Newer Ethernet shields have a MAC address printed on a sticker on the shield
// fill in your address here:
byte mac[] = { 
  0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED};

// fill in an available IP address on your network here,
// for manual configuration:
IPAddress ip(10,0,1,20);

// initialize the library instance:
EthernetClient client;

// if you don't want to use DNS (and reduce your sketch size)
// use the numeric IP instead of the name for the server:
//IPAddress server(216,52,233,121);      // numeric IP for api.cosm.com
char server[] = "api.cosm.com";   // name address for cosm API

unsigned long lastConnectionTime = 0;             // last time you connected to the server, in milliseconds
boolean lastConnected = false;                    // state of the connection last time through the main loop
const unsigned long postingInterval = 10L*1000L;  // delay between updates to cosm.com
						  // the "L" is needed to use long type numbers


void setup() {
  // start serial port:
  Serial.begin(9600);
 // start the Ethernet connection:
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // DHCP failed, so use a fixed IP address:
    Ethernet.begin(mac, ip);
  }
}

void loop() {
  // read the analog sensor:
  //int sensorReading = analogRead(A0);   

  // if there's incoming data from the net connection.
  // send it out the serial port.  This is for debugging
  // purposes only:
  if (client.available()) {
    char c = client.read();
    Serial.print(c);
  }

  // if there's no net connection, but there was one last time
  // through the loop, then stop the client:
  if (!client.connected() && lastConnected) {
    Serial.println();
    Serial.println("disconnecting.");
    client.stop();
  }

  // if you're not connected, and ten seconds have passed since
  // your last connection, then connect again and send data:
  if(!client.connected() && (millis() - lastConnectionTime > postingInterval)) {
    int chk = DHT.read22(DHTPIN);
    sendData(DHT.temperature, DHT.humidity);
  }
  // store the state of the connection for next time through
  // the loop:
  lastConnected = client.connected();
}

// this method makes a HTTP connection to the server:
void sendData(float thisData1, float thisData2) {
  // if there's a successful connection:
  if (client.connect(server, 80)) {
    Serial.println("connecting...");
    // send the HTTP PUT request:
    client.print("PUT /v2/feeds/");
    client.print(FEEDID);
    client.println(".csv HTTP/1.1");
    client.println("Host: api.cosm.com");
    client.print("X-ApiKey: ");
    client.println(APIKEY);
    client.print("User-Agent: ");
    client.println(USERAGENT);
    client.print("Content-Length: ");

    // calculate the length of the sensor reading in bytes:
    // 8 bytes for "sensor1," + number of digits of the data:
    int thisLength = 48;
    client.println(thisLength);

    // last pieces of the HTTP PUT request:
    client.println("Content-Type: text/csv");
    client.println("Connection: close");
    client.println();

    // here's the actual content of the PUT request:
    client.print("Digital_Temperature,");
    client.println(thisData1);
    client.print("Digital_Humidity,");
    client.println(thisData2);
  
  } 
  else {
    // if you couldn't make a connection:
    Serial.println("connection failed");
    Serial.println();
    Serial.println("disconnecting.");
    client.stop();
  }
   // note the time that the connection was made or attempted:
  lastConnectionTime = millis();
}

