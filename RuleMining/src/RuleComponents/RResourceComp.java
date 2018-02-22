package RuleComponents;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;

import FrequentItemsets.Trace;
import FrequentItemsets.Trace.TraceElement;
import Resources.EResourceClasses;
import dataStructures.Tuple;

public class RResourceComp extends RControlFlowComp implements ICustomCompAnalyzer<RResourceComp>{
	private final EResourceClasses resourceMode; //konfiguriert SOD oder BOD!
	private final String resourceName; //statt classes. flexibler. Equals ändert sich aber je nach class! 
	//um SOD (Resource name Unterschiedliche) and BOD (resource name gleich) zu unterstützen

	public RResourceComp(String activityLabel, EResourceClasses resourceMode, String resourceName) {
		super(activityLabel);
		this.resourceMode = resourceMode;
		this.resourceName = resourceName;
	}		
	
	@Override
	public String toString(){ 
		  return super.toString()+"|"; //use only the activity name to generate keys. Else we run into problems
		  //as the keys are too unique when using the resource names
		  //and the amount of rules that are generated an need to be checked is too high
	} 
	
	 @Override 
	 public boolean equals(Object aThat) {
	    /*if ( this == aThat ) return true;

	    if ( aThat == null ||  !(aThat instanceof RResourceComp) ) return false;

	    RResourceComp that = (RResourceComp)aThat;

	    String otherResource = that.resourceName;
	    String thisResource = resourceName;
	    
	    if(resourceMode == EResourceClasses.SameResource) //BOD
	    {
	    	return super.equals(that) && otherResource.equals(thisResource); 
	    }
	    else //SOD
	    {
	    	return super.equals(that) && !otherResource.equals(thisResource); 
	    }*/
		 
		 return super.equals(aThat);
	 }

