package GenerateAnomalies;

import java.util.ArrayList;
import java.util.List;

import LogTraceOB.LogTrace;
import MutatorsReadData.ControlFlowMutatorOne;
import MutatorsReadData.ControlFlowMutatorTwo;
import MutatorsReadData.IMutate;
import MutatorsReadData.ResourceMutatorOneBOD;
import MutatorsReadData.ResourceMutatorTwoSOD;
import MutatorsReadData.TemporalMutator;
import generic.RandomHelper;

public class AnomalyGen {

	public static List<LogTrace> turnIntoAnomaly(List<LogTrace> originalTraces)
	{
		List<LogTrace> mutatedTraces = new ArrayList<>();
		

		IMutate[] allKnownMutators = {
				new ControlFlowMutatorOne(), 
				new ControlFlowMutatorTwo(),
				new ResourceMutatorOneBOD(), 
				//new ResourceMutatorTwoSOD(),
				new TemporalMutator()
				};
		
		for(LogTrace eachTrace : originalTraces)
		{
			boolean mutationWasPossible;
			
			LogTrace mutatedTrace;
			do
			{
				mutatedTrace = eachTrace;
				mutationWasPossible = true;
				
				AnomalyStrengh randomStrength = RandomHelper.randomEnum(AnomalyStrengh.class);

				int amountOfMutatorsToApply = randomStrength.getAmountOfAnomalies();
				
				IMutate[] mutatorsToApply = RandomHelper.randomElements(allKnownMutators, amountOfMutatorsToApply);
				
				int howFrequentlyAreMutatorsApplied = RandomHelper.randInt(randomStrength.getApplicationTimesMin(), randomStrength.getApplicationTimesMax());
				
				for(int i=0;i<howFrequentlyAreMutatorsApplied;i++)
				{
					for(IMutate eachMutator : mutatorsToApply)
					{
						System.out.println("Mutator:"+eachMutator.getClass());
						mutatedTrace = eachMutator.mutate(mutatedTrace);
						
						if(mutatedTrace == null)
						{
							//we get null only if the mutation was not applicable, e.g., 
							//we we want to create a sod anomaly but all activities in the process already are assigned to the same 
							//resource
							mutationWasPossible = false;
							System.out.println("restarting mutation");
							break;
						}
					}
					
					if(!mutationWasPossible)
					{
						break;
					}
				}					
			}
			while(!mutationWasPossible);
			
			mutatedTraces.add(mutatedTrace);
		}
		
		
		return mutatedTraces;
	}
}
