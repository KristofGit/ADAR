package MutatorsReadData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import LogTraceOB.LogTrace;
import LogTraceOB.LogTraceElement;
import generic.RandomHelper;

public class ResourceMutatorTwoSOD implements IMutate {

	public LogTrace forceSODAnomaly(LogTrace trace){
		
		List<LogTraceElement> traceElements = new ArrayList<>(trace.getTraceElements());

		/*List<String> resourcesUsedInProcess =
				traceElements.stream().map(x->x.getResourceName()).distinct().collect(Collectors.toList());

		int randomListIndexSelect = RandomHelper.getRandomListIndex(traceElements);
		LogTraceElement originalActivity = traceElements.get(randomListIndexSelect);
		
		String originalResourceUsedAtRandomPlace = originalActivity.getResourceName();
		String newResourceToUse;
		
		int triedAmountsOfRandomChecks = 0;
		boolean didChangeSomething = false;
		
		do{
			newResourceToUse = resourcesUsedInProcess.get(RandomHelper.getRandomListIndex(resourcesUsedInProcess));
			
			if(!Objects.equals(originalResourceUsedAtRandomPlace, newResourceToUse))
			{
				didChangeSomething = true;
				break;
			}
			else
			{
				triedAmountsOfRandomChecks ++;
			}
		}while(true && triedAmountsOfRandomChecks < 10000);

		if(!didChangeSomething)
		{
			return null;
		}*/
		
		int triedAmountsOfRandomChecks = 0;
		boolean didChangeSomething = false;

		String newResource = UUID.randomUUID().toString();
		
		LogTraceElement firstActivity = null;
		LogTraceElement secondActivity = null;
		int firstActivityIndex;
		int secondActivityIndex;
		
		do
		{
			firstActivityIndex = RandomHelper.getRandomListIndex(traceElements);
			secondActivityIndex = RandomHelper.getRandomListIndex(traceElements);

			if(firstActivityIndex != secondActivityIndex)
			{
				firstActivity = traceElements.get(firstActivityIndex);
				secondActivity = traceElements.get(secondActivityIndex);

				String resourceFirst = firstActivity.getResourceName();
				String resourcesecond = secondActivity.getResourceName();

				if(!Objects.equals(resourceFirst, resourcesecond))
				{
					didChangeSomething = true;
					break;
				}
			}
			
			triedAmountsOfRandomChecks++;

		}while(true && triedAmountsOfRandomChecks < 100);

		if(!didChangeSomething)
		{
			return null;
		}
		
		LogTraceElement artificalActivityFirst = new LogTraceElement(firstActivity.getActivityName(), newResource, firstActivity.getActivityDuration());
		LogTraceElement artificalActivitySecond = new LogTraceElement(secondActivity.getActivityName(), newResource, secondActivity.getActivityDuration());

		traceElements.set(firstActivityIndex, artificalActivityFirst);
		traceElements.set(secondActivityIndex, artificalActivitySecond);

		return new LogTrace(traceElements);
	}
	
	public LogTrace mutate(LogTrace trace) {
		return forceSODAnomaly(trace);
	}
}
