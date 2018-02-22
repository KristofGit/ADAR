package GenerateAnomalies;

public enum AnomalyStrengh {

	A(2, 4,5),
	B(3, 4,5);

	private final int amountOfAnomalies;
	private final int applicationTimesMin;
	private final int applicationTimesMax;

	private AnomalyStrengh(int amountOfAnomalies, int applicationTimesMin, int applicationTimesMax)
	{
		this.amountOfAnomalies = amountOfAnomalies;
		this.applicationTimesMin = applicationTimesMin;
		this.applicationTimesMax = applicationTimesMax;
	}

	public int getAmountOfAnomalies() {
		return amountOfAnomalies;
	}

	public int getApplicationTimesMin() {
		return applicationTimesMin;
	}

	public int getApplicationTimesMax() {
		return applicationTimesMax;
	}
}
