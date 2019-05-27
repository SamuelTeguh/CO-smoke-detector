#include <FS.h>
#include <ESP8266WiFi.h>
#include <WiFiClient.h>
#include <ESP8266WebServer.h>
#include <Wire.h>
#include <ArduCAM.h>
#include <SPI.h>
#include <FirebaseArduino.h>
#include <FirebaseObject.h>
#include <NTPtimeESP.h>
#include <MQ2.h>
#include "memorysaver.h"

const int CS = 16;

// if the video is chopped or distored, try using a lower value for the buffer
// lower values will have fewer frames per second while streaming
static const size_t bufferSize = 4096; // 4096; //2048; //1024;

static const int fileSpaceOffset = 700000;

static const int wifiType = 0;

const char *ssid = "WIFI"; 
const char *password = "password"; 

#define FIREBASE_HOST ""
#define FIREBASE_AUTH ""

int sensorAsap = A0;
int co, smoke;
String capture = "1";
NTPtime NTPch("0.id.pool.ntp.org"); 
String date;
String dayformat;
String hours;
String image;
String imageUrl;
String coValue;
String smokeValue;

MQ2 mq2(sensorAsap);
strDateTime dateTime;

IPAddress ip;

const String fName = "res.txt";

int fileTotalKB = 0;
int fileUsedKB = 0; int fileCount = 0;
String errMsg = "";
int imgMode = 1; // 0: stream  1: capture
int resolution = 2;
// resolutions:
// 0 = 160x120
// 1 = 176x144
// 2 = 320x240
// 3 = 352x288
// 4 = 640x480
// 5 = 800x600
// 6 = 1024x768
// 7 = 1280x1024
// 8 = 1600x1200

ESP8266WebServer server(80);

ArduCAM myCAM(OV2640, CS);

void myCAMSaveToSPIFFS() {

  if ((fileTotalKB - fileUsedKB) < fileSpaceOffset)
  {
    String maxStr = "====== Maximum Data Storage Reached =========";
    Serial.println(maxStr);
    errMsg = maxStr;
    return;
  }

  String str;
  byte buf[256];
  static int i = 0;

  static int n = 0;
  uint8_t temp, temp_last;

  //  File file;
  //Flush the FIFO
  myCAM.flush_fifo();
  //Clear the capture done flag
  myCAM.clear_fifo_flag();
  //Start capture
  myCAM.start_capture();

  while (!myCAM.get_bit(ARDUCHIP_TRIG , CAP_DONE_MASK));
  Serial.println("File Capture Done!");

  fileCount++;

  str = "/pics/" + image + ".jpg";

  File f = SPIFFS.open(str, "w");
  if (!f) {
    Serial.println("prop file open failed");
  }
  else
  {
    Serial.println(str);
  }


  i = 0;
  myCAM.CS_LOW();
  myCAM.set_fifo_burst();
#if !(defined (ARDUCAM_SHIELD_V2) && defined (OV2640_CAM))
  SPI.transfer(0xFF);
#endif
  //Read JPEG data from FIFO
  while ( (temp != 0xD9) | (temp_last != 0xFF)) {
    temp_last = temp;
    temp = SPI.transfer(0x00);

    //Write image data to buffer if not full
    if ( i < 256)
      buf[i++] = temp;
    else {
      //Write 256 bytes image data to file
      myCAM.CS_HIGH();
      f.write(buf , 256);
      i = 0;
      buf[i++] = temp;
      myCAM.CS_LOW();
      myCAM.set_fifo_burst();
    }
  }

  //Write the remain bytes in the buffer
  if (i > 0) {
    myCAM.CS_HIGH();
    f.write(buf, i);
  }
  //Close the file
  f.close();
  Serial.println("CAM Save Done!");
}


bool loadFromSpiffs(String path) {
  String dataType = "text/plain";
  if (path.endsWith("/")) path += "index.htm";

  if (path.endsWith(".src")) path = path.substring(0, path.lastIndexOf("."));
  else if (path.endsWith(".jpg")) dataType = "image/jpeg";
  File dataFile = SPIFFS.open(path.c_str(), "r");
  if (server.hasArg("download")) dataType = "application/octet-stream";

  if (server.streamFile(dataFile, dataType) != dataFile.size()) {
  }

  dataFile.close();
  return true;
}

