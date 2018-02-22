package generic;

public class EnumHelper {

	public static <T extends Enum<?>> T first(Class<T> clazz){
        return clazz.getEnumConstants()[0];
    }
	
	public static <T extends Enum<?>> T last(Class<T> clazz){
        return clazz.getEnumConstants()[clazz.getEnumConstants().length-1];
    }
}
