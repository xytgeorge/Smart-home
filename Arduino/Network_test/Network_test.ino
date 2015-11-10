/*
  Pachube sensor client
 
 This sketch connects an analog sensor to Pachube (http://www.pachube.com)
 using a Wiznet Ethernet shield. You can use the Arduino Ethernet shield, or
 the Adafruit Ethernet shield, either one will work, as long as it's got
 a Wiznet Ethernet module on board.
 
 This example has been updated to use version 2.0 of the Pachube.com API. 
 To make it work, create a feed with a datastream, and give it the ID
 sensor1. Or change the code below to match your feed.
 
 
 Circuit:
 * Analog sensor attached to analog in 0
 * Ethernet shield attached to pins 10, 11, 12, 13
 
 created 15 March 2010
 modified 9 Apr 2012
 by Tom Igoe with input from Usman Haque and Joe Saavedra
 
http://arduino.cc/en/Tutorial/PachubeClient
 This code is in the public domain.
 
 */

#include <SPI.h>
#include <Ethernet.h>
#include <DHT.h>
#include <TextFinder.h>
#include <Wire.h> //IIC
#include <math.h>

#define APIKEY         "tyo6j7R2afMSlSm6dTpppVAotXSSAKxYL0gvMEF4Y2pDQT0g" // replace your Cosm api key here
#define FEEDID         119454 // replace your feed ID
#define USERAGENT      "My Project" // user agent is the project name
//digital pins
#define DHTPIN         3
#define RELAY          8
#define BODY_DETECTION 11
//analog pins
#define AMBIENT_LIGHT  0
#define FLAME          1
#define SOUND          2
#define GAS_MQ2        3// user agent is the project name

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
TextFinder finder( client );

int BH1750address = 0x23;
byte buff[2];

// if you don't want to use DNS (and reduce your sketch size)
// use the numeric IP instead of the name for the server:
IPAddress server(216,52,233,122);      // numeric IP for api.pachube.com
//char server[] = "api.pachube.com";   // name address for pachube API

unsigned long lastConnectionTime = 0;          // last time you connected to the server, in milliseconds
boolean lastConnected = false;                 // state of the connection last time through the main loop
const unsigned long postingInterval = 3000; //delay between updates to Pachube.com

void setup() {
 // Open serial communications and wait for port to open:
  Serial.begin(9600);
  Wire.begin();
  while (!Serial) {
    ; // wait for serial port to connect. Needed for Leonardo only
  }
  pinMode(BODY_DETECTION, OUTPUT);
  pinMode(RELAY,OUTPUT);
   
 // start the Ethernet connection:
  if (Ethernet.begin(mac) == 0) {
    Serial.println("Failed to configure Ethernet using DHCP");
    // DHCP failed, so use a fixed IP address:
    Ethernet.begin(mac, ip);
  }
}

void loop() {
  // read the analog sensor:
    
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
    float digital_light = 0;
    BH1750_Init(BH1750address);
    if(2==BH1750_Read(BH1750address))
    {
        digital_light=((buff[0]<<8)|buff[1])/1.2;
    }
    else
    {
         Serial.println("light sensor failed");
    }
    float ambient_light = (1024 - analogRead (AMBIENT_LIGHT)) / 10.24;
    int body_detection;
    if(digitalRead (BODY_DETECTION) == HIGH)
    {
      body_detection = 1;
    }
    else
    {
      body_detection = 0;
    }
    float flame = analogRead (FLAME) / 10.24;
    float sound = analogRead (SOUND) /10.24;
    float gasMQ2 = analogRead (GAS_MQ2) / 10.24;
    
    sendData(DHT.temperature, DHT.humidity, ambient_light, digital_light, body_detection, flame, gasMQ2, sound);
    client.stop();
    getData();
    client.stop();
}
  // store the state of the connection for next time through
  // the loop:
  lastConnected = client.connected();
}