void start_capture() {
  myCAM.clear_fifo_flag();
  myCAM.start_capture();
}
void handleNotFound() {

  String ipStr = WiFi.localIP().toString();
  if (wifiType == 1) // AP mode = 1
  {
    ipStr = WiFi.softAPIP().toString();
  }


  Serial.print("server uri: " ); Serial.println(server.uri());
  // if url contains request for stored image: http://IPaddress/pics/2.jpg
  if (server.uri().indexOf(".jpg") != -1 )
  {
    loadFromSpiffs(server.uri());
    return;
  }

  //// default HTML
  String message = "<html><head>\n";
  message += "</head><body>\n";
  message += "<form action=\"http://" + ipStr + "/submit\" method=\"POST\">";
  message += "<h1>Smoke Detector Cam</h1>\n";
  if (errMsg != "")
    message += "<div style=\"color:red\">" + errMsg + "</div>";


  if (imgMode == 0) // stream mode
  {
    message += "<div><h2>Video Streaming</h2></div> \n";
    message += "<div><img id=\"ArduCam\" src=\"http://" + ipStr + "/stream\" ></div>\n";
    imgMode = 1; // set back to capture mode so it doesn't get stuck in streaming mode

  }
  else
  {
    

    

    message += " <input type=\"radio\" id=\"capt\" name=\"imgMode\" value=\"capture\"  ";
    if (imgMode == 1)
      message += " checked ";
    message += "> Capture \n";

    message += "&nbsp; <input type='submit' value='Submit'  >\n";
    message += " &nbsp;  <a style=\"font-size:12px; font-weight:bold;\" href=\"http://" + ipStr + "\">Refresh</a>";
    message += " &nbsp; &nbsp; <a style=\"font-size:12px; font-weight:bold;\" onclick=\"return confirm('Are you sure? This will delete all stored images.')\" ";
    message += " href=\"http://" + ipStr + "/clear\">Clear Data</a>\n";

    message += "</div>\n";


    ///////////////////////////////
    FSInfo fs_info;
    SPIFFS.info(fs_info);

    fileTotalKB = (int)fs_info.totalBytes;
    fileUsedKB = (int)fs_info.usedBytes;


    if (fileCount > 0)
    {
      int percentUsed = ((float)fileUsedKB / (float)(fileTotalKB - fileSpaceOffset)) * 100;
      String colorStr = "green";
      if (percentUsed > 90)
        colorStr = "red";

      message += "<div style=\"width:450px; background-color:darkgray; padding:1px;\">";
      message += "<div style=\"position:absolute; color:white; padding-top:2px; font-size:11px;\"> &nbsp; space used: " + String(percentUsed) + "%</div>";
      message += "<div style=\"width:" + String(percentUsed) + "%; height:16px; background-color: " + colorStr + ";\"></div></div>\n";

    }

    message += "<table><tr>";
    int colCnt = 0;
    for (int i = 1; i <= fileCount; i++)
    {
      message += "<td><a href=\"/pics/" + image + ".jpg\">" + i + ".jpg</a></td>\n";

      colCnt++;
      if (colCnt >= 10) //  columns
      {
        message += "</tr><tr>";
        colCnt = 0;
      }
    }
    message += "</tr></table>\n";




    //useful for debugging max data storage
    /*
      message += "<table><tr><td>Total Bytes: </td><td style=\"text-align:right;\">";
      message += fileTotalKB;
      message += "</td></tr><tr><td>Used Bytes: </td><td style=\"text-align:right;\">";
      message += fileUsedKB;
      message += "</td></tr><tr><td>Remaing Bytes: </td><td style=\"text-align:right;\">";
      message += (fileTotalKB - fileUsedKB);
      message += "</td></tr></table>\n";
    */
    /*
      float flashChipSize = (float)ESP.getFlashChipSize() / 1024.0 / 1024.0;
      message += "<br>chipSize: ";
      message += flashChipSize;
    */

    message += "<div><img id=\"ArduCam\" src=\"http://" + ipStr + "/capture\" ></div>\n";


  }

  message += "</form> \n";
  message += "</body></html>\n";

  server.send(200, "text/html", message);

}

void clearData()
{
  errMsg = "======  Data Storage Cleared =========";
  Dir dir = SPIFFS.openDir("/pics");
  while (dir.next()) {
    SPIFFS.remove(dir.fileName());
  }

  fileCount = 0;
  fileTotalKB = 0;
  fileUsedKB = 0;

  handleNotFound();
}

void getData(){
  String path =  "path";
  FirebaseObject object = Firebase.get(path);
  coValue = object.getString("coValue");
  smokeValue = object.getString("smokeValue");
}

