package FrequentItemsets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import FrequentItemsets.Trace.TraceElement;
import RuleComponents.ICustomCompAnalyzer;
import dataStructures.UniqueObjectIdentifier;
import generic.ArrayHelper;

public class Trace<T> {

	public static class TraceElement<T> extends UniqueObjectIdentifier{
		private final int positionIndex;
		private final Trace<T> traceElementBelongsTo;
		private final T value;
		
		public TraceElement(T value, int positionIndex, Trace<T> originTrace)
		{
			this.positionIndex = positionIndex;
			this.traceElementBelongsTo = originTrace;
			this.value = value;
		}

		public int getPositionIndex() {
			return positionIndex;
		}

		public Trace<T> getTraceElementBelongsTo() {
			return traceElementBelongsTo;
		}
		
		public List<TraceElement<T>> getElementsAfter()
		{
			Trace<T> trace = traceElementBelongsTo.getElementsAfterPosition(getPositionIndex());
			return trace.getAllElements();			
		}

		public T getValue() {
			return value;
		}
		
		@Override
		public String toString() {
			return getValue().toString();
		}
	}
	
	private List<TraceElement<T>> theTrace = new ArrayList<TraceElement<T>>();
	
	public Trace()
	{
		
	}
	
	public Trace(T[] elements)
	{
		this(Arrays.asList(elements));
	}
	
	public Trace(List<T> elements)
	{
		IntStream.range(0, elements.size()).forEach(idx -> {
			T value = elements.get(idx);
			TraceElement<T> element = new TraceElement<>(value, idx, this); 
	
			theTrace.add(element);
		});
	}
	
	public List<T> getUniqueItems()
	{
		Set<T> result = new HashSet<T>();
			
		theTrace.stream().forEach(x->{
			result.add(x.getValue());
		});
				
		return new ArrayList<T>(result);
	}
	
	public List<TraceElement<T>> getAllElements()
	{
		return Collections.unmodifiableList(theTrace);
	}
	
	public Trace<T> getElementsAfterPosition(int positionIndex)
	{
		Trace<T> result = new Trace<T>();
		
		//we do not change the indexes! maybe necessary TODO
		int incrementedIndex = positionIndex+1;
		
		if(theTrace.size()>incrementedIndex)
		{
			result.addToTrace(theTrace.subList(incrementedIndex, theTrace.size()));
		}
		
		return result;
	}
	
	private void addToTrace(List<TraceElement<T>> traceElements)
	{
		this.theTrace.addAll(traceElements);
	}
	
	public boolean isEmpty()
	{
		return theTrace.isEmpty();
	}
	
	//only checks for the order, elements must not follow directly after each other
	//so e.g. A->B is also given if the trace is A->C->B
	public boolean occursInOrder(T[] collectionInOrder)
	{
		
			if(!ArrayHelper.isEmpty(collectionInOrder))
			{
				T value = collectionInOrder[0];
				
				if(value instanceof ICustomCompAnalyzer)
				{
					ICustomCompAnalyzer<T> specialAnalyzer = (ICustomCompAnalyzer<T>)value;
					
					List<T> listCollectionInOrder = Arrays.asList(collectionInOrder);
					
					return specialAnalyzer.isSupported(this, listCollectionInOrder);
				}
			}
				
		boolean result = true;
		
		int lastIndexStartedToSearch = 0;
		for(T eachTraceElement : collectionInOrder)
		{
			boolean foundElement = false;
			for(int i=lastIndexStartedToSearch;i<theTrace.size();i++)
			{
				T traceElement = theTrace.get(i).value;
				
				T valueOne = eachTraceElement;
				T valueTwo = traceElement;
				
				if(Objects.equals(valueOne, valueTwo))
				{
					foundElement = true;
					break;					
				}
				
				
				lastIndexStartedToSearch = i;
			}
			
			// we were not able to find the elements in the right order
			if(!foundElement)
			{
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	
	/*public boolean occursInOrder(TraceElement<T>[] collectionInOrder)
	{
		if(!ArrayHelper.isEmpty(collectionInOrder))
		{
			T value = collectionInOrder[0].getValue();
			
			if(value instanceof ICustomCompAnalyzer)
			{
				ICustomCompAnalyzer<T> specialAnalyzer = (ICustomCompAnalyzer<T>)value;
				
				return specialAnalyzer.isSupported(this, collectionInOrder);
			}
		}
				
		
		boolean result = true;
		
		int lastIndexStartedToSearch = 0;
		for(TraceElement<T> eachTraceElement : collectionInOrder)
		{
			boolean foundElement = false;
			for(int i=lastIndexStartedToSearch;i<theTrace.size();i++)
			{
				TraceElement<T> traceElement = theTrace.get(i);
				
				T valueOne = eachTraceElement.value;
				T valueTwo = traceElement.value;
				
				if(Objects.equals(valueOne, valueTwo))
				{
					foundElement = true;
					break;					
				}
				
				
				lastIndexStartedToSearch = i;
			}
			
			// we were not able to find the elements in the right order
			if(!foundElement)
			{
				result = false;
			}
		}
		
		return result;
	}*/
	
}
