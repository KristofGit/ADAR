package MutatorsRuleParts;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import FrequentItemsets.Trace;
import FrequentItemsets.Trace.TraceElement;
import RuleComponents.RControlFlowComp;
import RuleComponents.RResourceComp;
import generic.RandomHelper;

public class ResourceMutatorRule {

	public Trace<RResourceComp> injectArtifialResource(Trace<RResourceComp> trace){
	
		//replcae the resource of one of the activities with a random value
		
		List<TraceElement<RResourceComp>> allElementsOriginal = trace.getAllElements();
		
		List<RResourceComp> allActivitesOriginalLaterModiefied = allElementsOriginal.stream().map(x->x.getValue()).collect(Collectors.toList());
	
		int randomListIndexReplace  = RandomHelper.getRandomListIndex(allActivitesOriginalLaterModiefied);

		RResourceComp originalResource = allActivitesOriginalLaterModiefied.get(randomListIndexReplace);
		
		String originalActivity = originalResource.getActivityLabel();
		String resourceName = UUID.randomUUID().toString();
		
		RResourceComp artificalResource = new RResourceComp(
				originalActivity, originalResource.getResourceMode(), resourceName);
		
		allActivitesOriginalLaterModiefied.set(randomListIndexReplace, artificalResource);
		
		return new Trace<>(allActivitesOriginalLaterModiefied);
	}
	
	public Trace<RResourceComp> copyExistingResourceOver(Trace<RResourceComp> trace){
		
		//take one of the existng resources in the process, select one randomly
		//and assign this random one to an activity that has a different one
	
		List<TraceElement<RResourceComp>> allElementsOriginal = trace.getAllElements();
		
		List<RResourceComp> allActivitesOriginalLaterModiefied = allElementsOriginal.stream().map(x->x.getValue()).collect(Collectors.toList());
		
		List<String> resourcesUsedInProcess = allActivitesOriginalLaterModiefied.stream().map(x->x.getResourceName()).distinct().collect(Collectors.toList());
		
		int randomListIndexReplace  = RandomHelper.getRandomListIndex(allActivitesOriginalLaterModiefied);
		RResourceComp activityToModity = allActivitesOriginalLaterModiefied.get(randomListIndexReplace);
		
		String originalResourceUsedAtRandomPlace =  activityToModity.getResourceName();
		String newResourceToUse;
		do{
			newResourceToUse = resourcesUsedInProcess.get(RandomHelper.getRandomListIndex(resourcesUsedInProcess));
			
			if(!Objects.equals(originalResourceUsedAtRandomPlace, newResourceToUse))
			{
				break;
			}
		}while(true);
		
		
		
		String originalActivity = activityToModity.getActivityLabel();
		
		RResourceComp artificalResource = new RResourceComp(
				originalActivity, activityToModity.getResourceMode(), newResourceToUse);
		
		allActivitesOriginalLaterModiefied.set(randomListIndexReplace, artificalResource);//replace original with modfiied
		
		return new Trace<>(allActivitesOriginalLaterModiefied);
	}
}
