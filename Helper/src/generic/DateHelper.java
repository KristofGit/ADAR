package generic;

import java.util.Date;

public class DateHelper {

	public static double duration(Date one, Date two)
	{
		if(one == null || two == null)
		{
			return 0;
		}
		
		double duration = Math.abs(one.getTime()-two.getTime());
		
		return Math.max(duration, 1); //prevents a duration of 0 which could end up in dividion by zero problems
	}
}
