package Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import Configuration.BehaviourSource;
import Configuration.Config;
import DetectAnomaliesInNewBehaviour.Detect;
import DetectAnomaliesInNewBehaviour.DetectionResult;
import Evaluate.QualityCalc;
import FrequentItemsets.MineAprioriRules;
import FrequentItemsets.Rule;
import FrequentItemsets.Trace;
import GeneralAnomalyDetection.LogTracePreparation;
import GenerateAnomalies.AnomalyGen;
import LogTraceOB.LogTrace;
import Reading.ReadFromLogs;
import Resources.EResourceClasses;
import RuleComponents.RControlFlowComp;
import RuleComponents.RResourceComp;
import RuleComponents.RTemporalComp;
import Similarity.TraceSimilarity;
import dataStructures.Tuple;
import generic.CollectionHelper;
import generic.Normalization;

public class main {

	static List<Rule<RResourceComp>> bodRules = new ArrayList<>();
	static List<Rule<RResourceComp>> sodRules = new ArrayList<>();
	
	static List<Rule<RControlFlowComp>> controlFlowRules = new ArrayList<>();
	static List<Rule<RTemporalComp>> temporalRules = new ArrayList<>();

	
	public static void main(String[] args) {
	
		BehaviourSource source = Config.DataToUse;

		ReadFromLogs read = new ReadFromLogs();
		List<LogTrace> allOriginalTraces = read.Read(source);

		Set<Integer> years = new HashSet<Integer>();
		
		for(LogTrace eachTrace : allOriginalTraces)
		{
			List<Integer> listOfYears = eachTrace.getTraceElements().stream().map(x->x.getExecutionYear()).distinct().collect(Collectors.toList());
			years.addAll(listOfYears);
		}
		System.out.println("Years in data covered:" + years);

		QualityCalc calc = null;

		
		for(Integer eachYear : years)
		{
			eachYear = 2015;
			List<LogTrace> tracesFilteredByYear = new ArrayList<LogTrace>();

			for(LogTrace eachTrace : allOriginalTraces)
			{
				List<Integer> listOfYears = eachTrace.getTraceElements().stream().map(x->x.getExecutionYear()).distinct().collect(Collectors.toList());
				
				if(listOfYears.contains(eachYear))
				{
					tracesFilteredByYear.add(eachTrace);
				}
			}
			System.out.println("year:"+eachYear);
			System.out.println("Traces before filtering " + allOriginalTraces.size() + " , after filtering:" + tracesFilteredByYear.size());
			
			if(source == BehaviourSource.HEP1 || source == BehaviourSource.HEP3 || source == BehaviourSource.HEP4)
			{
				tracesFilteredByYear = allOriginalTraces; //for hep we take all traces
			}
			
			
		for(int i=0;i<Config.amountOfEvaluationRuns;i++)
		{
			System.out.print("Traces original amount:"+tracesFilteredByYear.size());
			
			Tuple<List<LogTrace>,List<LogTrace>> separatedTrainingTesting = CollectionHelper.separate(tracesFilteredByYear, 0.5);
			
			List<LogTrace> trainingData = separatedTrainingTesting.y;
			List<LogTrace> testingData = separatedTrainingTesting.x;

			System.out.println("Traces training amount:"+trainingData.size());
			System.out.println("Traces testing amount:"+testingData.size());

			Tuple<List<LogTrace>,List<LogTrace>> separatedTesting = CollectionHelper.separate(testingData, 0.5);

			List<LogTrace> testingDataNormal = separatedTesting.y;
			List<LogTrace> testingDataAnomaly = separatedTesting.x;

			System.out.println("Traces testing anomaly amount:"+testingDataAnomaly.size());
			System.out.println("Traces testing normal amount:"+testingDataNormal.size());
			
			System.out.println("Turn into anomaly");
			testingDataAnomaly = AnomalyGen.turnIntoAnomaly(testingDataAnomaly);
			
			System.out.println("Generate rules");
			generateRules(trainingData);
			
			System.out.println(Config.RuleMinSupp);
			System.out.println("Training");
			double scoreTraining = maxAnomalyScore(trainingData);
			System.out.println("Testing");
			double scoreTesting = maxAnomalyScore(testingDataNormal);
			System.out.println("Anomaly");
			double scoreAnomaly = maxAnomalyScore(testingDataAnomaly);
			
			
			System.out.println("rule size general:"+Config.MaxRuleSizeGeneral + " min support:"+ Config.RuleMinSupp);
			
			DetectionResult normal = detectAnomalies(trainingData, testingDataNormal, scoreTraining);
			DetectionResult anomaly = detectAnomalies(trainingData, testingDataAnomaly, scoreTraining);
			
			System.out.println("normal:"+normal);
			System.out.println("anomaly:"+anomaly);
		
			if(calc == null)
			{
				calc = new QualityCalc(normal,anomaly);
			}
			else
			{
				calc.add(normal, anomaly);
			}
			
			System.out.println("Result after runs:"+i);
			calc.calculateAndPrintQuality();
		}
break;
		}
		
		calc.printRawNumbers();
	}
	
