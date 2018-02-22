package FrequentItemsets;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import FrequentItemsets.Trace.TraceElement;
import generic.ArrayHelper;
import generic.CollectionHelper;

public class RuleState<T> {

	private double support;
	private double confidence;
	private double lift;
	
	public RuleState(List<Trace<T>> traces, final TraceElement<T>[] itemset)
	{
		SupConf(traces, itemset);
	}
	
	private void SupConf(List<Trace<T>> traces, final TraceElement<T>[] itemset)
	{
		//SUP holds how frequently the itemset occurs in the traces in relation to all traces
		//so we need to count how frequently the itemset is fiven in all traces
		
		AtomicInteger amountOfFoundIFTHEN = new AtomicInteger(0);
		int amountOfTraces = traces.size();
		
		T[] plaingRule = (T[]) Arrays.stream(itemset).map(x->x.getValue()).toArray();
		
		traces.stream().parallel().forEach(x->{
			boolean found = x.occursInOrder(plaingRule);
			if(found)
			{
				amountOfFoundIFTHEN.incrementAndGet();
			}
		});
		
		//CONF holds how frequently the rule is supported (IF and THEN) in comparison how frequently
		//the IF occurs
		AtomicInteger amountOfFoundIF = new AtomicInteger(0);
		traces.stream().parallel().forEach(x->{
			boolean found = x.occursInOrder(ArrayHelper.shrink(plaingRule));
			if(found)
			{
				amountOfFoundIF.incrementAndGet();
			}
		});
		
		AtomicInteger amountOfFoundTHEN = new AtomicInteger(0);
		traces.stream().parallel().forEach(x->{
			boolean found = x.occursInOrder(ArrayHelper.toArray(ArrayHelper.getLastElement(plaingRule)));
			if(found)
			{
				amountOfFoundTHEN.incrementAndGet();
			}
		});
		
				// sup(X & Y) / sup (X)
		confidence = (amountOfFoundIFTHEN.doubleValue()/amountOfTraces)/(amountOfFoundIF.doubleValue()/amountOfTraces);
		

		 // sup(x)/ |T|
		//support = amountOfFoundIF.doubleValue()/amountOfTraces;
		support = amountOfFoundIFTHEN.doubleValue()/amountOfTraces;

		lift = (amountOfFoundIFTHEN.doubleValue()/amountOfTraces)/(((amountOfFoundIF.doubleValue()/amountOfTraces))*(amountOfFoundTHEN.doubleValue()/amountOfTraces));
	}

	public double getConfidence() {
		return confidence;
	}

	public double getSupport() {
		return support;
	}

	public double getLift() {
		return lift;
	}

}