	@Override
	public boolean isSupported(Trace<RResourceComp> trace, List<RResourceComp> rule) {

		//TODO SEEMS NOT TO BE NECESSARY AS THIS IS AUTOMATICALLY HANDLED BY THE SOLUTION BELOW
		//WHICH CHECKS FOR SOD AND BOD
		//first check if the rule can be applied. Hence does the process contain the necessary activities
		//if this is not the case then reply that the rule is supported as it could not be decided if it is or not 
		//(so there is no case agains the rule!)
		/*Set<String> activityNameTraces = trace.getAllElements().stream().map(x->x.getValue().getActivityLabel()).collect(Collectors.toSet());
		Set<String> activityNameRule = Arrays.stream(rule).map(x->x.getValue().getActivityLabel()).collect(Collectors.toSet());

		boolean allActivitesInRuleAreInTrace = activityNameTraces.containsAll(activityNameRule);
		
		if(!allActivitesInRuleAreInTrace)
		{
			return true; //if not all activities are given then return that it is supported
		}*/
		
		
		//special checker for SOD and BOD 
		//through this we can check the whole trace at once were the generic equals checker are insufficient
	
		/* Checking for BOD
		 * Works only for "simple" traces. So loops are supported and also parallelism
		 * but problems can occur if the same sub sequence of the trace (with the same activities) is executed 
		 * in a parallel fashion. Based on the execution traces it is no longer possible to determine
		 * to which parallel branch the activities belong and so BOD violations can not be detected
		 */

		//Set<String> resourcesThatWasBoundTo = new HashSet<String>();
		
		//sorted by occurrence (the content) and the overall list is sorted by the rule
		/*List<List<TraceElement<RResourceComp>>> matchingResourceAcivities = new ArrayList<>();
		
		for(TraceElement<RResourceComp> eachRulePart : rule)
		{
			List<TraceElement<RResourceComp>> activityOccurrencesForRulePart = trace.getAllElements().stream()
					.filter(x->x.getValue().getActivityLabel() == eachRulePart.getValue().getActivityLabel())
					.sorted((o1,o2)-> {
						return Integer.compare(o1.getPositionIndex(), o2.getPositionIndex());
					}).collect(Collectors.toList());
					
			
			matchingResourceAcivities.add(activityOccurrencesForRulePart);			
		}*/
		
		
		List<Set<String>> activityResourcePositionAssignment = new ArrayList<>();
		
		for(RResourceComp eachRulePart : rule)
		{
			List<TraceElement<RResourceComp>> activityOccurrencesForRulePart = trace.getAllElements().stream()
					.filter(x->x.getValue().getActivityLabel() == eachRulePart.getActivityLabel())
					.sorted((o1,o2)-> {
						return Integer.compare(o1.getPositionIndex(), o2.getPositionIndex());
					}).collect(Collectors.toList());
					
			
			Set<String> resources = 
					activityOccurrencesForRulePart.stream().map(x->x.getValue().getResourceName()).collect(Collectors.toSet());
			
			activityResourcePositionAssignment.add(resources);
			
		}
		
		//STRICT BOD. When the first task gets assignet a specific resource ALL folloup tasks must get assignet the same resource
		//in a single instance trace
		//this means that the size of each set of resource assignments for this trace must be one
		//and the the merge of all sets must also have a set of one
		//checks each activity indivually
		boolean isMoreThenOnePersonAssignedIndividual = activityResourcePositionAssignment.stream().anyMatch(x->x.size()>1);
		//combination of all activities
		boolean isMoreThenOnePersonAssignedGlobal = activityResourcePositionAssignment.stream().flatMap(x->x.stream()).collect(Collectors.toSet()).size()>1;
		
		boolean weHaveBOD = !isMoreThenOnePersonAssignedGlobal && !isMoreThenOnePersonAssignedIndividual;
		
		
		//_------------------------------------------------------------------------
		
		boolean weHaveSOD = true;
		//STRICT SOD. None of the activites must have the same resources!
		Set<String> knownResourceAssignments = new HashSet<>();
		for(Set<String> eachResourceAssignment : activityResourcePositionAssignment)
		{
			if(knownResourceAssignments.isEmpty())
			{
				knownResourceAssignments.addAll(eachResourceAssignment);
			}
			else
			{
				Set<String> intersection = Sets.intersection(knownResourceAssignments, eachResourceAssignment);
				
				if(!intersection.isEmpty())
				{
					//then we found duplicate resource assignments
					weHaveSOD = false;
					break;
				}
				else
				{
					knownResourceAssignments.addAll(eachResourceAssignment);
				}
			}			
		}
		
		/*for(TraceElement<RResourceComp> eachFirstActivity : matchingResourceAcivities.get(0))
		{
			resourcesThatWasBoundTo.add(eachFirstActivity.getValue().getResourceName());
		}
		
		for(String eachResources : resourcesThatWasBoundTo)
		{
			List<TraceElement<RResourceComp>> firstActivity = matchingResourceAcivities.get(0);

			
		}
		
		for(TraceElement<RResourceComp> eachFirstActivity : matchingResourceAcivities.get(0))
		{
			RResourceComp value = eachFirstActivity.getValue();
			String resource = value.getResourceName();
			int indexInTrace = eachFirstActivity.getPositionIndex();
			
			 //indexOfRuleAcitivites => 0 would be the first activity which we already use to start the loop
			for(int indexOfRuleAcitivites=1;indexOfRuleAcitivites<matchingResourceAcivities.size();indexOfRuleAcitivites++)
			{
				List<TraceElement<RResourceComp>> activities = matchingResourceAcivities.get(indexOfRuleAcitivites);
				
				//find first activity that comes AFTER the last one
				//use indexInTrace for this order realtion
				
				/*Searches the next activity that comes as close after indexintrace as possible
				 * activities.stream().map(s->{
					int relativeIndex = s.getPositionIndex()-indexInTrace;
					return new Object[]{s, relativeIndex};
					})
				.filter(a->(int)a[1]<0) //negative ones are BEFORE the last activity
				.min((a1,a2)->{
					int valuea1 = (int)a1[1];
					int valuea2 = (int)a2[1];
					
					return Integer.compare(valuea1, valuea2); 
				}).map(a->(TraceElement<RResourceComp>)a[0])
				.orElse(null);*/
				
				//all activities that come after 
				/*List<TraceElement<RResourceComp>> activitiesWithIndexAfterTraceAndMatchingResource = activities.stream().filter(x->x.getPositionIndex()>indexInTrace)
				.filter(x->Objects.equal(x.getValue().getResourceName(), resource))
				.sorted((o1,o2)->{
					return Integer.compare(o1.getPositionIndex(), o2.getPositionIndex());
				})
				.collect(Collectors.toList());
				
				if(!activitiesWithIndexAfterTraceAndMatchingResource.isEmpty())
				{
					activities.remove(activitiesWithIndexAfterTraceAndMatchingResource.get(0));
				}			
			}
		}*/
		
		return getResourceMode()==EResourceClasses.SameResource? weHaveBOD : weHaveSOD;
	}

	/**
	 * @return the resourceName
	 */
	public String getResourceName() {
		return resourceName;
	}

	public EResourceClasses getResourceMode() {
		return resourceMode;
	}
}
