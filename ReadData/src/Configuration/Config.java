package Configuration;

public class Config {

	public static final BehaviourSource DataToUse = BehaviourSource.BPIC3;//also holds the path to the logged traces were we read behavior from

	public static final int amountOfEvaluationRuns = 100;
	
	//works well for BPIC
	public static final int MaxRuleSizeGeneral = 3; //maximum length of one of the generated rules, if a rule gets this length it is no longer extended
	public static final int MaxRuleSizeResource = 2; //maximum length of one of the generated rules, if a rule gets this length it is no longer extended

	//BPIC
	public static final double RuleMinSupp = 0.90;
	public static final double RuleMinSuppTemp = 0.8; //BPIC denpending on rule count  between .55 and .65 was used

	public static final double FuzzyTemporalMinSupport = 0.4 ;

	public static final double FuzzyTemporalOverlap = 0.2;//0.2 is 20% overlapp
	
	
	//Works well for HEP	 
	/*public static final int MaxRuleSizeGeneral = 3; //maximum length of one of the generated rules, if a rule gets this length it is no longer extended
	public static final int MaxRuleSizeResource = 2;
	public static final double RuleMinSupp = 0.90;
	public static final double RuleMinSuppTemp = 0.80;

	public static final double FuzzyTemporalMinSupport = 0.4 ;

	public static final double FuzzyTemporalOverlap = 0.2;//0.2 is 20% overlapp*/
	 
	
	/*
	 * 	public static final int MaxRuleSize = 6; //maximum length of one of the generated rules, if a rule gets this length it is no longer extended
	public static final double RuleMinSupp = 0.90;
	public static final double RuleMinSuppTemp = 0.78;

	public static final double FuzzyTemporalMinSupport = 0.4 ;

	public static final double FuzzyTemporalOverlap = 0.2;//0.2 is 20% overlapp
	 */
}
