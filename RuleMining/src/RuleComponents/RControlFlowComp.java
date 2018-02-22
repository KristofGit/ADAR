package RuleComponents;

import java.util.List;

import Configuration.BehaviourSource;
import Configuration.Config;
import LogTraceOB.LogTrace;
import Reading.ReadFromLogs;

public class RControlFlowComp{

	private final String activityLabel;
	
	public RControlFlowComp(String activityLabel)
	{
		if(activityLabel == null)
		{
			activityLabel = "";
		}
		
		this.activityLabel = activityLabel;
	}
	
	@Override
	public String toString(){ 
		  return getActivityLabel()+"|";  
	} 
	
	 @Override 
	 public boolean equals(Object aThat) {
	    if ( this == aThat ) return true;

	    if ( aThat == null ||  !(aThat instanceof RControlFlowComp) ) return false;

	    RControlFlowComp that = (RControlFlowComp)aThat;

	    return getActivityLabel().equals(that.getActivityLabel());
	 }

	public String getActivityLabel() {
		return activityLabel;
	}
}
