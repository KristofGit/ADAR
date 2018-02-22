package Similarity;

import java.util.List;
import java.util.Set;

import com.google.common.collect.*;

import LogTraceOB.LogTrace;
import LogTraceOB.LogTraceElement;

public class TraceSimilarity {

	public static LogTrace findMostSimilarOne(List<LogTrace> tracesToSearchIn, LogTrace shouldBeSimilarToThis)
	{
		LogTrace mostSimilarTrace = null;
		
		int minDifferencesFoundSoFar = Integer.MAX_VALUE;
		
		Multiset<String> activitySet = HashMultiset.create();
		
		for(LogTraceElement eachElement : shouldBeSimilarToThis.getTraceElements())
		{
			activitySet.add(eachElement.getActivityName());
		}
		
		
		for(LogTrace eachOtherTrace : tracesToSearchIn)
		{
			Multiset<String> activitySetTrace = HashMultiset.create();
			Multiset<String> activitySetCompare = HashMultiset.create(activitySet);

			for(LogTraceElement eachElement : eachOtherTrace.getTraceElements())
			{
				activitySetTrace.add(eachElement.getActivityName());
			}
			
			//calc similarity 
			activitySetCompare.removeAll(activitySetTrace);
			activitySetTrace.removeAll(activitySet);
			
			int sumDifferences = activitySetCompare.size()+activitySetTrace.size();
			
			if(mostSimilarTrace == null || minDifferencesFoundSoFar > sumDifferences)
			{
				mostSimilarTrace = eachOtherTrace;
				minDifferencesFoundSoFar = sumDifferences;
			}
		}
		
		return mostSimilarTrace;
	}
}
