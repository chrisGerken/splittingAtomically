mvn -e exec:java -Dexec.classpathScope=compile -Dexec.mainClass=org.atomsg.topology.AtomicMessagesTopology -Djava.util.logging.config.file=src/main/resources/logging.properties

#To execute in production
#storm jar atomic-messages-storm-1.0.0-SNAPSHOT-jar-with-dependencies.jar org.atomsg.topology.AtomicMessagesTopology prod -Djava.util.logging.config.file=prod.logging.properties

