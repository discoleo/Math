package data;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;

public class ComparatorPolyMSize implements Comparator<Monom> {

	// Power Cut-Off
	protected final int nPOW;
	
	public ComparatorPolyMSize(final int nPow) {
		this.nPOW = nPow;
	}
	
	@Override
	public int compare(final Monom m1, final Monom m2) {
		// Descending Order!
		
		// Special Power
		if(m1.size() == 1 && m1.firstEntry().getValue() >= nPOW) {
			if(m2.size() != 1 || m2.firstEntry().getValue() < nPOW) return -1;
			if(m1.size() > m2.size()) return -1;
			if(m1.size() < m2.size()) return 1;
			return m1.firstKey().compareTo(m2.firstKey());
		}
		if(m2.size() == 1 && m2.firstEntry().getValue() >= nPOW) return 1;
		
		// Monom Size
		if(m1.size() > m2.size()) return -1;
		if(m2.size() > m1.size()) return 1;

		final Iterator<Map.Entry<String, Integer>> it1 = m1.entrySet().iterator();
		final Iterator<Map.Entry<String, Integer>> it2 = m2.entrySet().iterator();
		while(it1.hasNext()) {
			final Map.Entry<String, Integer> entryVar1 = it1.next();
			if( ! it2.hasNext()) {
				return -1;
			}
			final Map.Entry<String, Integer> entryVar2 = it2.next();
			final int cmpVar = entryVar1.getKey().compareTo(entryVar2.getKey());
			if(cmpVar != 0) {
				return cmpVar;
			}
			final int cmpPow = entryVar1.getValue().compareTo(entryVar2.getValue());
			if(cmpPow != 0) {
				return - cmpPow;
			}
		}
		if(it2.hasNext()) {
			return 1;
		}

		return 0;
	}
}
