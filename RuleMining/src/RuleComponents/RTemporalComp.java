package RuleComponents;

import java.util.List;
import java.util.Objects;

import Configuration.Config;
import FrequentItemsets.Trace;
import FrequentItemsets.Trace.TraceElement;
import Temporal.EDurationClasses;
import Temporal.MultiFuzzyBehaviour;
import Temporal.MultiFuzzyBehaviour.SingleFuzzyResult;
import Temporal.FurryDurationGen.FuzzyResultList;

public class RTemporalComp extends RControlFlowComp implements ICustomCompAnalyzer<RTemporalComp> {

	private double durationOfActivityExact;
	private final EDurationClasses activityDuration;
	private final FuzzyResultList<EDurationClasses> fuzzyDuration; //fuzzy durations for this activity (i.e., all instances of this activitry)
	//will mainly be used when comparing the rules with new given executions.
	
	public RTemporalComp(
			String activityLabel,
			double durationOfActivityExact,
			EDurationClasses specificActivityDuration,
			FuzzyResultList<EDurationClasses> fuzzyDurationAllActivityInstances
			) {
		super(activityLabel);
		
		assert(specificActivityDuration != null);
		
		this.activityDuration = specificActivityDuration;
		this.fuzzyDuration = fuzzyDurationAllActivityInstances;
		this.durationOfActivityExact = durationOfActivityExact;
	}
	
	
	public RTemporalComp(
			String activityLabel,
			EDurationClasses specificActivityDuration,
			FuzzyResultList<EDurationClasses> fuzzyDurationAllActivityInstances
			) {
		super(activityLabel);
		
		assert(specificActivityDuration != null);
		
		this.activityDuration = specificActivityDuration;
		this.fuzzyDuration = fuzzyDurationAllActivityInstances;
	}
	
	@Override
	public String toString(){ 
		  return super.toString()+"|"+getActivityDuration()+"|";  
	} 
	
	 @Override 
	 public boolean equals(Object aThat) {
	    if ( this == aThat ) return true;

	    if ( aThat == null ||  !(aThat instanceof RTemporalComp) ) return false;

	    RTemporalComp that = (RTemporalComp)aThat;

	    boolean equalsName = super.equals(that);
	    boolean equalsDuration = getActivityDuration().equals(that.getActivityDuration());
	    
	    return equalsName && equalsDuration;
	 }

	public FuzzyResultList<EDurationClasses> getFuzzyDuration() {
		return fuzzyDuration;
	}

	public EDurationClasses getActivityDuration() {
		return activityDuration;
	}

	public boolean isSupported(Trace<RTemporalComp> trace, List<RTemporalComp> rule) {
		
		/* The idea is that the support for the specific duration class must also be taken into consideration
		 * when evaluating the overall support for the rule, So that 
		 */
			
		boolean result = true;
		
		int lastIndexStartedToSearch = 0;
		List<TraceElement<RTemporalComp>> traceElements = trace.getAllElements();
		
		for(RTemporalComp ruleElementPart : rule)
		{
			boolean foundElement = false;
			for(int i=lastIndexStartedToSearch;i<traceElements.size();i++)
			{
				RTemporalComp traceElement = traceElements.get(i).getValue();
				
				RTemporalComp valueOne = ruleElementPart;
				RTemporalComp valueTwo = traceElement;
				
				/*if(Objects.equals(valueOne, valueTwo))
				{
					
					foundElement = true;
					break;					
				}*/
				
				//equal activity, later on check equal druation 
				if(valueOne.getActivityLabel().equals(valueTwo.getActivityLabel()))
				{
					double durationOfActivity = traceElement.durationOfActivityExact;
					
					EDurationClasses expactedDurationClassFromRule = ruleElementPart.activityDuration;
					FuzzyResultList<EDurationClasses> activityDurationBehaviourGlobal = ruleElementPart.getFuzzyDuration(); //for all traces and this activity
					
					MultiFuzzyBehaviour<EDurationClasses, Double> behaviourAndCoverage = //last value indicates how well the duration supplied is supported by this duration class 
							activityDurationBehaviourGlobal.getBehaviourForDuration(durationOfActivity, EDurationClasses.class);
					
					//check if:
					//1) the duration class that I'm looking for must be supported
					//2) The duration support must be high enough
					
					SingleFuzzyResult<EDurationClasses, Double> support = behaviourAndCoverage.getBehaviour(expactedDurationClassFromRule);
					
					if(!support.isOutOfScope())
					{
						Double representationFoundForExpectedClass = support.getRepresentation();
						
						if(representationFoundForExpectedClass >= Config.FuzzyTemporalMinSupport)
						{
							foundElement = true;
							break;		
						}
					}
				}
				
				
				lastIndexStartedToSearch = i;
			}
			
			// we were not able to find the elements in the right order
			if(!foundElement)
			{
				result = false;
				break;
			}
		}
		
		return result;
	}


	public double getDurationOfActivityExact() {
		return durationOfActivityExact;
	}
}