	private static double maxAnomalyScore(List<LogTrace> traces)
	{
		return maxAnomalyScore(traces, true);
	}
	
	private static double maxAnomalyScore(List<LogTrace> traces, boolean print)
	{
		double maxAnomalyScore = 0;
		double totalAnomalyScoreSum = 0;
		double minAnomalyScore = Double.MAX_VALUE;

		List<Trace<RControlFlowComp>> controlFlowTraces = 
				LogTracePreparation.prepareControlFlowTraces(traces);
				
		List<Trace<RTemporalComp>> temporalTraces =
				LogTracePreparation.prepareTemporalTraces(traces, Config.FuzzyTemporalOverlap);

		List<Trace<RResourceComp>> resourceTracesSame = 
				LogTracePreparation.prepareResourceTraces(traces, EResourceClasses.SameResource);
		
		List<Trace<RResourceComp>> resourceTracesDiff = 
				LogTracePreparation.prepareResourceTraces(traces, EResourceClasses.DifferentResource);
		
		Detect detect = new Detect(sodRules,bodRules,temporalRules,controlFlowRules);

		for(int i=0;i<traces.size();i++)
		{		
			double totalNormalScore=0;
			double totalAnomalyScore=0;
			
			Trace<RControlFlowComp> traceControlFlow = controlFlowTraces.get(i);
			Trace<RTemporalComp> traceTemporal = temporalTraces.get(i);
			Trace<RResourceComp> traceResourceSOD = resourceTracesDiff.get(i);
			Trace<RResourceComp> traceResourceBOD = resourceTracesSame.get(i);

			Tuple<Double,Double> scroreControlFlow = detect.dermineAnomalyScoreControlFlow(traceControlFlow);
			Tuple<Double,Double> scoreTemporal = detect.dermineAnomalyScoreTemporal(traceTemporal);
			Tuple<Double,Double> scoreSod = detect.dermineAnomalyScoreSOD(traceResourceSOD);
			Tuple<Double,Double> scoreBOD = detect.dermineAnomalyScoreBOD(traceResourceBOD);

			totalNormalScore+=scroreControlFlow.x;
			totalNormalScore+=scoreTemporal.x;

			totalAnomalyScore+=scroreControlFlow.y;
			totalAnomalyScore+=scoreTemporal.y;

			totalAnomalyScore+=scoreSod.y;
			totalAnomalyScore+=scoreSod.y;
			
			totalAnomalyScore+=scoreBOD.y;
			totalAnomalyScore+=scoreBOD.y;
			
			totalAnomalyScore = Normalization.normalize(totalAnomalyScore, 4);
			
			maxAnomalyScore = Math.max(maxAnomalyScore, totalAnomalyScore);
			totalAnomalyScoreSum+=totalAnomalyScore;
			minAnomalyScore = Math.min(minAnomalyScore, totalAnomalyScore);

		}
		
		if(print)
		{
		System.out.println("");
		System.out.println("Min anomaly Score:"+minAnomalyScore);
		System.out.println("Max anomaly Score:"+maxAnomalyScore);
		System.out.println("AVG anomaly Score:"+totalAnomalyScoreSum/traces.size());
		}
		
		return (totalAnomalyScoreSum/traces.size());
		//return maxAnomalyScore;
	}
		
