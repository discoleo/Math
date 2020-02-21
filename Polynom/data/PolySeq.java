/*
 * Polynomial Sub-Sequences
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
package data;

import java.util.Map;
import java.util.TreeMap;

public class PolySeq extends TreeMap<Integer, Polynom> {
	
	protected final String sVarName;

	public PolySeq(final String sVar) {
		this.sVarName = sVar;
	}

	// ++++++++++ MEMBER FUNCTIONS +++++++++++
	
	public Polynom GetNew(final Integer iPow) {
		{
			final Polynom pExist = this.get(iPow);
			if(pExist != null) {
				return pExist;
			}
		}
		final Polynom pExist = new Polynom(sVarName);
		this.put(iPow, pExist);
		return pExist;
	}
	
	public String Print() {
		final StringBuilder sb = new StringBuilder();
		

		for(final Map.Entry<Integer, Polynom> entryP : this.entrySet()) {
			sb.append("(").append(entryP.getValue().toString());
			sb.append(")");
			if(entryP.getKey() != 0) {
				sb.append("*").append(sVarName);
				sb.append("^").append(entryP.getKey());
			}
			sb.append(" +\n");
		}
		if(sb.length() >= 2) {
			sb.delete(sb.length() - 2, sb.length());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
