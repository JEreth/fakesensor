# FakeSensors - A simple value generation tool
This is a very simple application that generates arbitrary fake values (e.g. coming from sensors). I use it to simulate analytics scenarios, but it can be adapted for any other purpose where fake data sources are needed.

The application is implemented with Java and uses Spark framework (http://sparkjava.com/) as a simple web server.

**Features:**

* configurable via simple json file
* easily extensible (Add your own field types and custom value generators)
* provides values via a http interface
* simulate as many sensors as you need with one running instance

### Get started

**1.  Configuration**

Copy the config.json.sample in the root directory and rename it to config.json. Each item in the json array respresents a sensor (or any other data source). Currently a sensor has the following requiered values:

* **id:** represents the unique identifier of a sensor/data source and also represents the url part to call it later. (use only url valid characters - no spaces or special characters)
* **fields:** A json array of the fields that the data source incorperates. A more detailed list of possible field types is depicted below.

**2.  Run the application**

After you set up your config.json you simply have to start the app with: `javac Main.java`. Then you can access your sensors easily via your browser via *http://localhost:4567/get/##sensorid##*. The ##sensorid## part has to be replaced with the id you choose in your config.json e.g. http://localhost:4567/get/thermometer.
The response will be a json string similiar to something like this:
`{"response":[{"temperature":22,"humidity":8}],"status":"success"}`

The status value shows if the query worked at all (success or error) and response array returns the actual values as an JSON array.

### Configuration

#### A) Fields
Currently the following field types are implemented

**1. IntegerField** Simple integer field that returns a full number without decimal places (e.g. 42)

**2. StringField** Simple String field that returns a text value

#### B) Generators
Currently the following value generators are implemented

**1. DefaultGenerator** Fallback generator that simply returns an random integer value between 0 and 100

**2. SimpleRangeIntegerGenerator** Simple integer generator that returns a random integer between a given range. To define the regine use the additional fields *range_from* and *range_to*

**3. SimpleStringValueGenerator** Simple string generator that just retuns a given text. To define the text use the additional field *value*

### Contribute and future work
This project is part of an ongoing research at the University of Stuttgart and will be a tool to simulate analytics scenarions in the context of the Internet of Things. Feel free to use this generator for any purpose or to contribute and with enhancments or new field types or generators.
If you have any questiosn don't hesitate to contact me or use the Github issue tracker.

#### ToDo
- Add new field types and generators
- Make value generation consistent over time so that you can get a value for a certain timestamp
- Maybe add other protocols than http (e.g. MQTT)
- Make the application more robuts by adding fallbacks and better exception handling