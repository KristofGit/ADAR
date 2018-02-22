package MutatorsReadData;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.swing.plaf.basic.BasicBorders.RadioButtonBorder;

import FrequentItemsets.Trace;
import LogTraceOB.LogTrace;
import LogTraceOB.LogTraceElement;
import RuleComponents.RControlFlowComp;
import generic.CollectionHelper;
import generic.RandomHelper;

public class ControlFlowMutatorOne implements IMutate {
	
	public static LogTrace alterActivityOrder(LogTrace trace)
	{
		List<LogTraceElement> traceElements = new ArrayList<>(trace.getTraceElements());
			
		int randomListIndexSelect,randomListIndexInject;
		
		do
		{
			randomListIndexSelect = RandomHelper.getRandomListIndex(traceElements);
			randomListIndexInject  = RandomHelper.getRandomListIndex(traceElements);
		}
		while(randomListIndexInject == randomListIndexSelect);
		
		Collections.swap(traceElements, randomListIndexSelect, randomListIndexInject);
		
		//switch first last, etc. to ensure that this are not normal orders which could also be found in the normal traces

		/*for(int i=0;i<traceElements.size();i++)
		{
			int swapLeft = i;
			int swapRight = traceElements.size()-i-1;
			
			if(swapLeft == swapRight || swapRight<swapLeft)
			{
				return null;  //because we failed
			}
			
			LogTraceElement activityToSwitchLeft  = traceElements.get(i);
			LogTraceElement activityToSwitchRight  = traceElements.get(i);

			if(activityToSwitchLeft.mutated || activityToSwitchRight.mutated)
			{
				continue;
			}
			
			activityToSwitchLeft.mutated = true;
			activityToSwitchRight.mutated = true;
			
			Collections.swap(traceElements, swapLeft, swapRight);
			
			break;//because we have sucessfully mutated it
		}*/
				
		return new LogTrace(traceElements);
	}
	
	public LogTrace mutate(LogTrace trace) {
		return alterActivityOrder(trace);
	}

}
