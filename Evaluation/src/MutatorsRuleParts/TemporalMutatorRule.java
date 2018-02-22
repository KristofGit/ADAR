package MutatorsRuleParts;

import java.util.List;
import java.util.stream.Collectors;

import FrequentItemsets.Trace;
import FrequentItemsets.Trace.TraceElement;
import RuleComponents.RControlFlowComp;
import RuleComponents.RResourceComp;
import RuleComponents.RTemporalComp;
import Temporal.EDurationClasses;
import Temporal.FurryDurationGen.FuzzyResultList;
import generic.RandomHelper;

public class TemporalMutatorRule {

	public static Trace<RTemporalComp> injectArtifialExecutionTime(Trace<RTemporalComp> trace) {
		//Assign a new random temporal behaviour class
		
		List<TraceElement<RTemporalComp>> allElementsOriginal = trace.getAllElements();

		List<RTemporalComp> allActivitesOriginalLaterModiefied = 
				allElementsOriginal.stream().map(x->x.getValue()).collect(Collectors.toList());

		int randomListIndexToReplace = RandomHelper.getRandomListIndex(allActivitesOriginalLaterModiefied);

		RTemporalComp originalActivity = allActivitesOriginalLaterModiefied.get(randomListIndexToReplace);

		EDurationClasses originalDurationClass = originalActivity.getActivityDuration();
		EDurationClasses artificalReplacementDurationClass; 
		
		String originalActivityName = originalActivity.getActivityLabel();
		FuzzyResultList<EDurationClasses> fuzzyDuration = originalActivity.getFuzzyDuration();
		
		do
		{
			artificalReplacementDurationClass = RandomHelper.randomEnum(EDurationClasses.class);
			
			if(originalDurationClass != artificalReplacementDurationClass)
			{
				break;
			}			
		}while(true);
		
		assert(artificalReplacementDurationClass != null);
		
		RTemporalComp replacemenetArtificalActivity = new RTemporalComp(
				originalActivityName, Double.MAX_VALUE, artificalReplacementDurationClass, fuzzyDuration);
		
		allActivitesOriginalLaterModiefied.set(randomListIndexToReplace, replacemenetArtificalActivity);
		
		return new Trace<RTemporalComp>(allActivitesOriginalLaterModiefied);
	}
}
