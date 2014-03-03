import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.sybase.esp.adapter.framework.AdapterColumn;
import com.sybase.esp.adapter.framework.AepRecord;
import com.sybase.esp.adapter.framework.discovery.*;
import com.sybase.esp.adapter.framework.module.Transporter;
import com.sybase.esp.adapter.framework.utilities.*;
import com.sybase.esp.sdk.data.Schema;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;



/**
 * RabbitMQExampleInputTransporter is a copy, paste, adapt, modify, name change and column add from ExampleDiscoverableInputTransporter.
 * Thank you author mdai
 *  17nov13 drw change - add columns
 * 
 */

public class RabbitMQExampleInputTransporter extends Transporter implements TableDiscovery, ColumnDiscovery  {
	private final static String REAL_DATA_QUEUE_NAME = "DfwTrafficRealDataQueue";
	private long lVal;
	ConnectionFactory factory;
    Connection connection;
    Channel channel;
    QueueingConsumer consumer;
	
	@Override
	public void start() throws Exception {
		utility.getAdapterLogger().info("RabbitMQExampleInputTransporter is starting");
	}

	@Override
	public void stop() throws Exception {
		utility.getAdapterLogger().info("RabbitMQExampleInputTransporter is stopping");
		
	}

	
	@Override
	public void execute() throws Exception {
		utility.getAdapterLogger().info("RabbitMQExampleInputTransporter is running");
		lVal = 1;
		while(!utility.isStopRequested())
		{	//a blocking wait with no tmo.  Use nextDelivery(long) for an mSec tmo
			QueueingConsumer.Delivery delivery = consumer.nextDelivery(); //RabbitMQ
			String message = new String(delivery.getBody());
			utility.getAdapterLogger().info("[" + lVal + "]" + " Received '" + message + "'");
			AepRecord record = new AepRecord();
			record.getValues().add(message);
			AdapterRow row = utility.createRow (record);
			utility.sendRow(row);
			lVal = lVal + 1;
		}
	}

//	CREATE SCHEMA traficSchema (
//			dataTime       date,
//			detectorName   string,
//			direction      string,
//			detectorID1    integer,
//			detectorID2    integer,
//			detectorStatus integer,
//			speed          integer,
//			volume         integer,
//			occupancy      integer,
//			longVehicleVol integer
//		);
	
	public List<AdapterColumn> discoverSchema() {
		List<AdapterColumn> result =  new LinkedList<AdapterColumn>();
		//dataTime
		AdapterColumn c1 = new AdapterColumn();
		c1.setKey(false);
		c1.setName("dataTime");
		c1.setNullible(false);
		c1.setType(Schema.DataType.DATE);
		result.add(c1);
		//detectorName
		AdapterColumn c2 = new AdapterColumn();
		c2.setKey(false);
		c2.setName("detectorName");
		c2.setNullible(false);
		c2.setType(Schema.DataType.STRING);
		result.add(c2);
		//direction
		AdapterColumn c3 = new AdapterColumn();
		c3.setKey(false);
		c3.setName("direction");
		c3.setNullible(false);
		c3.setType(Schema.DataType.STRING);
		result.add(c3);
		//detectorID1
		AdapterColumn c4 = new AdapterColumn();
		c4.setKey(false);
		c4.setName("detectorID1");
		c4.setNullible(false);
		c4.setType(Schema.DataType.INTEGER);
		result.add(c4);
		//detectorID2
		AdapterColumn c5 = new AdapterColumn();
		c5.setKey(false);
		c5.setName("detectorID2");
		c5.setNullible(false);
		c5.setType(Schema.DataType.INTEGER);
		result.add(c5);
		//detectorStatus
		AdapterColumn c6 = new AdapterColumn();
		c6.setKey(false);
		c6.setName("detectorStatus");
		c6.setNullible(false);
		c6.setType(Schema.DataType.INTEGER);
		result.add(c6);
		//speed
		AdapterColumn c7 = new AdapterColumn();
		c7.setKey(false);
		c7.setName("speed");
		c7.setNullible(false);
		c7.setType(Schema.DataType.INTEGER);
		result.add(c7);
		//volume
		AdapterColumn c8 = new AdapterColumn();
		c8.setKey(false);
		c8.setName("volume");
		c8.setNullible(false);
		c8.setType(Schema.DataType.INTEGER);
		result.add(c8);
		//occupancy
		AdapterColumn c9 = new AdapterColumn();
		c9.setKey(false);
		c9.setName("occupancy");
		c9.setNullible(false);
		c9.setType(Schema.DataType.INTEGER);
		result.add(c9);
		//longVehicleVol
		AdapterColumn c10 = new AdapterColumn();
		c10.setKey(false);
		c10.setName("longVehicleVol");
		c10.setNullible(false);
		c10.setType(Schema.DataType.INTEGER);
		result.add(c10);

		return result;
	}

	@Override
	public void init() throws Exception {
		utility.getAdapterLogger().info("RabbitMQExampleInputTransporter is initializing");
		lVal = 0;
		//RabbitMQ initializations
		factory = new ConnectionFactory();
	    factory.setHost("localhost");
	    connection = factory.newConnection();
	    channel = connection.createChannel();
	    channel.queueDeclare(REAL_DATA_QUEUE_NAME, false, false, false, null);
	    utility.getAdapterLogger().info(" [REAL Thread] Waiting for messages...");
	    consumer = new QueueingConsumer(channel);
	    channel.basicConsume(REAL_DATA_QUEUE_NAME, true, consumer);
	}

	@Override
	public void destroy() {
		utility.getAdapterLogger().info("RabbitMQExampleInputTransporter is destroying");
	    utility.getAdapterLogger().info("REAL Thread close RabbitMq channel and connection...");
	    try {
			channel.close();
		} catch (IOException e1) {
			utility.getAdapterLogger().info("RabbitMQ channel.close() IOException" + e1.getMessage());
			//e1.printStackTrace();
		}
	    try {
			connection.close();
		} catch (IOException e2) {
			utility.getAdapterLogger().info("RabbitMQ connection.close() IOException" + e2.getMessage());
			//e2.printStackTrace();
		}
	}

	@Override
	public List<String> getTables() {
		List<String> tables = new LinkedList<String>();
		tables.add("MyInStream");
		
		return tables;
	}

	@Override
	public List<AdapterColumn> getColumns(String strTable, List<Object> samplesData) {
		if(strTable.equals("MyInStream"))
		{
			return discoverSchema();
		}
		return null;
	}
}
