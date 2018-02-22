package DetectAnomaliesInNewBehaviour;

import java.awt.event.TextEvent;
import java.util.List;

import Configuration.Config;
import FrequentItemsets.Rule;
import FrequentItemsets.Trace;
import GeneralAnomalyDetection.LogTracePreparation;
import LogTraceOB.LogTrace;
import RuleComponents.RControlFlowComp;
import RuleComponents.RResourceComp;
import RuleComponents.RTemporalComp;
import dataStructures.Tuple;
import generic.Normalization;

public class Detect {

	/*
	 * Take the historiuc data and generate rules from them
	 * See how much rules match on one of the traces to analyze
	 * Calculate their anoamyly score
	 * If this score is below the minimum score of the historic traces then its an anomaly
	 */
	
	private final List<Rule<RControlFlowComp>> ruleControlFlow;
	private final List<Rule<RTemporalComp>> ruleTemporal;
	private final List<Rule<RResourceComp>> ruleResourceSOD;
	private final List<Rule<RResourceComp>> ruleResourceBOD;

	private final int maxRuleSize = Config.MaxRuleSizeGeneral;

	public Detect(
			List<Rule<RResourceComp>> ruleResourceSOD,
			List<Rule<RResourceComp>> ruleResourceBOD,
			List<Rule<RTemporalComp>> ruleTemporal,
			List<Rule<RControlFlowComp>> ruleControlFlow)
	{
		this.ruleControlFlow = ruleControlFlow;
		this.ruleTemporal = ruleTemporal;
		this.ruleResourceSOD = ruleResourceSOD;
		this.ruleResourceBOD = ruleResourceBOD;
	}
	
	/*
	 * Each rule has an individual importance depending on its support
	 * 
	 * By summing up the support for the supported/non supported rules 
	 * we get an anomaly score (support sum of all non supported rules)
	 * and an normal score (support sum of all supported rule)
	 * 
	 * These scores are then compared to determine if a trace os an anomaly or if it is not
	 * The rules that are not supported can be collected and ranked based on their support to identify the most important/influencal ones
	 * 
	 * We will not find the same amount of rules in all three contextual aspects and data types
	 * so when aggreagting their value we have to normalize them and potentially also define their importance (e.g., if SOD/BOD is more important then time) 
	 * Or contorl flow more imporant thent he other two
	 * 
	 * TODO: Potentially we can also add the rule length to it to argue that longer rules are harder to support and
	 * should have an higher impact
	 */
	
	public  Tuple<Double, Double> dermineAnomalyScoreBOD(Trace<RResourceComp> trace)
	{
		double supSumTemporalNor=0;
		double supSumTemporalAnon=0;

		for(Rule<RResourceComp> eachRule : ruleResourceBOD)
		{
			int ruleSizeModivierReversed = maxRuleSize-eachRule.getSize();
			
			ruleSizeModivierReversed = Math.max(1, ruleSizeModivierReversed);
			
			double sup = eachRule.getRuleSupportInTraining() * ruleSizeModivierReversed;
			
			if(eachRule.IsSupported(trace))
			{
				supSumTemporalNor+=sup;
			}
			else
			{
				supSumTemporalAnon+=sup;
			}
		}
		
		supSumTemporalNor = Normalization.normalize(supSumTemporalNor, ruleResourceBOD.size());
		supSumTemporalAnon = Normalization.normalize(supSumTemporalAnon, ruleResourceBOD.size());
		
		return new Tuple<Double, Double>(supSumTemporalNor, supSumTemporalAnon);
	}
	
	public Tuple<Double, Double> dermineAnomalyScoreSOD(Trace<RResourceComp> trace)
	{
		double supSumTemporalNor=0;
		double supSumTemporalAnon=0;

		for(Rule<RResourceComp> eachRule : ruleResourceSOD)
		{
			int ruleSizeModivierReversed = maxRuleSize-eachRule.getSize();
			
			ruleSizeModivierReversed = Math.max(1, ruleSizeModivierReversed);
			
			double sup = eachRule.getRuleSupportInTraining() * ruleSizeModivierReversed;
			
			if(eachRule.IsSupported(trace))
			{
				supSumTemporalNor+=sup;
			}
			else
			{
				supSumTemporalAnon+=sup;
			}
		}
		
		supSumTemporalNor = Normalization.normalize(supSumTemporalNor, ruleResourceSOD.size());
		supSumTemporalAnon = Normalization.normalize(supSumTemporalAnon, ruleResourceSOD.size());
		
		return new Tuple<Double, Double>(supSumTemporalNor, supSumTemporalAnon);
	}
	
