package MutatorsReadData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import FrequentItemsets.Trace;
import LogTraceOB.LogTrace;
import LogTraceOB.LogTraceElement;
import RuleComponents.RTemporalComp;
import generic.RandomHelper;

public class TemporalMutator implements IMutate {

	public static LogTrace injectArtifialExecutionTime(LogTrace trace) {
		
		List<LogTraceElement> traceElements = new ArrayList<>(trace.getTraceElements());

		int randomListIndexSelect = RandomHelper.getRandomListIndex(traceElements);

		LogTraceElement originalTraceElement = traceElements.get(randomListIndexSelect);
		
		String activityName = originalTraceElement.getActivityName();
		String resourceName = originalTraceElement.getActivityName();
		Double duration = Double.MAX_VALUE;
		
		LogTraceElement artificalActivity = new LogTraceElement(activityName, resourceName, duration);
		
		traceElements.set(randomListIndexSelect, artificalActivity);
		
		return new LogTrace(traceElements);
	}
	
	public LogTrace mutate(LogTrace trace) {
		return injectArtifialExecutionTime(trace);
	}
}
