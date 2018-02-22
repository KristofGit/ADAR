package Temporal;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MultiFuzzyBehaviour<T extends Enum<?>, N extends Number> {

	public static class SingleFuzzyResult<T,N extends Number>{
		private T queriedType;
		private N representation;
		
		public SingleFuzzyResult()
		{
		}
		
		public SingleFuzzyResult(T t, N n)
		{
			queriedType = t;
			representation = n;
		}
		
		public boolean isOutOfScope() //if the behavior is not represented 
		{
			return queriedType == null || representation == null;
		}

		public N getRepresentation() {
			return representation;
		}
	};
	
	private Map<T, N> behaviour = new HashMap<T, N>();
	
	public T getMostLikelyBehaviour()
	{
		T result = null;
		N highestN = null;
		for(Entry<T, N> eachEntry : behaviour.entrySet())
		{
			N assignmentPercentage = eachEntry.getValue(); //how strongly it is assigned to a specific class, higher => better
			T assignmentClass = eachEntry.getKey();
			
			if(highestN == null || assignmentPercentage.doubleValue()>highestN.doubleValue())
			{
				highestN = assignmentPercentage;
				result = assignmentClass;
			}	
		}
		
		return result;
	}
	
	public void addBehviour(T type, N representation) 
	{
		behaviour.put(type, representation);
	}
	
	public boolean isEmpty()
	{
		return behaviour.isEmpty();
	}
	
	public SingleFuzzyResult<T, N> getBehaviour(T type)
	{
		N representation = behaviour.get(type);
		
		if(representation == null)
		{
			return new SingleFuzzyResult<>();
		}
		
		return new SingleFuzzyResult<>(type, representation);
	}

}
