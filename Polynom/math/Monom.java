package math;
import java.util.Map;
import java.util.TreeMap;

/*
 * The Monom Data-Class
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
public class Monom extends TreeMap<String, Integer> {
	// clasa Monom e la baza un TreeMap / dictionar
	// Nume variabila => Puterea variabilei
	
	// WARNING:
	// free term & coefficient are NOT stored in Monom!
	
	private int iPTotala = 0; // puterea totala
	
	public Monom(final String sVar, final Integer iPutere) {
		if(iPutere != 0) {
			this.put(sVar, iPutere);
		}
	}
	
	public Monom() {
	}

	// Copy Constructor
	public Monom(final Monom m) {
		this.putAll(m);
	}
	
	// ++++++++++++++++

	public Monom Add(final String sVar, final int iPow) {
		if(iPow != 0) {
			this.put(sVar, iPow);
		}
		return this;
	}
	public void Put(final String sVar, final Integer iPutere) {
		// simple Put
		iPTotala -= super.put(sVar, iPutere);
		iPTotala += iPutere;
	}

	@Override
	public Integer put(final String sVar, final Integer iPutere) {
		// TODO: rename/move to Add()
		iPTotala += iPutere;
		// cautam puterea anterioara
		// putem folosi si: this.containsKey(sVar);
		// varianta de mai jos e mai eficienta
		final Integer iPVar = this.get(sVar);
		if(iPVar == null) {
			// NU exista inca aceasta variabila
			
			// apelam functia de baza cu: super.put()
			return super.put(sVar, iPutere);
		} else {
			// apelam functia de baza cu: super.put()
			final int iPVarNew = iPVar + iPutere;
			if(iPVarNew != 0) {
				return super.put(sVar, iPVar + iPutere);
			} else {
				this.remove(sVar);
				return 0;
			}
		}
	}
	
	@Override
	public Integer remove(final Object sVar) {
		final Integer iPow = super.remove(sVar);
		if(iPow == null) {
			return null;
		}
		iPTotala -= iPow;
		return iPow;
	}
	
	public int GetPower() {
		// returneaza puterea totala
		// poate fi util la sortare
		return iPTotala;
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for(final Map.Entry<String, Integer> entry : this.entrySet()) {
			if(sb.length() > 0) {
				sb.append("*");
			}
			sb.append(entry.getKey());
			if(entry.getValue() != 1) {
				// Puterea != 1
				sb.append('^').append(entry.getValue());
			}
		}
		return sb.toString();
	}
}
