package math;

import java.util.Vector;

import data.Monom;
import data.Polynom;
import io.Parser;

public class ElementaryPoly {
	final MathTools math;
	final Parser parser;
	
	final String sX = "x";
	final String sS = "S"; // E1 Variable Name
	final Polynom pS; // E1 Replacement
	
	// E internal Replacement
	final Vector<Monom> vMER = new Vector<> ();
	// E3
	final Monom mE3;
	// E1
	final Vector<Polynom> vPE1 = new Vector<> ();
	final Vector<Polynom> vPR1 = new Vector<> ();
	// E2
	final Vector<Polynom> vPE2 = new Vector<> ();
	
	
	public ElementaryPoly(final Parser parser, final MathTools math) {
		this.parser = parser;
		this.math = math;
		// S
		pS = parser.Parse(sS, sS);
		// E internal Replacement
		vMER.add(new Monom(sS, 1));
		vMER.add(new Monom("E2", 1));
		vMER.add(new Monom("E3", 1));
		// E3
		mE3 = new Monom("x", 1).Add("y", 1).Add("z", 1);
		//
		BuildR1(3);
	}
	
	// ++++++++++ MEMBER FUNCTIONS ++++++++++++
	
	public void Print(final Vector<Polynom> pTest) {
		for(final Polynom p : pTest) {
			System.out.println(p.toString());
		}
	}
	
	// +++ Build the symmetric Polynomials
	// +++ derived from the Elementary Polynomials
	public void BuildE(final int nPow) {
		this.BuildE1(nPow);
		this.BuildE2(nPow);
		this.BuildR1(nPow);
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
	
	// +++ Build Replacements +++

	public Vector<Polynom> BuildR1(final int nPow) {
		// optimization for the "Bases"
		if(vPR1.size() < 3) {
			if(vPR1.size() == 0) {
				vPR1.add(pS);
			}
			if(vPR1.size() == 1) {
				vPR1.add(parser.Parse(pS + "^2 - 2*E2", sS));
			}
			if(vPR1.size() == 2) {
				vPR1.add(parser.Parse(pS + "^3 - 3*E2*" + sS + "+3*E3", sS));
			}
		}
		// remaining cases
		for(int n = vPR1.size(); n < nPow; n++) {
			Polynom pR1 = math.Mult(vPR1.get(n - 1), vMER.get(0));
			pR1 = math.Diff(pR1, math.Mult(vPR1.get(n - 2), vMER.get(1)));
			pR1 = math.Add(pR1, math.Mult(vPR1.get(n - 3), vMER.get(2)));
			vPR1.add(pR1);
		}
		
		return vPR1;
	}
	
	// +++ Encode +++
	
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
	public Polynom ReplaceEV3(final Polynom p, final int nPow) {
		Polynom pR = math.Replace(p, "E1_2", vPR1.get(1));
		for(int iPow = 2; iPow < nPow; iPow++) {
			pR = math.Replace(pR, "E1_" + (iPow+1), vPR1.get(iPow));
		}
		// E2...
		pR = math.Replace(pR, "E2c1", parser.Parse(sS + "*E2 - 3*E3", sS));
		pR = math.Replace(pR, "E2c2", parser.Parse("E2^2 - 2*E3*" + sS, sS));
		
		return pR;
	}
}
