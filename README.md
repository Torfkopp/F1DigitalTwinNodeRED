# F1DigitalTwin

A simple simulation of an F1 car during a race to show the usage of Digital Twins in an F1 setting.
Unlike in the other programme (https://github.com/Torfkopp/F1DigitalTwin), here the Digital Twin is outsourced and implemented in NodeRED.

How to run:
- Run NodeRED with the flows from the "NodeRed" folder
- Run an mqtt-broker like mosquitto with localhost:1883 (or change the number in the NodeRedCommunication class AND the NodeRED-mqtt-in-Nodes)
- Run the Application class

                       __
                   _.--""  |
    .----.     _.-'   |/\| |.--.
    |jrei|__.-'   _________|  |_)  _______________  
    |  .-""-.""""" ___,    `----'"))   __   .-""-.""""--._  
    '-' ,--. `    |tic|   .---.       |:.| ' ,--. `      _`.
     ( (    ) ) __|tac|__ \\|// _..--  \/ ( (    ) )--._".-.
      . `--' ;\__________________..--------. `--' ;--------'
       `-..-'                               `-..-'
