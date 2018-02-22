package DetectAnomaliesInNewBehaviour;

public class DetectionResult {

	public int totalTracesCount=0;
	public int anomalousTraces=0;
	public int nonAnomlousTraces=0;
	@Override
	public String toString() {
		return "DetectionResult [totalTracesCount=" + totalTracesCount + ", anomalousTraces=" + anomalousTraces
				+ ", nonAnomlousTraces=" + nonAnomlousTraces + "]";
	}
}
