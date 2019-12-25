package math;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/*
 * The Polynomial Data-Class
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
public class Polynom extends TreeMap<Monom, Double> {
	
	// the name of the base variable, e.g. "x"
	protected final String sRootName;
	
	// ++++ Constructor ++++

	public Polynom(final String sRootName) {
		// folosim Constructorul clasei de baza
		super(new ComparatorPolynom(sRootName));
		this.sRootName = sRootName;
	}
	public Polynom() {
		// folosim Constructorul primar
		this("x");
	}
	public Polynom(final int b0, final String sRootName) {
		this(sRootName);
		// b0 = free term
		if(b0 != 0) {
			this.put(new Monom(), (double) b0);
		}
	}
	public Polynom(final Monom m, final double value, final String sRootName) {
		this(sRootName);
		if(value != 0) {
			this.put(m, value);
		}
	}
	
	// ++++++ Copy Constructor ++++++
	public Polynom(final Polynom p) {
		this(p, p.sRootName);
	}
	public Polynom(final Polynom p, final String sRootName) {
		this(p.sRootName);
		for(final Map.Entry<Monom, Double> entry : p.entrySet()) {
			// assumes Polynom p is well formed
			this.Add(new Monom(entry.getKey()), entry.getValue());
		}
	}
	
	// ++++++ Member Functions ++++++
	
	public String MainRootName() {
		return sRootName;
	}

	public Polynom Add(final int [] iCoeffs) {
		int nPow = 0;
		for(final int iCoeff : iCoeffs) {
			if(iCoeff != 0) {
				final Monom m = new Monom(sRootName, nPow);
				this.Add(m, iCoeff);
			}
			nPow ++;
		}
		return this;
	}

	public void Add(final Monom m, final double dCoeff) {
		if(dCoeff == 0) {
			return;
		}
		final Double dCoeffPrev = this.get(m);
		if(dCoeffPrev == null) {
			this.put(m, dCoeff);
		} else {
			final double dCoeffNew = dCoeffPrev + dCoeff;
			if(dCoeffNew != 0) {
				this.put(m, dCoeffNew);
			} else {
				this.remove(m);
			}
		}
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		for(final Map.Entry<Monom, Double> entry : this.entrySet()) {
			final double dCoeff = entry.getValue();
			final double dCoeffSign;
			if(sb.length() > 0) {
				if(dCoeff < 0) {
					sb.append(" - ");
					dCoeffSign = - dCoeff;
				} else {
					sb.append(" + ");
					dCoeffSign = dCoeff;
				}
			} else {
				// TODO: "-1 * x" as first Term => "-x"
				dCoeffSign = dCoeff;
			}
			final int iCoeffSign = (int) dCoeffSign;
			final boolean isInt = (iCoeffSign == dCoeffSign);
			if(entry.getKey().isEmpty()) {
				if(isInt) {
					sb.append(iCoeffSign);
				} else {
					sb.append(dCoeffSign);
				}
			} else if(dCoeffSign == 1) {
				sb.append(entry.getKey().toString());
			} else {
				if(isInt) {
					sb.append(iCoeffSign);
				} else {
					sb.append(dCoeffSign);
				}
				// TODO: Space vs NO space
				sb.append("*" + entry.getKey().toString());
			}
		}
		return sb.toString();
	}
	
	// +++++ COMPARATOR +++++++
	
	// clasa poate fi clasa de sine statatoare
	public static class ComparatorPolynom implements Comparator<Monom> {
		// TODO: Option: Order Ascending vs Descending;

		// TODO: Option: Comparator based first on dominant variable;
		protected final boolean isPrimary = true;
		protected final String sPrimary;
		
		public ComparatorPolynom(final String sPrimary) {
			this.sPrimary = sPrimary;
		}
		
		@Override
		public int compare(final Monom m1, final Monom m2) {
			// free Term
			if(m1.size() == 0) {
				if(m2.size() == 0) {
					return 0;
				}
				return 1;
			} else if(m2.size() == 0) {
				return -1;
			}
			
			if(isPrimary) {
				final Integer nPow1 = m1.get(sPrimary);
				final Integer nPow2 = m2.get(sPrimary);
				if(nPow1 == null) {
					if(nPow2 != null) {
						return -1;
					}
				} else {
					if(nPow2 == null) {
						return 1;
					} else {
						final int nPowDiff = nPow1 - nPow2;
						if(nPowDiff != 0) {
							return nPowDiff;
						}
					}
				}
			}

			final Iterator<Map.Entry<String, Integer>> it1 = m1.entrySet().iterator();
			final Iterator<Map.Entry<String, Integer>> it2 = m2.entrySet().iterator();
			while(it1.hasNext()) {
				final Map.Entry<String, Integer> entryVar1 = it1.next();
				if( ! it2.hasNext()) {
					return 1;
				}
				final Map.Entry<String, Integer> entryVar2 = it2.next();
				final int cmpVar = entryVar1.getKey().compareTo(entryVar2.getKey());
				if(cmpVar != 0) {
					return cmpVar;
				}
				final int cmpPow = entryVar1.getValue().compareTo(entryVar2.getValue());
				if(cmpPow != 0) {
					return cmpPow;
				}
			}
			if(it2.hasNext()) {
				return -1;
			}

			return 0;
		}
	}
}
