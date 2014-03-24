language-detector-server
========================

The purpose of this service is to provide a simple JSON webservice to the language detection java library <https://code.google.com/p/language-detection/>


Quick-start
-----------

To generate the project jar :

    $ ./sbt assembly

Then, to start the server :

    $ java -jar target/scala-2.10/language-detector-server-assembly-0.1.0.jar

There are other options available :

    $ java -jar target/scala-2.10/language-detector-server-assembly-0.1.0.jar -help

To query the endpoint just POST a JSON query to :
    
    http://localhost:8080/lang-detector

The JSON query format is the following :

    [{"id": "request-id", "text": "Text to detect"}, ...]

The JSON response format is the following :

    [{"id": "request-id", result: [{"lang": "en", "prob": "0.999"},...]}, ...]

Here is a request example using curl and python for JSON pretty formating :

    $ curl -s -d '[{"id":"test-fr","text":"Bonjour tout le monde"}, {"id":"test-en","text":"My name is Pascal"}]' http://localhost:8080/lang-detector | python -mjson.tool

The admin page is located at :

<http://localhost:9990/admin>

For more information and documentation about twitter-server :

<http://twitter.github.io/twitter-server/>
