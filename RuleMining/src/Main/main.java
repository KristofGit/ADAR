package Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import FrequentItemsets.MineAprioriRules;
import generic.ArrayHelper;

public class main {

	public static void main(String[] args) {
		MineAprioriRules ruleMining = new MineAprioriRules(.1);
		ruleMining.conductTestMinig();
		
		List<Double> values = Arrays.asList(5.,6.,7.,8.,9.);
		
		Integer[] array = new Integer[]{1,2,3};
		
		array = ArrayHelper.extend(array, 4);
		array = ArrayHelper.extend(array, 5);
		
		array = ArrayHelper.shrink(array);
		array = ArrayHelper.shrink(array);


	}

}
