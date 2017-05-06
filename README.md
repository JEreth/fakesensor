# FakeSensors - Easily simulate http and mqtt data sources

This is a very simple application that generates arbitrary fake values (e.g. coming from sensors) and serves them via http and mqtt.
I use it to simulate analytics scenarios, but it can be adapted for any other purpose where fake data sources are needed.

**Implementation details:**

* Build with Java
* Spark framework (http://sparkjava.com/) for http requests
* Moquette as a mqtt broker (http://andsel.github.io/moquette/)
* Ecplise Paho as mqtt client (to publish messages) (https://eclipse.org/paho/)
* Apache Kafka support (https://kafka.apache.org/)

**Features:**

* configurable via simple json file
* easily extensible (Add your own field types and custom value generators)
* provides values via a http interface, mqtt broker or a kafka topic
* simulate as many sensors/data sources as you need with only one running instance

### Get started

**1.  Configuration**

Copy the config.json.sample in the root directory and edit it as you need. Find more information about the config.json below.

**2.  Run the application**

You can run the application easily with the executable jar in the bin directory with `java -jar bin/fakesensor.jar` from the root directory (take care that a bin/config.json file exists).
Alternatvely you can build and run the application from source

As soon as the application is running you can access your sensors easily via http or mqtt:

**2.1. Access via http**

You can easily access the data sources with your browser at *http://localhost:4567/get/##sensorid##*. The ##sensorid## part has to be replaced with the id you choose in your config.json e.g. http://localhost:4567/get/thermometer.
The response will be a json string similar to something like this:
`{"response":[{"temperature":22,"humidity":8}],"status":"success"}`

The status value shows if the query worked at all (success or error) and response array returns the actual values as an JSON array.

**2.2. Access via mqtt**

More about MQTT: MQTT is a light-weight message protocol often used in the Internet of Things context. A message approach always contains two components A) a message broker that receives and distributes the messages and B) one or many message clients that publish messages by sending them to the broker and read messages from the broker (by subscribing to topics).

This application provides a broker and a client. However, you can also use your own broker by changing the ip in the *config.json*. The published messages will be printed in the console. Alternatively you can subscribe to the sensors with any mqtt client (e.g. mqttfx http://www.jensd.de/apps/mqttfx/). Therefore use the local IP *tcp://0.0.0.0:1883* and use the sensor name defined in the config.json as topic.

**2.3 Accsess via kafka**

You can use the kafka streaming platform to publish messages that can be subscribed by arbitrary sources. Learn more at https://kafka.apache.org/.
 
The default kafka server is *localhost:9092* but you can change that in the *config.json*. You can easily run a local test environment (see kafka docs) or use a docker image (e.g. https://github.com/spotify/docker-client). It is also possible to use a remote kafka cluster (e.g. on AWS https://aws.amazon.com/kafka/).

### Configuration (config.json)

#### == General configuration ==
The main configuration contains the following elements

**1. mode** sets the way you access the values. Current modes are csv, http, mqtt or kafka. If not set csv is used as default.

**2. mqtt_broker** (optional) here you can define the mqtt broker. Default is a local embedded broker *tcp://0.0.0.0:1883*

**3. kafka_server** (optional) here you cn define the kafka server that receives the messages. Default is a local installation *localhost:9092*

**4. sensors** json array that contains the actual sensors and its fields (see below)

#### == Sensor configuration ==
A sensor can have the following configurations

**1. id** this is the unique identifier of a sensor/data source and also represents the url part to call it later. (use only url valid characters - no spaces or special characters)

**2. publish_intervall** defines the interval in milliseconds (e.g. 5000 = 5s) how often a message for this sensor will be published

**3. fields:** A json array of the fields that the data source incorporates. A more detailed list of possible field types is depicted below.


#### == Fields ==
Currently the following field types are implemented.

**1. IntegerField** Simple integer field that returns a full number without decimal places (e.g. 42)

**2. StringField** Simple String field that returns a text value

**3. DoubleField** Simple double field that returns a number with decimal places

A field always incorporates a generator that generates values (e.g. a random number in a range). Find a list of available generators below.

#### == Generators ==
Currently the following value generators are implemented

**1. DefaultGenerator** Fallback generator that simply returns a random integer value between 0 and 100

**2. SimpleRangeIntegerGenerator** Simple integer generator that returns a random integer between a given range. To define the range use the additional fields *range_from* and *range_to*

**3. SimpleStringValueGenerator** Simple string generator that just retuns a given text. To define the text use the additional field *value*

**4. SimpleRangeDoubleGenerator** Simple double generator that returns a random integer between a given range. To define the range use the additional fields *range_from* and *range_to* with decimal numbers. Additionally you can define the field *decimal_places* for a custom rounding (default 2 decimal places)


### Contribute and future work
This project is part of an ongoing research at the University of Stuttgart and will be a tool to simulate analytics scenarios in the context of the Internet of Things. Feel free to use this generator for any purpose or to contribute and with enhancements or new field types or generators.
If you have any questions don't hesitate to contact me or use the Github issue tracker.

#### ToDo
- Add new field types and generators
- Make value generation consistent over time so that you can get a value for a certain timestamp
- Add vagrant/docker files to easily deploy in virtual environments
- Make the application more robust by adding fallbacks and better exception handling
- Add simulation mode to quickly generate a lot of data (currently everything happens in real-time)