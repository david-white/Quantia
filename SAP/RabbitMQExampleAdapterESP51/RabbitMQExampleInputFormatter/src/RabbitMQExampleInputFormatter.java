import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import com.sybase.esp.adapter.framework.utilities.*;
import com.sybase.esp.adapter.framework.AepRecord;
import com.sybase.esp.adapter.framework.module.RowFormatter;
import com.sybase.esp.sdk.Stream.Operation;

/**
 * 
 * Use, pilfer, cut, modify and paste the following code from ExampleRowInputFormatter.
 * Thank you author mdai
 * David
 * 
 */
public class RabbitMQExampleInputFormatter extends RowFormatter {
	private long lVal = 0;
	private Integer myIncrementingInteger = 1;

	@Override
	public void destroy() {
		utility.getAdapterLogger().info("RabbitMQExampleInputFormatter is destroying");
	}

	@Override
	public void init() throws Exception {
		utility.getAdapterLogger().info("RabbitMQExampleInputFormatter is initializing");
	} 

	/*
		^LastUpdated^2013-11-21T18:50:17.140625-06:00
	 */
	public Date getDateFromString(String dateStr) {
		if(dateStr == null) {
			utility.getAdapterLogger().info("ERROR: bad date (null), retrun now with new Date()");
			return(new Date());
		}
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Integer ndx = dateStr.indexOf('.');
		if(ndx != -1) {
			String strClipped = dateStr.substring(0, ndx).replace('-', '/').replace('T', ' ');
			utility.getAdapterLogger().info("strClipped     " + strClipped);
			try {
				Date clipDate = sdfDate.parse(strClipped);
				utility.getAdapterLogger().info("OK: date clipDate       " + clipDate.toString());
				return(clipDate);
			} catch (ParseException e1) {
				utility.getAdapterLogger().info("EXCEPTION: bad date (ParseException), retrun now with new Date()");
				e1.printStackTrace();
				return(new Date());
			}
		} else {
			utility.getAdapterLogger().info("ERROR: bad date, retrun now with new Date()");
			return(new Date());
		}
	}
	
	/*
	 * return the next string after match
	 * 
		^DataSourceId^10043
		^Id^10043 3
		^Name^EB SH183 @ Regal EBL2of4
		^PhysicalDetectorId^111
		^TmcId^DalTrans
		^Type^MainLane
		^xmlns:xsd^http://www.w3.org/2001/XMLSchema^xmlns:xsi^http://www.w3.org/2001/XMLSchema-instance
		^Id^3
		^LastUpdated^2013-11-21T18:50:17.140625-06:00
		^Occupancy^6
		^Speed^37
		^Status^Normal
		^Volume^7'
	 */
	public String nextStringFromMsg(String message, String strMatch) {
		StringBuilder sb = new StringBuilder();
		String delims = "^\t\n\r\f"; //String delims = "^";
		StringTokenizer st = new StringTokenizer(message, delims);
		while (st.hasMoreTokens()) {
			String s = st.nextToken();
		    if(s.equalsIgnoreCase(strMatch)) {
		    	break;
		    }
		}
    	if(st.hasMoreTokens()) {		//return the next string after match
    		sb.append(st.nextToken());
    		return(sb.toString());
    	} else {
    		return(null);
    	}
	}
	
