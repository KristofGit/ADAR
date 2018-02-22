package MutatorsRuleParts;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import FrequentItemsets.Trace;
import FrequentItemsets.Trace.TraceElement;
import RuleComponents.RControlFlowComp;
import generic.CollectionHelper;
import generic.RandomHelper;

public class ControlFlowMutatorRule {

	public static Trace<RControlFlowComp> alterActivityOrder(Trace<RControlFlowComp> trace)
	{
		/* Take a single activity in the trace and alter its order and position index accordingly
		 */
		List<TraceElement<RControlFlowComp>> allElementsOriginal = trace.getAllElements();
		
		List<RControlFlowComp> allActivitesOriginalLaterModiefied = allElementsOriginal.stream().map(x->x.getValue()).collect(Collectors.toList());
				
		int randomListIndexSelect,randomListIndexInject;
		do
		{
			randomListIndexSelect = RandomHelper.getRandomListIndex(allActivitesOriginalLaterModiefied);
			randomListIndexInject  = RandomHelper.getRandomListIndex(allActivitesOriginalLaterModiefied);
		}
		while(randomListIndexInject == randomListIndexSelect);
		
		RControlFlowComp activityToMove = allActivitesOriginalLaterModiefied.get(randomListIndexSelect);
		allActivitesOriginalLaterModiefied.remove(activityToMove);
		CollectionHelper.add(randomListIndexInject, activityToMove, allActivitesOriginalLaterModiefied);
		
		return new Trace<>(allActivitesOriginalLaterModiefied);
	}
	
	public static Trace<RControlFlowComp> injectArtificalActivity(Trace<RControlFlowComp> trace)
	{
		//create a single artifical activity and inject it at a random position in the trace
		List<TraceElement<RControlFlowComp>> allElementsOriginal = trace.getAllElements();
		
		List<RControlFlowComp> allActivitesOriginalLaterModiefied = allElementsOriginal.stream().map(x->x.getValue()).collect(Collectors.toList());

		RControlFlowComp randomActivity = new RControlFlowComp(UUID.randomUUID().toString());
		
		int randomListIndexInject = RandomHelper.getRandomListIndex(allActivitesOriginalLaterModiefied);

		CollectionHelper.add(randomListIndexInject, randomActivity, allActivitesOriginalLaterModiefied);

		return new Trace<>(allActivitesOriginalLaterModiefied);
	}
	 
}
