package generic;

public class Normalization {

	public static double  normalize(double value, int divideBy)
	{
		if(value == 0)
		{
			return 0;
		}
		
		if(divideBy == 0)
		{
			return value;
		}
		
		return value/divideBy;
	}
}
