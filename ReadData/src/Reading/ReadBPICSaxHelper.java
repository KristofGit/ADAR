package Reading;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import LogTraceOB.LogTrace;

public class ReadBPICSaxHelper {

	public static List<LogTrace>  readFromBPICWithSAX(File file)
	{
		List<LogTrace> traces = new ArrayList<>();
		
		 try {
	         SAXParserFactory factory = SAXParserFactory.newInstance();
	         SAXParser saxParser = factory.newSAXParser();
	         ReadBPICSaxHandler userhandler = new ReadBPICSaxHandler();
	         saxParser.parse(file, userhandler);     
	         
	         traces = userhandler.getResult();
	      } catch (Exception e) {
	         e.printStackTrace();
	      }
		 
		 return traces;
	}
}
