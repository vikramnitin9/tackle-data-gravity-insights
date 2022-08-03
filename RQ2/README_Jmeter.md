# Mono2Micro Jmeter 

Please read the documentation at https://jmeter.apache.org/ to learn more about Apache Jmeter to load test functional behavior and measure performances.

## Install Apache Jmeter
https://jmeter.apache.org/download_jmeter.cgi

## Open the Apache Jmeter UI
Cd bin
sh jmeter.sh 

Load the .jmx file and run the Jmeter test. 

## Running Jmeter from command line

The script has the following options:
	-JHOST	    The name of the machine running the DayTrader Application. The default is localhost.
	-JPORT	    The HTTP port of the server running the DayTrader Application. The default is 9080.
	-JPROTOCOL  The transport either http or https
	-JTHREADS   The number of jmeter threads to start. The default is 50.
	-JRAMP		The ramp up time for starting the threads. Set this to the same value as -JTHREADS for a smoother startup. The default is 0.
	-JDURATION  The time (in seconds) to run jmeter.
	-JMAXTHINKTIME The time in milliseconds to wait between each call. The default is 0 ms
	-JSTOCKS    The total amount of stocks/quotes in the database, minus one. The default is 9999, which assumes there are 10,000 stocks in the database.
	-JBOTUID    The lowest user id. The default is 0.
	-JTOPUID    The highest user id. The default is 14999, which assumes there are 15,000 users in the database.
	
Example: 
jmeter -n -t daytrader7.jmx -JHOST=localhost -JPORT=9082 -JPROTOCOL=http -JMAXTHINKTIME=100 -JDURATION=300



To see output every five seconds from JMeter, edit the following section in <JMETER_HOME>/bin/jmeter.properties

