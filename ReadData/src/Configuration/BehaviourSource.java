package Configuration;

public enum BehaviourSource {

	HEP4("/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Rule Based Anomaly Detection/Entwicklung NEU/HEP/all_Kurs02_se04.xes"),
	HEP1("/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Rule Based Anomaly Detection/Entwicklung NEU/HEP/all_Kurs02_se01.xes"),
	HEP3("/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Rule Based Anomaly Detection/Entwicklung NEU/HEP/all_Kurs02_se03.xes"),

	BPIC1("/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Rule Based Anomaly Detection/Entwicklung NEU/BPIC/BPIC15_1.xes"),
	BPIC2("/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Rule Based Anomaly Detection/Entwicklung NEU/BPIC/BPIC15_2.xes"),
	BPIC3("/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Rule Based Anomaly Detection/Entwicklung NEU/BPIC/BPIC15_3.xes"),
	BPIC4("/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Rule Based Anomaly Detection/Entwicklung NEU/BPIC/BPIC15_4.xes"),
	BPIC5("/home/esad/BTSync/UNI/Doktorrat/Initale Arbeiten/Rule Based Anomaly Detection/Entwicklung NEU/BPIC/BPIC15_5.xes");

	private String path;
	
	private BehaviourSource(String pathToLogFile)
	{
		this.path = pathToLogFile;
	}
	
	public String getPathToLog()
	{
		return this.path;
	}
}