// this method makes a HTTP connection to the server:
void sendData(float thisData1, float thisData2, float thisData3, float thisData4, int thisData5, float thisData6, float thisData7, float thisData8) {
  // if there's a successful connection:
  if (client.connect(server, 80)) {
     Serial.println("connecting...");
    Serial.print("Digital Temperature = ");
    Serial.print(thisData1);
    Serial.println(" oC");
    Serial.print("Digital Humidity = ");
    Serial.print(thisData2);
    Serial.println(" %Rh");
    Serial.print("Analog Light = ");
    Serial.print(thisData3);
    Serial.println(" %");
    Serial.print("Digital Light = ");
    Serial.print(thisData4);
    Serial.println(" Lux");
    Serial.print("Body Detection = ");
    if (thisData5 == 1)
    {
      Serial.println("Y");
    }
    else if (thisData5 == 0)
    {
      Serial.println("N");
    }
    Serial.print("Flame = ");
    Serial.print(thisData6);
    Serial.println(" %");
    Serial.print("Gas_MQ2 = ");
    Serial.print(thisData7);
    Serial.println(" %");
    Serial.print("Sound = ");
    Serial.print(thisData8);
    Serial.println(" %");
    // send the HTTP PUT request:
    client.print("PUT /v2/feeds/");
    client.print(FEEDID);
    client.println(".csv HTTP/1.1");
    client.println("Host: api.pachube.com");
    client.print("X-PachubeApiKey: ");
    client.println(APIKEY);
    client.print("User-Agent: ");
    client.println(USERAGENT);
    client.print("Content-Length: ");

    // calculate the length of the sensor reading in bytes:
    // 8 bytes for "sensor1," + number of digits of the data:
    int thisLength = 145;
    client.println(thisLength);

    // last pieces of the HTTP PUT request:
    client.println("Content-Type: text/csv");
    client.println("Connection: close");
    client.println();

    // here's the actual content of the PUT request:
    client.print("xDigital_Temperature,");
    client.println(thisData1);
    
    
    client.print("xDigital_Humidity,");
    client.println(thisData2);
    
    
    client.print("xLight_Analog,");
    client.println(thisData3);
    
    
    client.print("xLight_Digital,");
    client.println(thisData4);
       
    
    client.print("zBody_Detection,");
    client.println(thisData5);
    
    
    client.print("zFlame,");
    client.println(thisData6);
    
    
    client.print("zGas_MQ2,");
    client.println(thisData7);
    
    
    client.print("zSound,");
    client.println(thisData8);
  
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


void getData()
{
 if (client.connect(server, 80)) {
    Serial.println("connected");
    // Make a HTTP request:
    client.print("GET /v2/feeds/");
    client.print(FEEDID);
    client.println(".xml HTTP/1.1");
    client.println("Host: api.cosm.com");
    //client.println("Accept: */*");
    client.print("X-ApiKey: ");
    client.println(APIKEY);
    Serial.println("connecting...ok");
    client.println();
  } 
  else {
    // kf you didn't get a connection to the server:
    Serial.println("connection failed");
    Serial.println();
    Serial.println("disconnecting.");
    client.stop();
  }
  
  if (client.connected()) {
    int ret = client.read();
    Serial.println(ret);
    Serial.println("Connected");
    
      if(finder.find("HTTP/1.1") && finder.find("200 OK"))
      {
        Serial.println("Get Find ");
        int result = processFeed(12);
      }
      else
        Serial.println("Dropping connection - no 200 OK");
      }
  else {
    Serial.println("Disconnected");
  }
  lastConnectionTime = millis();
  }
  
int processFeed(int streamCount)
{
  finder.find("<environment updated=");
  char findBuffer [12]; 
  float findSensorValue[12];
  finder.getString("T", "\"",findBuffer, sizeof(findBuffer)); // get the time
  Serial.print("Values updated at ");
  Serial.println(findBuffer);
  int processed = 0;
  for(int i = 0; i < streamCount; i++)
  {
    if( finder.find( "<data id=" ) ) //find next data field
    {
      if(finder.find("<current_value ") )
      {
        finder.find(">"); // seek to the angle brackets
        findSensorValue[i] = finder.getFloat();
        Serial.println(findSensorValue[i]);
        processed++;
      }
    }
  else {
    Serial.print("unable to find Id field ");
    Serial.println(i);
    }
  }
  if(findSensorValue[5] == 1)
  {
    digitalWrite(RELAY,HIGH);
    Serial.println("ON");
  }
  else if (findSensorValue[5] == 0)
  {
    digitalWrite(RELAY,LOW);
    Serial.println("OFF");
  }
  client.flush();
  lastConnectionTime = millis();
  Serial.print(millis());
  return(processed == streamCount ); // return true iff got all data
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
// This method calculates the number of digits in the
// sensor reading.  Since each digit of the ASCII decimal
// representation is a byte, the number of digits equals
// the number of bytes:

/*int getLength(int someValue) {
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
}*/

