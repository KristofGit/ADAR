package Temporal;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import generic.EnumHelper;

public class FurryDurationGen {

	public static class FuzzyResult
	{
		private Double timespanStart;
		private Double timespanEnd;
		private Double overlappingStart;
		private Double overlappingEnd; 
		
		public FuzzyResult(Double start, Double end, Double overlapStart, Double overlapEnd)
		{
			timespanStart = start;
			timespanEnd = end;
			overlappingStart = overlapStart;
			overlappingEnd = overlapEnd;
		}
		
		public boolean conversPointInTime(Double pointInTime)
		{
			return pointInTime>=overlappingStart && pointInTime<=overlappingEnd;
		}
		
		//determines if a) point in time is covered by this fuzzy result and b) if it
		//is in one of the overlappings how much it is covered
		public Double coverage(Double pointInTime)
		{
			//if it is not contained
			if(pointInTime<=overlappingStart || pointInTime>=overlappingEnd) //use >= to prevent divisions by 0 below
			{
				return 0.;
			}
			
			//if it is contained in the main part
			if(pointInTime>=timespanStart && pointInTime<=timespanEnd)
			{
				return 1.;
			}
			
			//if it is contained in the overlapping section (right side)
			if(pointInTime>timespanEnd)
			{
				Double overlappingSpanLength = overlappingEnd-timespanEnd;
				Double normalizedPointInTime = pointInTime-timespanEnd;
				
				return 1-(normalizedPointInTime/overlappingSpanLength);
			}
			
			//if it is contained in the overlapping section (left side)
			Double overlappingSpanLength = timespanStart-overlappingStart;
			Double normalizedPointInTime = timespanStart-pointInTime;

			return 1-(normalizedPointInTime/overlappingSpanLength);
		}
	};
	
	public static class FuzzyResultList<T extends Enum<EDurationClasses>>
	{
		private Map<T, FuzzyResult> allFuzzyRepresentations = new HashMap<>();
		
		public void add(T type, FuzzyResult f)
		{
			allFuzzyRepresentations.put(type, f);
		}
		
		public  MultiFuzzyBehaviour<T, Double> getBehaviourForDuration(Double duration, Class<T> clazz)
		{
			 MultiFuzzyBehaviour<T, Double> result = new MultiFuzzyBehaviour<>();
			 
			 for (Map.Entry<T, FuzzyResult> entry : allFuzzyRepresentations.entrySet())
			 {
				 FuzzyResult value = entry.getValue();
				 T type = entry.getKey();
				 
				 if(value.conversPointInTime(duration))
				 {
					 result.addBehviour(type, value.coverage(duration));
				 }
			 }
			 
			 if(result.isEmpty()) //this can happen if the observed duration is significantly out of the scope
			 {
				 T typeToUse = null;
				 
				 double minDuration = 
						 allFuzzyRepresentations.entrySet()
						 .stream().map(x->x.getValue().overlappingStart).min((o1,o2)->Double.compare(o1, o2)).get();
				 
				 if(duration<minDuration)
				 {
					 typeToUse = EnumHelper.first(clazz);
				 }
				 else
				 {
					 typeToUse = EnumHelper.last(clazz);
				 }
				 //determine if its too high or too low
				 //assign then either the last or the first duration type
				 result.addBehviour(typeToUse, 0.); //assume a very low representation
			 }
			 
			 return result;
		}
	};
	
	//overlappingsValue percentage of overlappings used e.g., 1.1 or 2
	public FuzzyResultList<EDurationClasses> FuzIt(List<Double> durations, Double overlappingsValue)
	{
		FuzzyResultList<EDurationClasses> result = new FuzzyResultList<>();
		
		Double lowestDuration = Collections.min(durations);
		Double longestDuration = Collections.max(durations);
		
		Double timespanComplete = longestDuration - lowestDuration;
		int amountOfClassInstances = EDurationClasses.values().length;
		Double timespanIndividually = timespanComplete/amountOfClassInstances;
	
		Double timespanOverlapping = timespanIndividually*overlappingsValue;
		
		final Double unitilizedTimeSpan = -1.;
		Double timespanEndLast = unitilizedTimeSpan;

		for(int i=0;i<amountOfClassInstances;i++)
		{
			Double timespanStart;
			Double timespanEnd;
			
			if(timespanEndLast == unitilizedTimeSpan)
			{
				timespanStart = lowestDuration;
				timespanEnd = lowestDuration+timespanIndividually;
			}
			else
			{
				timespanStart = timespanEndLast;
				timespanEnd = timespanStart+timespanIndividually;
			}
			
			timespanEndLast = timespanEnd;
			
			Double overlappingStart = Math.max(timespanStart-timespanOverlapping,1);
			Double overlappingEnd = timespanEnd+timespanOverlapping; 
		
			EDurationClasses type = EDurationClasses.values()[i];
			FuzzyResult individualFurry = new FuzzyResult(timespanStart, timespanEnd, overlappingStart, overlappingEnd);
		
			result.add(type, individualFurry);
		}
		
		return result;
	}
}
