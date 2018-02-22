package Main;

import java.util.List;

import Configuration.BehaviourSource;
import Configuration.Config;
import DetectAnomaliesInNewBehaviour.Detect;
import FrequentItemsets.MineAprioriRules;
import FrequentItemsets.Rule;
import FrequentItemsets.Trace;
import GeneralAnomalyDetection.LogTracePreparation;
import LogTraceOB.LogTrace;
import Reading.ReadFromLogs;
import Resources.EResourceClasses;
import RuleComponents.RControlFlowComp;
import RuleComponents.RResourceComp;
import RuleComponents.RTemporalComp;
import dataStructures.Tuple;
import generic.CollectionHelper;

public class main {

	public static void main(String[] args) {
		BehaviourSource source = Config.DataToUse;
		
		ReadFromLogs read = new ReadFromLogs();
		List<LogTrace> allTracesFromLog = read.Read(source);
		
		/* First mine control flow rules
		 * Then check for the control flow rules if they hold for temporal and resource aspects too
		 * This is an optmization technique, because if something does not hold for control flow aspects then it does also not hold
		 * for temporal or resources
		 * 
		 * TODO: Later on combine the part roules (control flow, resource, temporal) into super roules that join
		 *  together all three parts becoming more specific and powerfull?
		 */
		
		//start with minin control flow rules:
				
		
		Tuple<List<LogTrace>,List<LogTrace>> resparatedData = CollectionHelper.separate(allTracesFromLog, 0.5);
		
		List<LogTrace> trainingData = resparatedData.x;
		List<LogTrace> testingData = resparatedData.y;

		
		List<Trace<RControlFlowComp>> controlFlowTracesTraining = 
				LogTracePreparation.prepareControlFlowTraces(trainingData);
		
		List<Trace<RControlFlowComp>> controlFlowTracesTestData = 
				LogTracePreparation.prepareControlFlowTraces(testingData);
		
		List<Trace<RTemporalComp>> temporalTracesTraining =
				LogTracePreparation.prepareTemporalTraces(trainingData, Config.FuzzyTemporalOverlap);
		List<Trace<RTemporalComp>> temporalTracesTestData =
				LogTracePreparation.prepareTemporalTraces(testingData, Config.FuzzyTemporalOverlap);

		List<Trace<RResourceComp>> resourceTracesSameTrainingBOD = 
				LogTracePreparation.prepareResourceTraces(trainingData, EResourceClasses.SameResource);
		List<Trace<RResourceComp>> resourceTracesSameTestDataBOD = 
				LogTracePreparation.prepareResourceTraces(testingData, EResourceClasses.SameResource);
		
		List<Trace<RResourceComp>> resourceTracesDiffTrainingSOD = 
				LogTracePreparation.prepareResourceTraces(trainingData, EResourceClasses.DifferentResource);
		List<Trace<RResourceComp>> resourceTracesDiffTestDataSOD = 
				LogTracePreparation.prepareResourceTraces(testingData, EResourceClasses.DifferentResource);
	 
		
		
		MineAprioriRules miner = new MineAprioriRules(Config.RuleMinSupp);
		List<Rule<RResourceComp>> bodRules = miner.mine(resourceTracesSameTrainingBOD);
		List<Rule<RResourceComp>> sodRules = miner.mine(resourceTracesDiffTrainingSOD);
		
		List<Rule<RControlFlowComp>> controlFlowRules = miner.mine(controlFlowTracesTraining);
		List<Rule<RTemporalComp>> temporalRules = miner.mine(temporalTracesTraining);


				
		for(int i=0;i<testingData.size();i++)
		{
			Detect detect = new Detect(sodRules,bodRules,temporalRules,controlFlowRules);

			Trace<RControlFlowComp> traceTestingControl = controlFlowTracesTestData.get(i);
			Trace<RTemporalComp> traceTestingTemporal = temporalTracesTestData.get(i);

			detect.dermineAnomalyScoreControlFlow(traceTestingControl);
			detect.dermineAnomalyScoreTemporal(traceTestingTemporal);

		}
		
		//List<Rule<RTemporalComp>> temporalRules = miner.mine(temporalTraces);
		//List<Rule<RResourceComp>> resourcesRulesSame = miner.mine(resourceTracesSame);
		//List<Rule<RResourceComp>> resourcesRulesDiff = miner.mine(resourceTracesDiff);


	}

}
