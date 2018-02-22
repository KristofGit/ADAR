package Evaluate;

import DetectAnomaliesInNewBehaviour.DetectionResult;

public class QualityCalc {

	private int amountOfTruePositive;
	private int amountOfFalsePositive;
	private int amountOfTrueNegative;
	private int amountOfFalseNegative;
	
	public QualityCalc(DetectionResult normal, DetectionResult anomaly)
	{
		this(anomaly.anomalousTraces, normal.anomalousTraces, normal.nonAnomlousTraces, anomaly.nonAnomlousTraces);
	}
	
	public QualityCalc(int amountOfTruePositive, int amountOfFalsePositive, int amountOfTrueNegative, int amountOfFalseNegative)
	{
		this.amountOfTruePositive = amountOfTruePositive;
		this.amountOfFalsePositive = amountOfFalsePositive;
		this.amountOfTrueNegative = amountOfTrueNegative;
		this.amountOfFalseNegative = amountOfFalseNegative;
	}
	
	public void add(DetectionResult normal, DetectionResult anomaly)
	{
		this.amountOfTruePositive += anomaly.anomalousTraces;
		this.amountOfFalsePositive +=  normal.anomalousTraces;
		this.amountOfTrueNegative += normal.nonAnomlousTraces;
		this.amountOfFalseNegative += anomaly.nonAnomlousTraces;

	}
	
	
	public void calculateAndPrintQuality()
	{
		double precision = calculatePrecision();
		double recall = calculateRecall();
		double accuracy = calculateAccuracy();
		double fscore1 = calculateFScore(1);
		double fscore05 = calculateFScore(0.5);
		
		System.out.println("Precision:"+precision);
		System.out.println("Recall:"+recall);
		System.out.println("Accuracy:"+accuracy);
		System.out.println("F1 measure:"+fscore1);
		System.out.println("F0.5 measure:"+fscore05);

	}
	
	public void printRawNumbers()
	{
		System.out.println("amountOfTruePositive:"+amountOfTruePositive);
		System.out.println("amountOfFalsePositive:"+amountOfFalsePositive);
		System.out.println("amountOfTrueNegative:"+amountOfTrueNegative);
		System.out.println("amountOfFalseNegative:"+amountOfFalseNegative);
	}
	
	public double calculatePrecision()
	{
		double result = 0;
		
		result = ((double)amountOfTruePositive)/(amountOfTruePositive+amountOfFalsePositive);
		
		return result;
	}
	
	public double calculateRecall()
	{
		double result = 0;
		
		result = ((double)amountOfTruePositive)/(amountOfTruePositive+amountOfFalseNegative);
		
		return result;
	}
	
	public double calculateAccuracy()
	{
		double result = 0;
		
		result = ((double)amountOfTruePositive+amountOfTrueNegative)/(amountOfTruePositive+amountOfTrueNegative+amountOfFalsePositive+amountOfFalseNegative);
		
		return result;
	}
	
	public double calculateFScore(double beta)
	{
		double result = 0;
		
		result = (1+Math.pow(beta, 2))*((calculatePrecision()*calculateRecall())/(Math.pow(beta, 2)*calculatePrecision()+calculateRecall()));
		
		return result;
	}
}
