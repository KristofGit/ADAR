package generic;

import java.lang.reflect.Array;

public class ArrayHelper {

	public static <T> T[] toArray(T... values) {
		
		return values;
	}
	
	public static <T> boolean isEmpty(T[] array)
	{
		return array == null || array.length == 0;
	}
	
	public static  <T> T getLastElement(T[] array)
	{
		if(array == null)
		{
			return null;
		}
		
		return array[array.length-1];
	}
	
	public static <T> T[] extend(T[] array, T add)
	{
		if(array == null)
		{
			return null;
		}
		
		if(add == null)
		{
			return array;
		}
		
		T[] arr = (T[])Array.newInstance(add.getClass(), array.length+1);
		
		for(int i=0;i<array.length;i++)
		{
			arr[i]=array[i];
		}
		
		arr[array.length] = add;
		
		return arr;
	}
	
	public static <T> T[] shrink(T[] array)
	{
		return shrink(array,1);
	}
	
	public static <T> T[] shrink(T[] array, int by)
	{
		if(array.length-by<=0)
		{
			by = 0; 
		}
		
		T[] arr = (T[])Array.newInstance(array.getClass().getComponentType(), array.length-by);

		for(int i=0;i<array.length-by;i++)
		{
			arr[i]=array[i];
		}
		
		return arr;
	}
}
