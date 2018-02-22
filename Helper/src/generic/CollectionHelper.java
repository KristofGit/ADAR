package generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dataStructures.Tuple;

public class CollectionHelper {

	public static <T> Tuple<List<T>,List<T>> separate(List<T> values, double ratio)
	{
		int sizeToExtract = (int) (values.size()*ratio);
		
		List<T> extracted = new ArrayList<>();
		List<T> remaining = new ArrayList<>(values);
		
		for(int i=0;i<sizeToExtract;i++)
		{
			int randomIndex = RandomHelper.getRandomListIndex(remaining);
			
			T value = remaining.get(randomIndex);
			remaining.remove(randomIndex);
			
			extracted.add(value);
		}
		
		return new Tuple<List<T>, List<T>>(remaining, extracted);
	}
	
	public static boolean anyEquals(Collection... args)
	{
		boolean result = true;
		
		if(ArrayHelper.isEmpty(args))
		{
			return result;
		}
		
		Set intersection = new HashSet();
		intersection.addAll(args[0]);
		
		
		for(Collection eachCollection : args)
		{
			intersection.retainAll(eachCollection);
			
			if(intersection.isEmpty())
			{
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	public static <T> T lastElement(List<T> list)
	{
		if(list == null || list.isEmpty())
		{
			return null;
		}
		
		return list.get(list.size()-1);
	}
	
	public static <T> void add(int index, T value, List<T> list)
	{
		if(index<0)
		{
			index = 0;
		}
		
		if(index >list.size())
		{
			index = list.size();
		}
		
		if(value == null || list == null)
		{
			return;
		}
		
		list.add(index, value);
	}	
}