void setup() {
  SPIFFS.begin();
  delay(10);
  // check for properties file
  File f = SPIFFS.open(fName, "r");

  if (!f) {
    // no file exists so lets format and create a properties file
    Serial.println("Please wait 30 secs for SPIFFS to be formatted");

    SPIFFS.format();

    Serial.println("Spiffs formatted");

    f = SPIFFS.open(fName, "w");
    if (!f) {
      Serial.println("properties file open failed");
    }
    else
    {
      // write the defaults to the properties file
      Serial.println("====== Writing to properties file =========");
      f.println(resolution);
      f.close();
    }
  }
  else
  {
    // if the properties file exists on startup,  read it and set the defaults
    Serial.println("Properties file exists. Reading.");

    while (f.available()) {

      // read line by line from the file
      String str = f.readStringUntil('\n');

      Serial.println(str);

      resolution = str.toInt();

    }

    f.close();
  }


  uint8_t vid, pid;
  uint8_t temp;

/*#if defined(__SAM3X8E__)
  Wire1.begin();
#else
  Wire.begin();
#endif
*/
  Serial.begin(115200);
  Serial.println("ArduCAM Start!");

  // set the CS as an output:
  pinMode(CS, OUTPUT);

  // initialize SPI:
  SPI.begin();
  SPI.setFrequency(4000000); //4MHz

  //Check if the ArduCAM SPI bus is OK
  myCAM.write_reg(ARDUCHIP_TEST1, 0x55);
  temp = myCAM.read_reg(ARDUCHIP_TEST1);
  if (temp != 0x55) {
    Serial.println("SPI1 interface Error!");
  }

  //Check if the camera module type is OV2640
  myCAM.wrSensorReg8_8(0xff, 0x01);
  myCAM.rdSensorReg8_8(OV2640_CHIPID_HIGH, &vid);
  myCAM.rdSensorReg8_8(OV2640_CHIPID_LOW, &pid);
  if ((vid != 0x26 ) && (( pid != 0x41 ) || ( pid != 0x42 )))
    Serial.println("Can't find OV2640 module! pid: " + String(pid));
  else
    Serial.println("OV2640 detected.");


  //Change to JPEG capture mode and initialize the OV2640 module
  myCAM.set_format(JPEG);
  myCAM.InitCAM();

  myCAM.OV2640_set_JPEG_size(OV2640_320x240);

  myCAM.clear_fifo_flag();

  
    // Connect to WiFi network
    Serial.println();
    Serial.println();
    Serial.print("Connecting to ");
    Serial.println(ssid);

    WiFi.mode(WIFI_STA);

    WiFi.begin(ssid, password);

    while (WiFi.status() != WL_CONNECTED) {
      delay(500);
      Serial.print(".");
    }

    //WiFi.config(ip, gateway, subnet); // remove this line to use DHCP

    Serial.println("WiFi connected");
    Serial.println("");
    Serial.print("ip: ");
    ip = WiFi.localIP();
    Serial.println(ip);
  
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  mq2.begin();

  // setup handlers
  
  server.on("/clear", clearData);
  server.onNotFound(handleNotFound);
  server.begin();
  Serial.println("Server started");


  Dir dir = SPIFFS.openDir("/pics");
  while (dir.next()) {
    fileCount++;
  }

  FSInfo fs_info;
  SPIFFS.info(fs_info);

  fileTotalKB = (int)fs_info.totalBytes;
  fileUsedKB = (int)fs_info.usedBytes;

}

void loop() {
  dateTime = NTPch.getNTPtime(7.0, 0);
  getData();
  co = mq2.readCO();
  smoke = mq2.readSmoke();
  Firebase.setFloat("CO", co);
  Firebase.setFloat("Smoke", smoke);
  getActivation();
  delay(1000);
  //Firebase.setFloat("sensorValue", sensorAsapValue);
  
  if(co > coValue.toInt() && smoke > smokeValue.toInt() && capture == "1")
  {
      NTPch.printDateTime(dateTime);
      if(String(dateTime.day).length()==1){
        dayformat = "0"+String(dateTime.day);
      }else{
        dayformat = String(dateTime.day);
      }
      date = dayformat + "-" + String(dateTime.month) + "-" + String(dateTime.year);
      hours = String(dateTime.hour) + "." + String(dateTime.minute);
      setDate(date);
      image = date + "_" + hours;
      imageUrl = "http://" + String(ip[0]) + "." + String(ip[1]) + "." + String(ip[2]) + "." + String(ip[3])  + "/pics/" + image + ".jpg";
      StaticJsonBuffer<500> jsonBuffer;
      JsonObject& root = jsonBuffer.createObject();
      String coDValue = String(co);
      String smokeDValue = String(smoke);
      root["coDValue"] = coDValue;
      root["smokeDValue"] = smokeDValue;
      root["date"] = date;
      root["time"] = hours;
      root["url"] = imageUrl;
      String name = Firebase.push("/detectionInfo", root);
      delay(5000);
      myCAMSaveToSPIFFS();
      capture = "0";
      setActivation(capture);
      
  }
    
    delay(1000);
  server.handleClient();
}

void setActivation(String capture){
  Firebase.setString("sensorActivation/capture",capture);
  
}

void getActivation(){
  String path = "sensorActivation";
  FirebaseObject object = Firebase.get(path);
  capture = object.getString("capture");
}

void setDate(String date){
  String path = "Date/"+date;
  FirebaseObject object = Firebase.get(path);
  String flag = object.getString("dateSort");
  String flagCount = object.getString("count");
  if(date == flag){
    int count = flagCount.toInt() + 1;
    String countS = String(count);
    Firebase.setString("Date/"+date+"/count",countS);
  }else{
    Firebase.setString("Date/"+date+"/dateSort",date);
    Firebase.setString("Date/"+date+"/count","1");
  }
  
  
}
