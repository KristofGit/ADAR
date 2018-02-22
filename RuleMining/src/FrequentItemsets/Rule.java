package FrequentItemsets;

import java.util.List;

import FrequentItemsets.Trace.TraceElement;
import generic.ArrayHelper;

public class Rule<T> {

	private final RuleState<T> state;
	private final T[] IF;
	private final T THEN;
	
	public Rule(T[] IF, T THEN, RuleState<T> state)
	{
		this.state = state;
		this.IF = IF;
		this.THEN = THEN;
	}
	
	public int getSize()
	{
		return IF.length+1;//+1 for the THEN part
	}
	
	//should return an anomaly score. Which is 0 if it is supported and higher then zero if not
	//the high of the anomaly score depends on the rule and its violation severeness
	public boolean IsSupported(Trace<T> trace)
	{
		T[] completeRule = ArrayHelper.extend(IF, THEN);
		
		return trace.occursInOrder(completeRule);
	}
	
	public double getRuleSupportInTraining()
	{
		return state.getLift();
	}
	
	
}