	public DetectionResult dermineAnomalyScoreTemporal(List<Trace<RTemporalComp>> trace)
	{
		DetectionResult result = new DetectionResult();
		result.totalTracesCount = trace.size();
		
		for(Trace<RTemporalComp> eachTrace : trace)
		{
			Tuple<Double,Double> resultSingle = dermineAnomalyScoreTemporal(eachTrace);
			
			double scoreNormal = resultSingle.x;
			double scoreAnomaly = resultSingle.y;
		
			if(scoreAnomaly>scoreNormal)
			{
				result.anomalousTraces++;
			}
			else
			{
				result.nonAnomlousTraces++;
			}
		}
		
		return result;
	}
	
	
	public Tuple<Double, Double> dermineAnomalyScoreTemporal(Trace<RTemporalComp> trace)
	{
		double supSumTemporalNor=0;
		double supSumTemporalAnon=0;

		for(Rule<RTemporalComp> eachRule : ruleTemporal)
		{			
			int ruleSizeModivierReversed = maxRuleSize-eachRule.getSize();
			
			ruleSizeModivierReversed = Math.max(1, ruleSizeModivierReversed);
			
			double sup = eachRule.getRuleSupportInTraining() * ruleSizeModivierReversed;
			
			if(eachRule.IsSupported(trace))
			{
				supSumTemporalNor+=sup;
			}
			else
			{
				supSumTemporalAnon+=sup;
			}
		}
		
		supSumTemporalNor = Normalization.normalize(supSumTemporalNor, ruleTemporal.size());
		supSumTemporalAnon = Normalization.normalize(supSumTemporalAnon, ruleTemporal.size());
		
		return new Tuple<Double, Double>(supSumTemporalNor, supSumTemporalAnon);
	}
	
	public DetectionResult dermineAnomalyScoreControlFlow(List<Trace<RControlFlowComp>> trace)
	{
		DetectionResult result = new DetectionResult();
		result.totalTracesCount = trace.size();
		
		for(Trace<RControlFlowComp> eachTrace : trace)
		{
			Tuple<Double,Double> resultSingle = dermineAnomalyScoreControlFlow(eachTrace);
			
			double scoreNormal = resultSingle.x;
			double scoreAnomaly = resultSingle.y;
		
			if(scoreAnomaly>scoreNormal)
			{
				result.anomalousTraces++;
			}
			else
			{
				result.nonAnomlousTraces++;
			}
		}
		
		return result;
	}
	
	public Tuple<Double,Double> dermineAnomalyScoreControlFlow(Trace<RControlFlowComp> trace)
	{
		double supSumControlFlowNor=0;
		double supSumControlFlowAnon=0;

		for(Rule<RControlFlowComp> eachRule : ruleControlFlow)
		{
			int ruleSizeModivierReversed = maxRuleSize-eachRule.getSize();
			
			ruleSizeModivierReversed = Math.max(1, ruleSizeModivierReversed);
			
			double sup = eachRule.getRuleSupportInTraining() * ruleSizeModivierReversed;
			
			if(eachRule.IsSupported(trace))
			{
				supSumControlFlowNor+=sup;
			}
			else
			{
				supSumControlFlowAnon+=sup;
			}
		}
		
		supSumControlFlowNor = Normalization.normalize(supSumControlFlowNor, ruleControlFlow.size());
		supSumControlFlowAnon = Normalization.normalize(supSumControlFlowAnon, ruleControlFlow.size());
		
		return new Tuple<Double, Double>(supSumControlFlowNor, supSumControlFlowAnon);
	}
	
}
