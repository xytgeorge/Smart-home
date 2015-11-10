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
#include <spi.h>


#define APIKEY         "tyo6j7R2afMSlSm6dTpppVAotXSSAKxYL0gvMEF4Y2pDQT0g" // replace your Cosm api key here
#define FEEDID         119454 // replace your feed ID
#define USERAGENT      "My Project" // user agent is the project name
//digital pins
#define DHTPIN         3
#define BODY           5
#define RELAY          8
//analog pins
#define AMBIENTLIGHT   0
#define SOUND          2
#define FLAME          3



dht DHT; //DHT22 
int BH1750address = 0x23;//BH1750 Address
byte buff[2];//buffer for BH1750


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
TextFinder finder (client);
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
  Wire.begin(9600);
  Serial.println("Test");
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
    float ambient_light = (1024 - analogRead(AMBIENTLIGHT))/10.24;
    float sound = analogRead(SOUND)/10.24;
    
    BH1750_Init(BH1750address);
    //delay(200);
    if(2==BH1750_Read(BH1750address))
    {
     float light = ((buff[0]<<8)|buff[1])/1.2;
     sendData(DHT.temperature, DHT.humidity, ambient_light, sound, light);
    }
    else
    {
      Serial.println("Send data failed");
    } 
  }
  // store the state of the connection for next time through
  // the loop:
  lastConnected = client.connected();
  
  
}

// this method makes a HTTP connection to the server:
void sendData(float thisData1, float thisData2, float thisData3, float thisData4, float thisData5) {
  // if there's a successful connection:
  Serial.print("Temperature (oC): ");
  Serial.println((float)thisData1, 2);
  Serial.print("Humidity (oC): ");
  Serial.println((float)thisData2, 2);
  Serial.print("Ambient Light (%): ");
  Serial.println((float)thisData3, 2);
  Serial.print("Sound (%): ");
  Serial.println((float)thisData4, 2);
  Serial.print("Light (Lx): ");
  Serial.println((float)thisData5, 2);
  
  delay(1000);
  
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
    // 8 bytes for "sensor1," + number of digits of the data
    int thisLength = 96;
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
    client.print("Ambient_Light,");
    client.println(thisData3);
    client.print("Sound,");
    client.println(thisData4);
    client.print("Light,");
    client.println(thisData5);
  
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


// This method calculates the number of digits in the
// sensor reading.  Since each digit of the ASCII decimal
// representation is a byte, the number of digits equals
// the number of bytes:

int getLength(int someValue) {
  // there's at least one byte:
  int digits = 1;
  // continually divide the value by ten, 
  // adding one to the digit count for each
  // time you divide, until you're at 0:
  int dividend = someValue /10;
  while (dividend > 0) {
    dividend = dividend /10;
    digits++;
  }
  // return the number of digits:
  return digits;
}

int BH1750_Read(int address) //
{
  int i=0;
  Wire.beginTransmission(address);
  Wire.requestFrom(address, 2);
  while(Wire.available()) //
  {
    buff[i] = Wire.read();  // receive one byte
    i++;
  }
  Wire.endTransmission(); 
  return i;
}

void BH1750_Init(int address)
{
  Wire.beginTransmission(address);
  Wire.write(0x10);//1lx reolution 120ms
  Wire.endTransmission();
}