	/*
		11-26-2013 06:01:48.300 INFO [Thread-19] (RabbitMQExampleInputTransporter.execute) [1] Received '^DataSourceId^10043^Id^10043 2176^Name^EB SH183 @ Wingren EBL3of3^PhysicalDetectorId^113^TmcId^DalTrans^Type^MainLane^xmlns:xsd^http://www.w3.org/2001/XMLSchema^xmlns:xsi^http://www.w3.org/2001/XMLSchema-instance^Id^2176^LastUpdated^2013-11-26T00:01:09.015625-06:00^Occupancy^1^Speed^56^Status^Normal^Volume^2'
		11-26-2013 06:01:48.303 INFO [Thread-18] (RabbitMQExampleInputFormatter.convert) [0] Received '^DataSourceId^10043^Id^10043 2176^Name^EB SH183 @ Wingren EBL3of3^PhysicalDetectorId^113^TmcId^DalTrans^Type^MainLane^xmlns:xsd^http://www.w3.org/2001/XMLSchema^xmlns:xsi^http://www.w3.org/2001/XMLSchema-instance^Id^2176^LastUpdated^2013-11-26T00:01:09.015625-06:00^Occupancy^1^Speed^56^Status^Normal^Volume^2'
		^DataSourceId^10079
		^Id^10079 102^Name^SB IH345 @ Pacific Lane 2 SB
		^PhysicalDetectorId^1^TmcId^DalTrans^Type
		^MainLane^xmlns:xsd^http://www.w3.org/2001/XMLSchema^xmlns:xsi^http://www.w3.org/2001/XMLSchema-instance^Id^102
		^LastUpdated^2013-11-26T00:01:35.4375-06:00
		^LongVolume^1^Occupancy^3^Speed^60^Status^Normal^Volume^8'
	 * 
	 * Try/Catch to ensure there are values for ESP even if the source data is bad.
	 * In production we would filter out bad data so as to not burden ESP.
	 */
	@Override
	public AdapterRow convert(AdapterRow arg0) throws Exception {
		  AepRecord record = new AepRecord();
		  AepRecord aepIn = (AepRecord) arg0.getData(0);
		  String message = (String) aepIn.getValues().get(0);
		  utility.getAdapterLogger().info("[" + lVal + "]" + " Received '" + message + "'");
		  
		  try { //LastUpdated for Date    LastUpdated = 2013-11-26T11:12:12.890625-06:00
			  String lastUpdated = nextStringFromMsg(message, "LastUpdated");
			  Date theDate = getDateFromString(lastUpdated);
			  record.getValues().add(theDate);			//dataTime       date,
		  } catch (NumberFormatException | NullPointerException exception) {
			  utility.getAdapterLogger().info("EXCEPTION: LastUpdated force to now with new Date()");
			  record.getValues().add(new Date());			//dataTime       date,
		  }
		  
		  try { //detectorName
			  String detectorName = nextStringFromMsg(message, "Name");
			  record.getValues().add(detectorName); //detectorName   string,
		  } catch (NumberFormatException | NullPointerException exception) {
			  utility.getAdapterLogger().info("EXCEPTION: detectorName force to ERROR_BAD_DETECTOR_NAME");
			  record.getValues().add("ERROR_BAD_DETECTOR_NAME"); //detectorName   string,
		  }

		  try { //direction
			  String detectorName = nextStringFromMsg(message, "Name");
			  String direction = detectorName.trim().substring(0, 2);
			  if( direction.contentEquals("NB") || direction.contentEquals("SB") || 
					  direction.contentEquals("EB") || direction.contentEquals("WB") ) {
				  record.getValues().add(direction);		//direction      string,
			  } else {
				  record.getValues().add("XX");		//direction      string,
			  }
		  } catch (NumberFormatException | NullPointerException exception) {
			  utility.getAdapterLogger().info("EXCEPTION: direction forced to XX...");
			  record.getValues().add("XX");		//direction      string,
		  }
		  
		  try { //Id
			  String splits[] = nextStringFromMsg(message, "Id").split(" "); //theId.split(" ");
			  record.getValues().add(Integer.parseInt(splits[0]));		//detectorID1    integer,
			  record.getValues().add(Integer.parseInt(splits[1]));		//detectorID2    integer,
		  } catch (NumberFormatException | NullPointerException exception) {
			  utility.getAdapterLogger().info("EXCEPTION: Id forced to incrementing number as they are keys...");
			  record.getValues().add(myIncrementingInteger++);		//detectorID1    integer,
			  record.getValues().add(myIncrementingInteger++);		//detectorID2    integer,
		  }
		  
		  //DEBUG: FORCE to zero so all data goes through, see the rabbitmq.ccl flex stream if( MyInStream.detectorStatus != 0 ){exit;}
		  record.getValues().add(0);		//detectorStatus 

		  try { //Speed
			  record.getValues().add(Integer.parseInt(nextStringFromMsg(message, "Speed")));		//LongVolume      integer,
		  } catch (NumberFormatException | NullPointerException exception) {
			  utility.getAdapterLogger().info("EXCEPTION: Speed forced to -1...");
			  record.getValues().add(-1);
		  }

		  try { //Volume
			  record.getValues().add(Integer.parseInt(nextStringFromMsg(message, "Volume")));		//LongVolume      integer,
		  } catch (NumberFormatException | NullPointerException exception) {
			  utility.getAdapterLogger().info("EXCEPTION: Volume forced to -1...");
			  record.getValues().add(-1);
		  }

		  try { //Occupancy
			  record.getValues().add(Integer.parseInt(nextStringFromMsg(message, "Occupancy")));		//LongVolume      integer,
		  } catch (NumberFormatException | NullPointerException exception) {
			  utility.getAdapterLogger().info("EXCEPTION: Occupancy forced to -1...");
			  record.getValues().add(-1);
		  }

		  try { //LongVolume
			  record.getValues().add(Integer.parseInt(nextStringFromMsg(message, "LongVolume")));		//LongVolume      integer,
		  } catch (NumberFormatException | NullPointerException exception) {
			  utility.getAdapterLogger().info("EXCEPTION: LongVolume forced to -1...");
			  record.getValues().add(-1);
		  }
		
		  record.setOpcode(Operation.INSERT);
		  arg0.setData(0, record);
		
		  lVal = lVal + 1;
		return arg0;
	}
}
