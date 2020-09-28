package math;

import java.util.Vector;

import data.Monom;
import data.Polynom;
import io.Parser;

public class ElementaryPoly {
	final MathTools math;
	final Parser parser;
	
	final String sX = "x";
	final String sS = "S"; // E1
	
	// E3
	final Monom mE3;
	// E1
	final Vector<Polynom> vPE1;
	// E2
	final Vector<Polynom> vPE2;
	
	
	public ElementaryPoly(final Parser parser, final MathTools math) {
		this.parser = parser;
		this.math = math;
		// E3
		mE3 = new Monom("x", 1).Add("y", 1).Add("z", 1);
		// E1
		vPE1 = new Vector<> ();
		// E2
		vPE2 = new Vector<> ();
	}
	
	// +++ Build the symmetric Polynomials
	// +++ derived from the Elementary Polynomials
	public void BuildE(final int nPow) {
		this.BuildE1(nPow);
		this.BuildE2(nPow);
	}
	public Vector<Polynom> BuildE1(final int nPow) {
		for(int n= vPE1.size() + 1; n <= nPow; n++) {
			final String sP = "x^" + n + "+y^" + n + "+z^" + n;
			final Polynom pE1 = parser.Parse(sP, sX);
			vPE1.add(pE1);
		}
		
		return vPE1;
	}
	
	public Vector<Polynom> BuildE2(final int nPowTotal) {
		if(vPE2.size() == 0) {
			vPE2.add(parser.Parse("x*y+x*z+y*z", sX));
		}
		// TODO: correct / optimal sequence ?
		for(int nTotal = vPE2.size() + 2; nTotal <= nPowTotal; nTotal++) {
			for(int n2 = 1; n2 <= nTotal / 2 ; n2++) {
				final int n1 = nTotal - n2;
				final String sP;
				if(n1 == n2) {
					sP = "x^" + n1 + "*y^" + n1 +
						"+x^" + n1 + "*z^" + n1 +
						"+y^" + n1 + "*z^" + n1;
				} else {
					sP = "x^" + n1 + "*y^" + n2 +
							"+x^" + n1 + "*z^" + n2 +
							"+y^" + n1 + "*x^" + n2 +
							"+y^" + n1 + "*z^" + n2 +
							"+z^" + n1 + "*x^" + n2 +
							"+z^" + n1 + "*y^" + n2;
				}
				final Polynom pE2 = parser.Parse(sP, sX);
				vPE2.add(pE2);
			}
		}
		
		return vPE1;
	}
	
	public Polynom EncodeE1(final Polynom p, final int nPow) {
		final String sReplace = (nPow > 1) ? "E1_" + nPow : sS;
		final Polynom pRE = math.DivExact(p, vPE1.get(nPow - 1), sReplace);
		return pRE;
	}
	
	public Polynom EncodeE2(final Polynom p, final int nPow) {
		// vE2: 0: E2; 1: x^2y; 2: x^2y^2; 3: x^3y;
		if(nPow < 2) { return null; }
		final String sReplace = (nPow > 2) ? "E2c" + (nPow - 2) : "E2";
		final Polynom pRE = math.DivExact(p, vPE2.get(nPow - 2), sReplace);
		return pRE;
	}
	
	public Polynom EncodeEV3(final Polynom p, final int nPow) {
		// E3
		Polynom pE = math.Replace(p, mE3, "E3");
		// E1 & E2:
		Polynom pRE = null;
		// E1^4
		for(int n = nPow; n >= 1; n--) {
			// E1^n
			pRE = EncodeE1(pE, n);
			if(pRE != null) {
				pE = pRE;
			}
			// x^k*y^j, with (k+j) = n; // TODO: not all!
			pRE = EncodeE2(pE, n);
			if(pRE != null) {
				pE = pRE;
			}
		}
		
		return pE;
	}
	public Polynom ReplaceEV3(final Polynom p) {
		// TODO: derive the decompositions;
		Polynom pR = math.Replace(p, "E1_2", parser.Parse(sS + "^2 - 2*E2", sS));
		pR = math.Replace(pR, "E1_3", parser.Parse(sS + "^3 - 3*E2*" + sS + "+3*E3", sS));
		pR = math.Replace(pR, "E1_4", parser.Parse(sS + "^4 - 4*E2*" + sS + "^2 + 4*E3*" + sS + " + 2*E2^2", sS));
		pR = math.Replace(pR, "E1_5", parser.Parse(sS + "^5 - 5*E2*" + sS + "^3 + 5*E3*" + sS + "^2 +"
				+ "5*E2^2*" + sS + "- 5*E3*E2", sS));
		// TODO: E1_6!
		// E2...
		pR = math.Replace(pR, "E2c1", parser.Parse(sS + "*E2 - 3*E3", sS));
		pR = math.Replace(pR, "E2c2", parser.Parse("E2^2 - 2*E3*" + sS, sS));
		
		return pR;
	}
}
