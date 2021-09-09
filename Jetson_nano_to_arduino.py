import serial
import time

arduino= serial.Serial('/dev/ttyACM0',9600)
Trans="Q"
Trans= Trans.encode('utf-8')

while 1:
    f = open("send.txt",'r')
    line = f.readline()
    
    if line == 'kidnap':
       arduino.write(Trans)
       print(line)
       time.sleep(11)








