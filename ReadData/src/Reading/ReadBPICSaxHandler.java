package Reading;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.deckfour.xes.model.XAttributeLiteral;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import LogTraceOB.LogTrace;
import LogTraceOB.LogTraceElement;
import generic.DateHelper;

public class ReadBPICSaxHandler extends DefaultHandler{
	private final List<LogTrace> result = new ArrayList<>();

	private LogTrace currentTrace;
	private String activityName;
	private String resourceName;
	private Date timestamp;
	private Date lastTimeStamp;
	
	private final DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

	  
	private boolean enteredEvent = false;
	
	  @Override
	   public void startElement(String uri, 
	   String localName, String qName, Attributes attributes) throws SAXException {

		  if(qName.equals("trace"))
		  {
			  currentTrace = new LogTrace();
			  lastTimeStamp = null;
		  }
		  else if(qName.equals("event"))
		  {
			  activityName = null;
			  resourceName = null;
			  timestamp = null;
			  enteredEvent = true;
		  }
		  else if(enteredEvent && (qName.equals("string") || qName.equals("date")))
		  {
			  String keyValue = attributes.getValue("key");
			  
			  if(keyValue.equals("activityNameEN"))
			  {
				  activityName = attributes.getValue("value");
			  }
			  else if(keyValue.equals("org:resource"))
			  {
				  resourceName = attributes.getValue("org:resource");
			  }
			  else if(qName.equals("date") && keyValue.equals("planned"))
			  {
				  String dateString = attributes.getValue("value");  
				  if(dateString != null)
				  {
					  try {
							timestamp = df2.parse(dateString);
						} catch (ParseException e) {
							e.printStackTrace();
						} 
				  }			  
			  }
		  }
	   }
	  
	 @Override
	   public void endElement(String uri, 
	   String localName, String qName) throws SAXException {

		 if(qName.equals("event"))
		 {
			enteredEvent = false;
			 
     		if(lastTimeStamp == null)
     		{
	        	lastTimeStamp = timestamp;
     			return; //skip the first event because we cant get the duration now!
     		}
     		
     		if(timestamp == null)
     		{
     			timestamp = lastTimeStamp;
     		}
     		
			 double duration = DateHelper.duration(timestamp, lastTimeStamp);
     		
			 currentTrace.addTraceElement(new LogTraceElement(
     				activityName, resourceName, duration, 1900+timestamp.getYear()));
     		
			 lastTimeStamp = timestamp;
		 }
		 else if(qName.equals("trace"))
		 {
			 result.add(currentTrace);
		 }
	   }

	public List<LogTrace> getResult() {
		return result;
	}
}
