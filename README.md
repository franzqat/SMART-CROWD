# SMART CROWD

Questa applicazione consente di misurare il numero di utenti connessi in un'area, basandosi su una scansione della rete WiFi della zona.


## Linguaggi e strumenti utilizzati
* Publisher sviluppato in Python3
* Subscriber in Android - Java


## Usage
Occorre configurare lo script in python dal Raspberry Pi per concordare il topic sul quale si desidera pubblicare e successivamente avviarlo.
Il client si sottoscrive switchando il bottone corrispondente al topic desiderato, se presente, o può aggiungerne uno custom conoscendo il topic.

## Descrizione dettagliata

L'architettura sfrutta il protocollo di comunicazione MQTT:
* Il publisher sfrutta uno script in python per pubblicare i contenuti dell'analisi della rete. L'analisi viene effettuata tramite il tool arp-scan.
* Il broker è in una piattaforma cloud di terze parti: https://www.cloudmqtt.com
* Il subscriber è stato sviluppato in un'applicazione Android attraverso la quale è possibile sottoscriversi ai topic e conoscere il numero delle persone presenti in un'area.

## Dipendenze
Occorre avere installato, nell'ambiente dove si desidera far funzionare lo script python, le seguenti dipendenze:
```
arp-scan
paho-mqtt
```
È possibile installarle tramite il comando pip install.
