package FrequentItemsets;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import Configuration.Config;
import FrequentItemsets.Trace.TraceElement;
import dataStructures.Tuple;
import generic.ArrayHelper;

public class MineAprioriRules {

	/*
	 * First try of itemset mining with integer numbers only
	 */
	
	public void conductTestMinig()
	{
		double minSupport = 0.1;
		double minConfidence = 0.1;
		
		List<Trace<Integer>> availableTraces = new ArrayList<Trace<Integer>>();
	
		availableTraces.add(new Trace(Arrays.asList(1,2,3,4)));
		availableTraces.add(new Trace(Arrays.asList(5,3,4,2)));
		availableTraces.add(new Trace(Arrays.asList(1,2,3,4)));
		availableTraces.add(new Trace(Arrays.asList(1,4,2,3)));

	
					
		List<TraceElement<Integer>[]> currentItemset = createItemsetsOfSize1(availableTraces);
		
		while(true)
		{
			currentItemset = createNewItemsetsFromPreviousOnes(currentItemset);
			
			List<Tuple<RuleState<Integer>,TraceElement<Integer>[]>>  acceptableItemsets = 
						calculateFrequentItemsets(availableTraces, currentItemset, minSupport);
			
			currentItemset = acceptableItemsets.stream().map(x->x.y).collect(Collectors.toList());
		}
	}
	
	private final double minSupport;
	private final int maxRuleSize;
	
	public MineAprioriRules(double mindSupport, int maxRuleSize)
	{
		this.minSupport = mindSupport;
		this.maxRuleSize = maxRuleSize;
	}
	
	public MineAprioriRules(double mindSupport)
	{
		this(mindSupport,Config.MaxRuleSizeGeneral);
	}
	
	public <T> List<Rule<T>> mine(List<Trace<T>> tracesToMineIn)
	{
		List<Tuple<RuleState<T>,TraceElement<T>[]>> itemSetsForRules = new ArrayList<>();
		
		List<TraceElement<T>[]> currentItemset = createItemsetsOfSize1(tracesToMineIn);
		
		boolean clearResultAfterEachRound = false;
		
		int countRuleSize = 1;
		
		while(true)
		{
			
			System.out.println("Current item size:"+currentItemset.size());
			//filter lengh limitation
			currentItemset = currentItemset.parallelStream().filter(x->x!= null && x.length<maxRuleSize).collect(Collectors.toList());
			
			if(currentItemset.isEmpty()) //happens if everyhting was filtered away or we ended up at the end 
				//of the traces and because of this no new itemsets could be created
			{
				break; 
			}
		
			if(clearResultAfterEachRound)
			{
				itemSetsForRules.clear();
			}
			
			List<TraceElement<T>[]> itemsetBeforeExtension = currentItemset;
			currentItemset = createNewItemsetsFromPreviousOnes(currentItemset);
			
			countRuleSize++;
			
			//acceptableitemsets comply to the minsup, minconf, etc. requirements
			List<Tuple<RuleState<T>,TraceElement<T>[]>>  acceptableItemsets = 
					calculateFrequentItemsets(tracesToMineIn, currentItemset, minSupport);
			
			System.out.println("Current acceptible items size:"+acceptableItemsets.size() + " rule size:" + countRuleSize);

			if(clearResultAfterEachRound && acceptableItemsets.isEmpty())
			{
				currentItemset = itemsetBeforeExtension;
				System.out.println("after extension nothing was acceptable, stopping with:"+itemsetBeforeExtension.size());
				break;
			}
			
			currentItemset = acceptableItemsets.stream().filter(x->x!=null).map(x->x.y).collect(Collectors.toList());
						
			itemSetsForRules.addAll(acceptableItemsets); //store also itemsets which do not have maximum size
			//min itemset size is 2 (a IF and a THEN part for the rules)1			
		}
		
		Set<String> traceElementsAlreadyTaken = new HashSet<>();//for duplicate filtering, i.e., duplicate rules!
		
		List<Rule<T>> result = new ArrayList<>();
		
		for(Tuple<RuleState<T>,TraceElement<T>[]> eachPotentialRule : itemSetsForRules)
		{
			if(eachPotentialRule != null)
			{
				
				RuleState<T> state = eachPotentialRule.x;
				TraceElement<T>[] rule = eachPotentialRule.y;
				
				
				String ruleAsString = toString(rule);
				
				if(!traceElementsAlreadyTaken.contains(ruleAsString))
				{
					TraceElement<T>[] ifRulePartEnclosed = ArrayHelper.shrink(rule);
					TraceElement<T> thenRulePartEnclosed = ArrayHelper.getLastElement(rule);
					
					T[] ifRulePart = (T[])Arrays.stream(ifRulePartEnclosed).map(x->x.getValue()).toArray();
					T thenRulePart = thenRulePartEnclosed.getValue();
					
					result.add(new Rule<T>(ifRulePart, thenRulePart, state));
				}
			}
		}
		
		//TODO do cleanup for super rules that enclose smaller ones? 
		//however when we mine separate rule parts this can result in issues i.e., a super rule can be present for control flow
		//but not for the control flow AND temporal combination
		
		return result;
	}
	
