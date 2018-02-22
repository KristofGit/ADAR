package LogTraceOB;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogTrace {

	private List<LogTraceElement> trace = new ArrayList<LogTraceElement>();
	
	public LogTrace()
	{
		
	}
	
	public LogTrace(List<LogTraceElement> traceContent)
	{
		trace.addAll(traceContent);
	}
	
	public void addTraceElement(LogTraceElement element)
	{
		this.trace.add(element);
	}
	
	public boolean isEmpty()
	{
		return trace.isEmpty();
	}
	
	public List<LogTraceElement> getTraceElements()
	{
		return Collections.unmodifiableList(trace);
	}

	public int getSize()
	{
		return trace.size();
	}
}
