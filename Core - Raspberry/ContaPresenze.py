import subprocess
from time import sleep
import re
import paho.mqtt.client as mqtt #import the client1
import time

#Configurazione
broker_address="m24.cloudmqtt.com"
broker_port = 18951
topic = "unict/didattica/aulastudio"
qos = 1
username = "npjfazcj"
password = "H_BXhBstyAU4"

sleeptime_before_result = 5 #secondi
# Sleep once when this script is called to give the Pi enough time
# to connect to the network
#sleep(60)
############
#Functions
############
def on_message(client, userdata, message):
    print("message received " ,str(message.payload.decode("utf-8")))
    print("message topic=",message.topic)
    print("message qos=",message.qos)
    print("message retain flag=",message.retain)
    ########################################
def calcola_result():
    # Assign the output of the arp command executed
    output = subprocess.check_output("sudo arp-scan -l", shell=True)
    # Wait sleeptime seconds between scans
    sleep(sleeptime_before_result)

    #Genera in output una lista di valori che seguono il pattern del MAC address come specificato nella variabile p tramite un regex.
    #print(len([x.group() for x in p.finditer(output.decode("utf-8"))]))
    #restituisce la dimensione della lista generata
    result = [x.group() for x in p.finditer(output.decode("utf-8"))]
    #la conversione a set consente di eliminare i duplicati
    myset = set(result)
	print (myset)
	return str(len(myset))
  
# Main thread

p = re.compile(r'\b(?:\:?[a-f0-9]{2}){6}\b')

try:
    

    print("creating new instance")
    client = mqtt.Client("P1") #create new instance
    client.on_message=on_message #attach function to callback
    print("connecting to broker")
    client.username_pw_set(username, password)
    client.connect(broker_address, broker_port) #connect to broker
    client.loop_start() #start the loop
 #   print("Subscribing to topic",topic)
 #   client.subscribe(topic)
    while True:
        result = calcola_result()
        print("Publishing message to topic",topic)
        client.publish(topic,result,qos)
        print("Sent! " + result)
        time.sleep(10) # wait
    #client.loop_stop() #stop the loop
   # client.loop()



except KeyboardInterrupt:
    # On a keyboard interrupt signal threads to exit
    exit()

