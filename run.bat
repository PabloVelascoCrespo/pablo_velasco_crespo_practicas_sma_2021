javac -classpath "lib/jade.jar;lib/jsoup-1.14.3.jar;lib\mail.jar" -Xlint:deprecation -d classes src/chatbot/*.java
java jade.Boot -agents "emisor:chatbot.Emisor;receptor:chatbot.Receptor"