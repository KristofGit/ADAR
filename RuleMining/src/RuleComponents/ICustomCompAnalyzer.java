package RuleComponents;

import java.util.List;

import FrequentItemsets.Trace;
import FrequentItemsets.Trace.TraceElement;

public interface ICustomCompAnalyzer<T> {

	//if special comparison rules were the normal equals is insufficient must be applied
	//used for resource binding of duty and separation of duty aspects
	public boolean isSupported(Trace<T> trace, List<T> rule);
}
