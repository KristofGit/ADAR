package MutatorsReadData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import LogTraceOB.LogTrace;
import LogTraceOB.LogTraceElement;
import generic.RandomHelper;

public class ControlFlowMutatorTwo implements IMutate{

	
	public static LogTrace injectArtificalActivity(LogTrace trace)
	{
		List<LogTraceElement> traceElements = new ArrayList<>(trace.getTraceElements());
		int randomListIndexSelect = RandomHelper.getRandomListIndex(traceElements);

		LogTraceElement artificalTrace = 
				new LogTraceElement(UUID.randomUUID().toString(), UUID.randomUUID().toString(), Double.MAX_VALUE);
		
		traceElements.set(randomListIndexSelect, artificalTrace);
		
		return new LogTrace(traceElements); 
	}

	
	public LogTrace mutate(LogTrace trace) {
		return injectArtificalActivity(trace);
	}
}
