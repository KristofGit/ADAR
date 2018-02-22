package GeneralAnomalyDetection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import FrequentItemsets.Trace;
import LogTraceOB.LogTrace;
import LogTraceOB.LogTraceElement;
import Resources.EResourceClasses;
import RuleComponents.RControlFlowComp;
import RuleComponents.RResourceComp;
import RuleComponents.RTemporalComp;
import Temporal.EDurationClasses;
import Temporal.FurryDurationGen;
import Temporal.FurryDurationGen.FuzzyResultList;

public class LogTracePreparation {

	public static Trace<RControlFlowComp> prepareControlFlowTraces(LogTrace logTraces)
	{
		return prepareControlFlowTraces(Arrays.asList(logTraces)).get(0);
	}
	
	public static List<Trace<RControlFlowComp>> prepareControlFlowTraces(List<LogTrace> logTraces)
	{
		List<Trace<RControlFlowComp>> result = new ArrayList<Trace<RControlFlowComp>>();
		
		for(LogTrace eachLogTrace : logTraces)
		{
			RControlFlowComp[] elementsForTrace = new RControlFlowComp[eachLogTrace.getSize()];
			
			int i=0; 
			for(LogTraceElement eachTraceElement : eachLogTrace.getTraceElements())
			{
				elementsForTrace[i] = new RControlFlowComp(eachTraceElement.getActivityName());
				i++;
			}
			
			result.add(new Trace<>(elementsForTrace));
		}
		
		return result;
	}
	
	public static List<Trace<RTemporalComp>> prepareTemporalTraces(List<LogTrace> logTraces, double fuzzyTempralOverlapping)
	{
		List<Trace<RTemporalComp>> result = new ArrayList<Trace<RTemporalComp>>();

		//key = activity name, value = list of activity durations based on logs
		Map<String, List<Double>> listActivityDurations = new HashMap<>();
		
		for(LogTrace eachLogTrace : logTraces)
		{
			for(LogTraceElement eachTraceElement : eachLogTrace.getTraceElements())
			{
				String activityName = eachTraceElement.getActivityName();
				Double duration = eachTraceElement.getActivityDuration();
				
				List<Double> durationsList = listActivityDurations.get(activityName);
				
				if(durationsList == null)
				{
					durationsList = new ArrayList<>();
					listActivityDurations.put(activityName, durationsList);
				}
				
				durationsList.add(duration);
			}
		}
		
		Map<String, FuzzyResultList<EDurationClasses>> listActivityDurationsFuzzy = new HashMap<>();
		FurryDurationGen gen = new FurryDurationGen();

		for(Entry<String, List<Double>> eachActivityDurations : listActivityDurations.entrySet())
		{
			List<Double> duration = eachActivityDurations.getValue();
			String activityName = eachActivityDurations.getKey();
			
			FuzzyResultList<EDurationClasses> fuzzyResult = gen.FuzIt(duration, fuzzyTempralOverlapping);
			
			listActivityDurationsFuzzy.put(activityName, fuzzyResult);
		}
		
		for(LogTrace eachLogTrace : logTraces)
		{
			RTemporalComp[] elementsForTrace = new RTemporalComp[eachLogTrace.getSize()];
			
			int i=0; 
			for(LogTraceElement eachTraceElement : eachLogTrace.getTraceElements())
			{
				String activityName = eachTraceElement.getActivityName();
				Double activityDureation = eachTraceElement.getActivityDuration();
				FuzzyResultList<EDurationClasses> fuzzyResult = listActivityDurationsFuzzy.get(activityName); //for all activity instances
				EDurationClasses durationActivity = fuzzyResult.getBehaviourForDuration(activityDureation, EDurationClasses.class).getMostLikelyBehaviour(); //for the specific activity instance
				
				assert(durationActivity != null);
				
				elementsForTrace[i] = new RTemporalComp(
						eachTraceElement.getActivityName(),
						activityDureation,
						durationActivity,
						fuzzyResult);
				i++;
			}
			
			result.add(new Trace<>(elementsForTrace));
		}
		
		return result;
	}
	
	public static List<Trace<RResourceComp>> prepareResourceTraces(List<LogTrace> logTraces, EResourceClasses resourceRules)
	{
		List<Trace<RResourceComp>> result = new ArrayList<Trace<RResourceComp>>();
		
		for(LogTrace eachLogTrace : logTraces)
		{
			RResourceComp[] elementsForTrace = new RResourceComp[eachLogTrace.getSize()];
			
			int i=0; 
			for(LogTraceElement eachTraceElement : eachLogTrace.getTraceElements())
			{
				String activityName = eachTraceElement.getActivityName();
				String resourceName = eachTraceElement.getResourceName();
				
				elementsForTrace[i] = new RResourceComp(activityName, resourceRules, resourceName);
				i++;
			}
			
			result.add(new Trace<>(elementsForTrace));
		}
		
		return result;
	}
}
