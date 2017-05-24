all:
	mkdir -p classes
	javac -d classes -classpath .:lib/ch.qos.logback.classic-0.9.28.jar:lib/libthrift-0.9.1.jar:lib/logback-classic-1.0.13.jar:lib/logback-core-1.0.13.jar:lib/slf4j-api-1.7.21.jar:lib/slf4j-simple-1.7.21.jar src/server/*.java src/client/Client.java src/util/*.java
	cp manifest_server.txt manifest_client.txt ./classes
	cd classes && \
	jar cvfm server.jar manifest_server.txt server/*.class && \
	jar cvfm client.jar manifest_client.txt client/*.class && \
	mv server.jar client.jar ../

clean:
	rm -rf classes
	rm server.jar client.jar