	private static DetectionResult detectAnomalies(List<LogTrace> trainingData, List<LogTrace> tracesIdentifyAnomaliesIn, double scoreAnomalyComparison)
	{
		
		DetectionResult result = new DetectionResult();
		result.totalTracesCount = tracesIdentifyAnomaliesIn.size();
		
		List<Trace<RControlFlowComp>> controlFlowTraces = 
				LogTracePreparation.prepareControlFlowTraces(tracesIdentifyAnomaliesIn);
				
		List<Trace<RTemporalComp>> temporalTraces =
				LogTracePreparation.prepareTemporalTraces(tracesIdentifyAnomaliesIn, Config.FuzzyTemporalOverlap);

		List<Trace<RResourceComp>> resourceTracesSame = 
				LogTracePreparation.prepareResourceTraces(tracesIdentifyAnomaliesIn, EResourceClasses.SameResource);
		
		List<Trace<RResourceComp>> resourceTracesDiff = 
				LogTracePreparation.prepareResourceTraces(tracesIdentifyAnomaliesIn, EResourceClasses.DifferentResource);
		
		Detect detect = new Detect(sodRules,bodRules,temporalRules,controlFlowRules);

		for(int i=0;i<tracesIdentifyAnomaliesIn.size();i++)
		{		
			double totalNormalScore=0;
			double totalAnomalyScore=0;
			
			LogTrace traceToIdentifyAnomalie = tracesIdentifyAnomaliesIn.get(i);
			LogTrace mostSimilarTraceFromTraining = TraceSimilarity.findMostSimilarOne(trainingData, traceToIdentifyAnomalie);

			scoreAnomalyComparison = maxAnomalyScore(Arrays.asList(mostSimilarTraceFromTraining), false);
			
			Trace<RControlFlowComp> traceControlFlow = controlFlowTraces.get(i);
			Trace<RTemporalComp> traceTemporal = temporalTraces.get(i);
			Trace<RResourceComp> traceResourceSOD = resourceTracesDiff.get(i);
			Trace<RResourceComp> traceResourceBOD = resourceTracesSame.get(i);

			Tuple<Double,Double> scroreControlFlow = detect.dermineAnomalyScoreControlFlow(traceControlFlow);
			Tuple<Double,Double> scoreTemporal = detect.dermineAnomalyScoreTemporal(traceTemporal);
			Tuple<Double,Double> scoreSod = detect.dermineAnomalyScoreSOD(traceResourceSOD);
			Tuple<Double,Double> scoreBOD = detect.dermineAnomalyScoreBOD(traceResourceBOD);

			totalNormalScore+=scroreControlFlow.x;
			totalNormalScore+=scoreTemporal.x;

			totalAnomalyScore+=scroreControlFlow.y;
			totalAnomalyScore+=scoreTemporal.y;

			totalAnomalyScore+=scoreSod.y;
			totalAnomalyScore+=scoreSod.y;
			
			totalAnomalyScore+=scoreBOD.y;
			totalAnomalyScore+=scoreBOD.y;
			
			totalAnomalyScore = Normalization.normalize(totalAnomalyScore, 4);

			/*if(totalAnomalyScore>totalNormalScore)
			{
				result.anomalousTraces+=1;
			}
			else
			{
				result.nonAnomlousTraces+=1;
			}*/
			
			if(totalAnomalyScore>scoreAnomalyComparison)
			{
				result.anomalousTraces+=1;

			}
			else
			{
				result.nonAnomlousTraces+=1;

			}
		}

		
		return result;
	}
	
	
	private static void generateRules(List<LogTrace> trainingData)
	{
		List<Trace<RControlFlowComp>> controlFlowTracesTraining = 
				LogTracePreparation.prepareControlFlowTraces(trainingData);
				
		List<Trace<RTemporalComp>> temporalTracesTraining =
				LogTracePreparation.prepareTemporalTraces(trainingData, Config.FuzzyTemporalOverlap);

		List<Trace<RResourceComp>> resourceTracesSameTrainingBOD = 
				LogTracePreparation.prepareResourceTraces(trainingData, EResourceClasses.SameResource);
		
		List<Trace<RResourceComp>> resourceTracesDiffTrainingSOD = 
				LogTracePreparation.prepareResourceTraces(trainingData, EResourceClasses.DifferentResource);

	
		MineAprioriRules minerNormal = new MineAprioriRules(Config.RuleMinSupp);
		MineAprioriRules mineTemporal = new MineAprioriRules(Config.RuleMinSuppTemp);

		MineAprioriRules minerResource = new MineAprioriRules(1.0, Config.MaxRuleSizeResource);

		//bodRules = miner.mine(resourceTracesSameTrainingBOD);
		//sodRules = miner.mine(resourceTracesDiffTrainingSOD);
		
		System.out.println("Rule Control Flow");
		controlFlowRules = minerNormal.mine(controlFlowTracesTraining);
		
		System.out.println("Rule temmporal");
		temporalRules = mineTemporal.mine(temporalTracesTraining);
		
		System.out.println("Rule SOD");
		//sodRules = minerResource.mine(resourceTracesDiffTrainingSOD);
		
		System.out.println("Rule BOD");
		bodRules = minerResource.mine(resourceTracesSameTrainingBOD);
	}

}
