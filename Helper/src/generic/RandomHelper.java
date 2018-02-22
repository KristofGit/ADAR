package generic;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomHelper {
	public static final Random rand = new Random();
	
	public static int getRandomListIndex(Collection list)
	{
		if(list == null)
		{
			return 0;
		}
		
		return rand.nextInt(list.size());
	}
	
	public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = rand.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
	
	public static int randInt(int min, int max) {
	    return rand.nextInt((max - min) + 1) + min;
	}
	
	public static <T> T[] randomElements(T[] array, int amount)
	{
		if(array == null)
		{
			return null;
		}
		
		if(amount<0)
		{
			amount = 0;
		}
		
		if(amount > array.length)
		{
			return array;
		}
		
		T[] result = (T[])Array.newInstance(array.getClass().getComponentType(), amount);
		
		Set<Integer> arrayIndexesToUse = new HashSet<>();
		do
		{
			int randomIndex = rand.nextInt(array.length);
			arrayIndexesToUse.add(randomIndex);
		}while(arrayIndexesToUse.size()<amount);
		
		
		int i=0;
		for(Integer eachIndexOldArray : arrayIndexesToUse)
		{
			result[i]=array[eachIndexOldArray];
			i++;
		}
		
		return result;
		
	}
}
