package LogTraceOB;

public class LogTraceElement {

	private final String activityName;
	private final String resourceName;
	private final double activityDuration; //in milliseconds
	private final int executionYear; 
	
	public boolean mutated;

	public LogTraceElement(
			String activityName, 
			String resourceName,
			double activityDuration)
	{
		this.activityName = activityName;
		this.resourceName = resourceName;
		this.activityDuration = activityDuration; 
		this.executionYear = 0;
	}
	
	public LogTraceElement(
			String activityName, 
			String resourceName,
			double activityDuration,
			int executionYear)
	{
		this.activityName = activityName;
		this.resourceName = resourceName;
		this.activityDuration = activityDuration; 
		this.executionYear = executionYear;
	}

	public String getActivityName() {
		return activityName;
	}

	public String getResourceName() {
		return resourceName;
	}

	public double getActivityDuration() {
		return activityDuration;
	}

	public int getExecutionYear() {
		return executionYear;
	}
	
	public LogTraceElement clone()
	{
		return new LogTraceElement(activityName, resourceName, activityDuration, executionYear);
	}
}
