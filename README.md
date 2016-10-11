# FakeSensors - Easily simulate http and mqtt data sources

This is a very simple application that generates arbitrary fake values (e.g. coming from sensors) and serves them via http and mqtt.
I use it to simulate analytics scenarios, but it can be adapted for any other purpose where fake data sources are needed.

**Implementation details:**

* Build with Java
* Spark framework (http://sparkjava.com/) for http requests
* Moquette as a mqtt broker (http://andsel.github.io/moquette/)
* Ecplise Paho as mqtt client (to publish messages) (https://eclipse.org/paho/)

**Features:**

* configurable via simple json file
* easily extensible (Add your own field types and custom value generators)
* provides values via a http interface and/or mqtt broker
* simulate as many sensors/data sources as you need with only one running instance

### Get started

**1.  Configuration**

Copy the config.json.sample in the root directory and rename it to config.json. Find more information about the config.json below.

**2.  Run the application**

After you set up your config.json you simply have to start the app with: `javac Main.java`. Then you can access your sensors easily via http or mqtt

**2.1. Access via http**

You can easily access the data sources with your browser at *http://localhost:4567/get/##sensorid##*. The ##sensorid## part has to be replaced with the id you choose in your config.json e.g. http://localhost:4567/get/thermometer.
The response will be a json string similar to something like this:
`{"response":[{"temperature":22,"humidity":8}],"status":"success"}`

The status value shows if the query worked at all (success or error) and response array returns the actual values as an JSON array.

**2.2. Access via mqtt**

Info about MQTT: MQTT is a light-weight message protocol often used in the Internet of Things context. A message approach always contains two components A) a message broker that receives and distributes the messages and B) one or many message clients that publish messages by sending them to the broker and read messages from the broker (by subscribing to topics).

This application provides a broker and a client. However, you can also use your own broker by changing the ip in the *config.json*. The published messages will be printed in the console. Alternatively you can subscribe to the sensors with any mqtt client (e.g. mqttfx http://www.jensd.de/apps/mqttfx/). Therefore use the local IP *tcp://0.0.0.0:1883* and use the sensor name defined in the config.json as topic.


### Configuration (config.json)

#### == General configuration ==
The main configuration contains the following elements

**1. mqtt_broker** here you can define the mqtt broker. Default is a local embedded broker *tcp://0.0.0.0:1883*

**2. sensors** json array that contains the actual sensors and its fields (see below)

#### == Sensor configuration ==
A sensor can have the following configurations

**1. id** this is the unique identifier of a sensor/data source and also represents the url part to call it later. (use only url valid characters - no spaces or special characters)

**2. publish_intervall** defines the interval in milliseconds (e.g. 5000 = 5s) how often a message for this sensor will be published

**3. fields:** A json array of the fields that the data source incorporates. A more detailed list of possible field types is depicted below.


#### == Fields ==
Currently the following field types are implemented.

**1. IntegerField** Simple integer field that returns a full number without decimal places (e.g. 42)

**2. StringField** Simple String field that returns a text value

A field always incorporates a generator that generates values (e.g. a random number in a range). Find a list of available generators below.

#### == Generators ==
Currently the following value generators are implemented

**1. DefaultGenerator** Fallback generator that simply returns a random integer value between 0 and 100

**2. SimpleRangeIntegerGenerator** Simple integer generator that returns a random integer between a given range. To define the range use the additional fields *range_from* and *range_to*

**3. SimpleStringValueGenerator** Simple string generator that just retuns a given text. To define the text use the additional field *value*

### Contribute and future work
This project is part of an ongoing research at the University of Stuttgart and will be a tool to simulate analytics scenarios in the context of the Internet of Things. Feel free to use this generator for any purpose or to contribute and with enhancements or new field types or generators.
If you have any questions don't hesitate to contact me or use the Github issue tracker.

#### ToDo
- Add new field types and generators
- Make value generation consistent over time so that you can get a value for a certain timestamp
- Add vagrant/docker files to easily deploy in virtual environments
- Make the application more robust by adding fallbacks and better exception handling