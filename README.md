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

The admin page is located at :

<http://localhost:9990/admin>

For more information and documentation about twitter-server :

<http://twitter.github.io/twitter-server/>
