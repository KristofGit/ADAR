package MutatorsReadData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import FrequentItemsets.Trace;
import LogTraceOB.LogTrace;
import LogTraceOB.LogTraceElement;
import RuleComponents.RResourceComp;
import generic.RandomHelper;

public class ResourceMutatorOneBOD implements IMutate {

	public LogTrace injectArtifialResource(LogTrace trace){
		
		List<LogTraceElement> traceElements = new ArrayList<>(trace.getTraceElements());
		int randomListIndexSelect = RandomHelper.getRandomListIndex(traceElements);

		LogTraceElement originalTraceElement = traceElements.get(randomListIndexSelect);
		
		String activityName = originalTraceElement.getActivityName();
		String resourceName = UUID.randomUUID().toString();
		Double duration = originalTraceElement.getActivityDuration();
		
		LogTraceElement artificalActivity = new LogTraceElement(activityName, resourceName, duration);
		
		traceElements.set(randomListIndexSelect, artificalActivity);
		
		return new LogTrace(traceElements);
	}
	

	public LogTrace mutate(LogTrace trace) {
		return injectArtifialResource(trace);
	}
}
