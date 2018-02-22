package Reading;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.deckfour.xes.in.XesXmlGZIPParser;
import org.deckfour.xes.in.XesXmlParser;
import org.deckfour.xes.logging.XLogging;
import org.deckfour.xes.logging.XLogging.Importance;
import org.deckfour.xes.logging.XLoggingListener;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import Configuration.BehaviourSource;
import LogTraceOB.LogTrace;
import LogTraceOB.LogTraceElement;
import generic.DateHelper;

public class ReadFromLogs {

	public List<LogTrace> Read(BehaviourSource source)
	{
		List<LogTrace> result = new ArrayList<LogTrace>();
		
		XLogging.setListener(new XLoggingListener() {
			
			@Override
			public void log(String arg0, Importance arg1) {
				System.out.println(arg1 + ":"+arg0);
			}
		});
		
		List<File> files = new ArrayList<File>();
		files.add(new File(source.getPathToLog()));
		
		
	
		//List<ProcessModel> result = null;
		
		for(File xesFile : files)
		{
			if(!xesFile.getName().toLowerCase().contains("xes"))
			{
				continue;
			}
			
			XesXmlParser xesParser = new XesXmlParser();

			if(!xesParser.canParse(xesFile))
			{
				xesParser = new XesXmlGZIPParser();
				if (!xesParser.canParse(xesFile)) {
					throw new IllegalArgumentException("Unparsable log file: " + xesFile.getAbsolutePath());
				}
			}		
			
			try {
				
				if(source == BehaviourSource.HEP4 || source == BehaviourSource.HEP3 || source == BehaviourSource.HEP1)
				{
					result = parseLogHEPActivity(xesParser, xesFile);
				}
				else //else it must be BPIC because we only use BPIC and HEP here
				{
					//OpenXes blocked endlessly on some machines, so we use sax now
					//result = parseLogBPICActivity(xesParser, xesFile);
					result = ReadBPICSaxHelper.readFromBPICWithSAX(xesFile);
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
					
			//currently we only support one file (executions of the same activity are not merged if stored in multiple files!)
			//so top after the first
			break;
		}
		
		return result;
	}
	
	private static List<LogTrace> parseLogBPICActivity(XesXmlParser parser, File xesFile) throws Exception {
	List<LogTrace> result = new ArrayList<>();
		
		List<XLog> xLogs = parser.parse(xesFile);
				
		  for (XLog xLog : xLogs) {
	        	
	        	for (XTrace trace : xLog) {
	        		
	        		
	        		//workaround because we only have completed event and not start/end
	        		//so we assume the last compelted event as the starting event timestapnd
	        		//and the next complexted event at the ending event timestamp
	        		Date lastTimeStamp = null;
	        		LogTrace extractedTrace = new LogTrace();
	        		
					for (XEvent event : trace) {
						
						XAttributeMap attributeMap = event.getAttributes();

		        		XAttributeLiteral activityName = (XAttributeLiteral)attributeMap.get("activityNameEN"); //identifies the activity
		        		XAttributeLiteral resourceName = (XAttributeLiteral)attributeMap.get("org:resource"); //identifies the resource
		        		Date timestamp = getDate(attributeMap, "planned");
		        		
		        		if(lastTimeStamp == null)
		        		{
			        		lastTimeStamp = timestamp;
		        			continue; //skip the first event because we cant get the duration now!
		        		}
		        		
		        		if(timestamp == null)
		        		{
		        			timestamp = lastTimeStamp;
		        		}
		        		
		        		double duration = DateHelper.duration(timestamp, lastTimeStamp);
		        		
		        		extractedTrace.addTraceElement(new LogTraceElement(
		        				activityName.getValue(), resourceName.getValue(), duration, 1900+timestamp.getYear()));
		        		
		        		lastTimeStamp = timestamp;
					}
					
					if(!extractedTrace.isEmpty())
					{
						result.add(extractedTrace);
						
						System.out.println("read process traces:"+result.size());
					}
	        	}
	   	  }
	    		  	
	    		  
	    return result;
	}
	
	private static List<LogTrace> parseLogHEPActivity(XesXmlParser parser, File xesFile) throws Exception {
		
		List<LogTrace> result = new ArrayList<>();
		
		List<XLog> xLogs = parser.parse(xesFile);
		
		  for (XLog xLog : xLogs) {
	        	
	        	for (XTrace trace : xLog) {
	        			        		
	        		//workaround because we only have completed event and not start/end
	        		//so we assume the last compelted event as the starting event timestapnd
	        		//and the next complexted event at the ending event timestamp
	        		Date lastTimeStamp = null;
	        		LogTrace extractedTrace = new LogTrace();
	        		
					for (XEvent event : trace) {
						
						XAttributeMap attributeMap = event.getAttributes();

		        		XAttributeLiteral activityName = (XAttributeLiteral)attributeMap.get("concept:name"); //identifies the activity
		        		XAttributeLiteral resourceName = (XAttributeLiteral)attributeMap.get("org:resource"); //identifies the resource
		        		Date timestamp = getDate(attributeMap, "time:timestamp");
		        		
		        		if(lastTimeStamp == null)
		        		{
			        		lastTimeStamp = timestamp;
		        			continue; //skip the first event because we cant get the duration now!
		        		}
		        		
		        		double duration = DateHelper.duration(timestamp, lastTimeStamp);
		        		
		        		extractedTrace.addTraceElement(new LogTraceElement(
		        				activityName.getValue(), resourceName.getValue(), duration, 1900+timestamp.getYear()));
		        		
		        		lastTimeStamp = timestamp;
					}
					
					if(!extractedTrace.isEmpty())
					{
						result.add(extractedTrace);
					}
	        	}
		  }
		  	
		  
		  return result;
	}
	
	private static Date getDate(XAttributeMap attributeMap, String valuename) throws ParseException
	{
		Date result = null;
		
		XAttribute attribute =  attributeMap.get(valuename);
	
		if(attribute != null)
		{
			if(attribute instanceof XAttributeTimestamp)
			{
				result = ((XAttributeTimestamp)attribute).getValue();
			}
			else if(attribute instanceof XAttributeLiteral)
			{
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.ENGLISH);
				
				String dateString = ((XAttributeLiteral)attribute).getValue();
				
				result = df.parse(dateString);
			}
		}
		
		return result;
	}
}
