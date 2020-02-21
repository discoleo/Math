/*
 * export Polynomial as R code
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
package data;

import java.util.Vector;


public class PolyResult {
	
	private final Polynom poly;
	private final Polynom pRoot;
	
	private final Vector<Polynom> vCoeff;
	
	private final int nOrder;
	
	public PolyResult(final Polynom poly, final Polynom pRoot, final Vector<Polynom> vCoeff, final int nOrder) {
		this.poly  = poly;
		this.pRoot = pRoot;
		this.vCoeff = vCoeff;
		this.nOrder = nOrder;
	}
	
	// ++++++++++++++ MEMBER FUNCTIONS +++++++++++
	
	public Polynom GetPoly() {
		return poly;
	}
	
	public String toR() {
		final String sVarRoot = pRoot.sRootName;
		
		final StringBuilder sb = new StringBuilder();
		
		// TODO: substantial improvements;
		// - generalize "shifts";
		sb.append("poly.f = function(K, shifts, n=").append(nOrder).append(") {\n");
		sb.append(sVarRoot).append(" = K^(1/n)\n");
		// TODO: shifts
		for(int iShift = 1; iShift <= 3; iShift++) {
			sb.append("s").append(iShift).append(" = ").append("shifts[").append(iShift).append("]\n");
		}
		// Root
		sb.append(poly.sRootName).append(" = ").append(pRoot.toString()).append("\n");
		// Coeffs
		int idCoef = nOrder;
		for(final Polynom pCoeff : vCoeff) {
			sb.append("b").append(idCoef--).append(" = ");
			final String sCoeff = pCoeff.toString();
			if(sCoeff.isEmpty()) {
				sb.append(0).append("\n");
			} else {
				sb.append(sCoeff).append("\n");
			}
		}
		idCoef = nOrder;
		sb.append("b = c(b").append(idCoef--);
		for( ; idCoef >= 0; idCoef--) {
			sb.append(", ").append("b").append(idCoef);
		}
		sb.append(")\n").append("# ERROR\n");
		sb.append("err = ").append(poly.toString()).append("\n");
		// Output
		sb.append("rez = list(\"x\" = x, \"coeffs\"=b, \"err\" = err)\n");
		sb.append("return(rez)\n").append("}\n");
		
		return sb.toString();
	}
}
