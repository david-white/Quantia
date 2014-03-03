
This **example** project is for use with ESP 5.1 SP03.  It has been tested under Windows 7 with the the ESP project folder located at C:\Sybase\ESP-5_1\adapters\framework\examples\rabbitmq.

This is a simple example project and not inteneded as a production type project.  This example project is verbose in output to both the screen and log files.  **Caveat emptor...**

Thank you Author mdai for all the example code.  I used a great deal of your example code in this RabbitMQ example.


The example external adapter tranporter module (RabbitMQExampleInputTransporter) dequeues data from the RabbitMQ queue and sends data to the formatter module (RabbitMQExampleInputFormatter). The formatter module then parses the data and by way of the espconnector module publishes that data into an ESP input stream in the rabbitmq project.

Manifest

c:\pub1747\SAP\RabbitMQExampleAdapterESP51>dir /od

Volume in drive C has no label.

Volume Serial Number is 4A53-A693

Directory of c:\pub1747\SAP\RabbitMQExampleAdapterESP51

11/15/2013  02:30 PM           786,129 building_custom_adapters.pdf

11/16/2013  11:29 AM               580 csi_local.xml

11/25/2013  09:21 PM             5,047 modulesdefine.xml

11/26/2013  02:50 PM    <DIR>          testJars

11/27/2013  12:55 PM             3,198 README.txt

11/27/2013  01:10 PM    <DIR>          RabbitMQExampleInputTransporter

11/27/2013  01:10 PM    <DIR>          RabbitMQExampleInputFormatter

11/27/2013  01:11 PM    <DIR>          rabbitmq

11/27/2013  01:12 PM    <DIR>          DfwTrafficSend

11/27/2013  01:16 PM           988,560 RabbitMQExampleAdapterESP51.pdf

11/27/2013  01:16 PM    <DIR>          .

11/27/2013  01:16 PM    <DIR>          ..

5 File(s)      1,783,514 bytes

7 Dir(s)  308,761,505,792 bytes free

c:\pub1747\SAP\RabbitMQExampleAdapterESP51>



The RabbitMQExampleAdapterESP51.pdf file is the main document for this example.

The modulesdefine.xml file has the changes for RabbitMQExampleInputTransporter and RabbitMQExampleInputFormatter.

The csi_local.xml file is the file for the Security folder.

The testJars folder has a working DfwTrafficSend.jar to enqueue data into RabbitMQ.

The RabbitMQExampleInputTransporter folder has the transporter Eclipse project.

The RabbitMQExampleInputFormatter folder has the formatter Eclipse project.

The DfwTrafficSend folder has the DfwTrafficSend Eclipse project.

The rabbitmq folder is the ESP project.


c:\Sybase\ESP-5_1\adapters\framework\examples\rabbitmq>dir /od
 Volume in drive C has no label.
 Volume Serial Number is 4A53-A693

 Directory of c:\Sybase\ESP-5_1\adapters\framework\examples\rabbitmq

08/01/2013  04:25 PM               575 stop_project.bat
08/01/2013  04:25 PM               168 discover_adapter.bat
08/01/2013  04:25 PM               228 start_adapter.bat
08/01/2013  04:25 PM               721 start_project.bat
08/01/2013  04:25 PM               227 stop_adapter.bat
11/17/2013  12:40 PM               471 .project
11/18/2013  12:39 PM             1,111 set_example_env.bat
11/21/2013  08:13 PM               126 start_node.bat
11/21/2013  08:16 PM               394 subscribe.bat
11/25/2013  10:03 AM    <DIR>          bin
11/25/2013  08:29 PM             1,257 log4j.properties
11/25/2013  08:30 PM             1,849 ExampleAdapterForRabbitMQ.cnxml
11/25/2013  08:35 PM             1,873 adapter_config.xml
11/25/2013  08:35 PM             1,089 rabbitmq.ccl
11/27/2013  12:23 PM    <DIR>          ..
11/27/2013  12:23 PM    <DIR>          .
11/27/2013  12:38 PM    <DIR>          logs
11/27/2013  12:41 PM            13,655 rabbitmq.ccx
              14 File(s)         23,744 bytes
               4 Dir(s)  308,768,956,416 bytes free

c:\Sybase\ESP-5_1\adapters\framework\examples\rabbitmq>