	//analyze the itemsets and filter those which have a support below the minSupp
	private <T> List<Tuple<RuleState<T>,TraceElement<T>[]>> calculateFrequentItemsets(
			List<Trace<T>> traces,
			List<TraceElement<T>[]> itemsets, 
			double minSupport)
	{
        List<Tuple<RuleState<T>,TraceElement<T>[]>> frequentCandidates = new ArrayList<>(); //the frequent candidates for the current itemset

        //for each itemset, search all traces. Count up how frequently it occurs
        //basically a rule contains out of IF THEN, the last part of the frequent itemset is the THEN all parts davor concetenated
        //with AND are the IF 
        
        //calculate supp and conf
        itemsets.stream().parallel().forEach(eachItemset->{
        	RuleState<T> state = new RuleState<>(traces, eachItemset);
        	
        	double ruleConf = state.getConfidence();
        	double ruleSupp = state.getSupport();
        	double ruleLift = state.getLift();

        	//if(ruleConf>=minConf && ruleSupp>=minSupport)
        	if(ruleSupp >= minSupport)
        	{
        		frequentCandidates.add(new Tuple<RuleState<T>, Trace.TraceElement<T>[]>(state, eachItemset));
        	}    
        });       
        
        return frequentCandidates;
	}
		
	//we have itemsets with a given size. Create new possible ones with the given size +1
	//later on we will filter those that are out of scope (e.
	private <T> List<TraceElement<T>[]> createNewItemsetsFromPreviousOnes(List<TraceElement<T>[]> currentItemset)
	{
    	HashMap<String, TraceElement<T>[]> tempCandidates = new HashMap<String,TraceElement<T>[]>(); //temporary candidates
    	
		for(TraceElement<T>[] eachItemSet : currentItemset)
		{
			//we have the last trace element
			TraceElement<T> lastItemSetElement = ArrayHelper.getLastElement(eachItemSet);
			
			//we get all elements that occur after
			List<TraceElement<T>> elementsAfter = lastItemSetElement.getElementsAfter();
			
			//we create new itemsets by combining the old and the element after (old one is extended by exactly one)
			for(TraceElement<T> eachToExtendItemset : elementsAfter)
			{
				TraceElement<T>[] newItemset = ArrayHelper.extend(eachItemSet, eachToExtendItemset);

				String keyForDuplicateDetection = toString(newItemset);
			
				//we check for duplicates! 
				//TODO must this be addepted if we have loops were the same activity is executed one after another over and over again?
				if(!tempCandidates.containsKey(keyForDuplicateDetection))
				{
					tempCandidates.put(keyForDuplicateDetection, newItemset);
				}
			}
		}
		
		return new ArrayList<TraceElement<T>[]>(tempCandidates.values());
	}	
	
	private <T> String toString(TraceElement<T>[] itemset)
	{
		StringBuilder result = new StringBuilder();
		
		if(itemset == null)
		{
			return "";
		}
		
		Arrays.stream(itemset).forEach(x->{
			result.append(x.getValue().toString());
		});
		
		return result.toString();
	}
	
	private <T> List<TraceElement<T>[]> createItemsetsOfSize1(List<Trace<T>> traces)
	{
		List<TraceElement<T>[]> itemsets = new ArrayList<TraceElement<T>[]>();

		for(Trace<T> eachTrace : traces)
		{
			List<TraceElement<T>> traceContent = eachTrace.getAllElements();
			
			traceContent.stream().forEach(x->{
				TraceElement<T> toAdd = x;
				itemsets.add(new TraceElement[]{toAdd});
			});
		}
		
        return itemsets;
	}
}
