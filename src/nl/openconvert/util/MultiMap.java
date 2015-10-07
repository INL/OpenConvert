package nl.openconvert.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MultiMap<K,V> extends HashMap<K,Set<V>>
{
	String multiValueSeparator ="|";
	

	public void putValue(K name, V value)
	{
		Set<V> v = get(name);
		if (v == null) 
			this.put(name, v = new HashSet<V>());
		v.add(value);
	}
}
