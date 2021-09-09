#include<SoftwareSerial.h>

SoftwareSerial BTSerial(2,3);

void setup(){
  Serial.begin(9600);
  BTSerial.begin(9600);
}

void loop()
{
  
  if(BTSerial.available() > 0)
  {
    byte data = BTSerial.read();
    Serial.write(data);
  }
  if(Serial.available() > 0)
  {
    byte data = Serial.read();
    if(data == 'Q')
    {
      BTSerial.write("AT+IBE0FFFFFFEE");
      delay(3000);
      BTSerial.write("AT+IBE012312312");
    }
    else
    {
      BTSerial.write(data);
    }
  }
}
