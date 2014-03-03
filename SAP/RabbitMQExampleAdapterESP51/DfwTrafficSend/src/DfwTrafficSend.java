/*
 * 
	C:\Users\david\workspace\testJars>java -jar DfwTrafficSend.jar
 *
 */
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;

public class DfwTrafficSend {

  private final static String REAL_DATA_QUEUE_NAME = "DfwTrafficRealDataQueue";
  private final static String REAL_DATA_NODENAME = "Datum";
  private static ArrayList<String> detectorData = new ArrayList<String>();
  private static StringBuilder sbAccum = new StringBuilder();
  private static long messagesSent = 1;

  private static class DfwTrafficFetchToRabbitMq implements Runnable {
	  
	  /*
	   * This tread gets Dallas traffic data and enqueues it into a RabbitMQ
	   * named REAL_DATA_QUEUE_NAME
	   */
	  public void run() {
		  try { 
		    ConnectionFactory factory = new ConnectionFactory();	//RabbitMQ setup
		    factory.setHost("localhost");							//RabbitMQ setup
		    Connection realConnection = factory.newConnection();	//RabbitMQ setup
		    Channel realChannel = realConnection.createChannel();	//RabbitMQ setup
		    //the RabbitMQ queue declare
		    realChannel.queueDeclare(REAL_DATA_QUEUE_NAME, false, false, false, null);

		    try {
			    for(int count = 0; count < 10; count++) {
			    	getDetectorData();								//get Dallas data
				    ListIterator<String> it = detectorData.listIterator();
				    while(it.hasNext()) {
				    	String s = it.next().toString();			//publish to q
				        if( (s.contains("DataSourceId^")) && 
				        		(s.contains("LastUpdated^")) ) {
					        realChannel.basicPublish("", REAL_DATA_QUEUE_NAME, null, s.getBytes());
				            System.out.println("[" + messagesSent++ + "]" + " Sent '" + s + "'");
				        } else {
				            System.out.println(" ERROR: Bad Line '" + s + "'");
				        }
				    }
			    	System.out.println("Thread.sleep(30000)...");
				    Thread.sleep(30000); //30 seconds
			    }
		    } catch (InterruptedException  ie) {
		    	System.out.println("Tread interrupted...");
		    }
		  
		    System.out.println("Thread close RabbitMq channel and connection...");
		    realChannel.close();									//RabbitMQ cleanup
		    realConnection.close();									//RabbitMQ cleanup
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }
  }
  
  /*
   * Start the thread to fetch Dallas traffic data and enqueue it in RabbitMQ
   */
  public static void main(String[] args) throws Exception {
	  
	  Thread t = new Thread(new DfwTrafficFetchToRabbitMq());
	  System.out.println("t.start()...");
	  t.start();
	  System.out.println("t.join(80000)...");
	  t.join(80000);								//stop after 80 seconds
	  System.out.println("t.interrupt()...");
	  t.interrupt();
	  System.out.println("t.join()...");
	  t.join();
	  System.out.println("Done.");
  }
  
  	/*
  	 * Get Dallas traffic data from TxDOT
  	 */
	public static void getDetectorData() {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			String url = "http://dfwtraffic.dot.state.tx.us/DalTrans/GetFile.aspx?FileName=MapBurnerOutput/TrafficDetectors.xml";
			Document doc = db.parse(new URL(url).openStream());
			
	        if(doc.hasChildNodes()) {
	        	detectorData.clear();
	        	getDetectorDataFromNodeList(doc.getChildNodes());
	        }
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Parse the traffic data
	 */
    private static void getDetectorDataFromNodeList(NodeList nodeList) {
        for (int count = 0; count < nodeList.getLength(); count++) {
            Node elemNode = nodeList.item(count);
            if (elemNode.getNodeType() == Node.ELEMENT_NODE) {
                // get node name and value
                if (elemNode.hasAttributes()) {
                    NamedNodeMap nodeMap = elemNode.getAttributes();
                    for (int i = 0; i < nodeMap.getLength(); i++) {
                        Node node = nodeMap.item(i);
                        sbAccum.append("^" + node.getNodeName());
                        sbAccum.append("^" + node.getNodeValue());
                    }
                    if(elemNode.getNodeName() == REAL_DATA_NODENAME) { 
	                    detectorData.add(sbAccum.toString());
	                    sbAccum.setLength(0);
                    }
                }
                if (elemNode.hasChildNodes()) { //recursive call if the node has child nodes
                	getDetectorDataFromNodeList(elemNode.getChildNodes());
                }
            }
        }
    }
 
}