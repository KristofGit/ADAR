package dataStructures;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UniqueObjectIdentifier {

	private final static AtomicInteger idCounter = new AtomicInteger();
	private final int uniqueId = idCounter.getAndIncrement();

	protected int getUniqueObjectID()
	{
		return uniqueId;
	}

	@Override
	public boolean equals(Object obj) {

		boolean result = false;
		
		if(obj != null && obj instanceof UniqueObjectIdentifier)
		{
			UniqueObjectIdentifier other = (UniqueObjectIdentifier)obj;
			
			result = other.getUniqueObjectID() == this.getUniqueObjectID();
		}
				
		return result;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return uniqueId;
	}
	

	
}