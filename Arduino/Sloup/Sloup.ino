void setup() {
  pinMode(10,OUTPUT);

}

void loop() {
  int i;
  i = analogRead(0);
  if(i > 512)
    digitalWrite(10,HIGH);
  else
    digitalWrite(10,LOW);
}
