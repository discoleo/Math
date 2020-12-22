package math;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import data.Monom;
import data.Pair;
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
	final Vector<Pair<Polynom, String>> vPE2 = new Vector<> ();
	final TreeMap<String, Polynom> mapPR2 = new TreeMap<> ();
	
	
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
		TestE2(20);
	}
	
	// ++++++++++ MEMBER FUNCTIONS ++++++++++++
	
	public void Print(final Polynom pTest) {
		System.out.println(pTest.toString());
	}
	public void Print(final Vector<Polynom> pTest) {
		for(final Polynom p : pTest) {
			System.out.println(p.toString());
		}
	}
	public void Print(final TreeMap<String, Polynom> mapTest) {
		for(final Map.Entry<String, Polynom> entry : mapTest.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue().toString());
		}
	}
	public void PrintP(final Vector<Pair<Polynom, String>> pTest) {
		for(final Pair<Polynom, String> p : pTest) {
			System.out.println(p.key.toString());
		}
	}
	public Polynom GetE2(final int npos) {
		final Polynom p = vPE2.get(npos).key;
		this.Print(p);
		return new Polynom(p);
	}
	public void TestE2(final int iPow) {
		// TODO: Verify if CORRECT!
		this.BuildE(iPow);
		this.Print(mapPR2);
		// this.PrintP(vPE2);
		this.Print(GetE2(24));
	}
	
	// +++ Build the symmetric Polynomials
	// +++ derived from the Elementary Polynomials
	public void BuildE(final int nPow) {
		this.BuildE1(nPow);
		this.BuildE2(nPow);
		this.BuildR1(nPow);
		this.BuildR2(nPow);
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
			vPE2.add(new Pair<>(parser.Parse("x*y+x*z+y*z", sX), "E2"));
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
				vPE2.add(new Pair<>(pE2, "E2c_" + n1 + "_" + n2));
			}
		}
		
		return vPE1; // ??? vPE1 vs vPE2 ???
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
	
	public TreeMap<String, Polynom> BuildR2(final int nPow) {
		final int SKIP_MAX = 6;
		// fast Version for 3 Variables
		if(mapPR2.size() == 0) {
			mapPR2.put("E2", new Polynom(vMER.get(1), 1, sS));
		}
		for(int p1 = 2; p1 <= nPow; p1++) {
			for(int p2 = 1; p2 <= p1; p2++) {
				// x^p1 * y^p2
				// just for Test
				if(p2 >= SKIP_MAX || p1 > SKIP_MAX) { continue; }
				
				final int idP = (p1 - 1)*p1/2 + p2 - 1; // Vector[0]: offset = -1;
				if(mapPR2.size() > idP) { continue; }
				final int diffPow = p1 - p2;
				System.out.println("p1 = " + p1 + "; p2 = " + p2);
				if(diffPow == 0) {
					// sum(x^p1 * y^p1) = (E2)^p1 - (...);
					Polynom pE2Pow = math.Pow(vPE2.get(0).key, p1);
					pE2Pow = EncodeE3V3(pE2Pow);
					pE2Pow = math.Diff(pE2Pow, vPE2.get(this.GetIdE2(p1, p2)).key);
					System.out.println("After E2^n encoding: " + pE2Pow.toString());
					// computes automatically: TODO: misses power;
					pE2Pow = this.EncodeEV3(pE2Pow, p1 + p2 + 3); // p1 + p2 - 1
					pE2Pow = math.Diff(this.E2Pow(p1), pE2Pow);
					pE2Pow = this.ReplaceEV3(pE2Pow, p1 + p2 - 1);
					System.out.println("After encoding: " + pE2Pow.toString());
					mapPR2.put("E2c_" + p1 +"_" + p2, pE2Pow);
				} else {
					// sum(x^diffPow) * sum((x*y)^p2) - (...);
					final String sE2xy = this.GetE2Name(p2, p2);
					Polynom pE2Pow = math.Mult(vPR1.get(diffPow - 1), mapPR2.get(sE2xy));
					System.out.println("After encoding: " + pE2Pow.toString());
					if(diffPow == p2) {
						pE2Pow = math.Diff(pE2Pow, this.E3Pow(p2, 3));
					} else if(diffPow > p2) {
						final int idE1 = diffPow - p2 - 1; // Vector[id]: offset - 1;
						final Polynom pDiff = math.Mult(vPR1.get(idE1), this.E3Pow(p2, 1));
						pE2Pow = math.Diff(pE2Pow, pDiff);
					} else {
						final int idE2 = p2 - diffPow;
						final Polynom pDiff = math.Mult(this.GetE2(idE2, idE2), this.E3Pow(diffPow, 1));
						pE2Pow = math.Diff(pE2Pow, pDiff);
					}
					mapPR2.put("E2c_" + p1 +"_" + p2, pE2Pow);
				}
			}
		}
		
		return mapPR2;
	}
	
	public int GetIdE2(final int p1, final int p2) {
		// based on the complicated layout
		final int iRowD = p1 + p2 - 1;
		final int iRow = iRowD / 2;
		return iRow*(iRow + 1) + p2 - 1 - ((iRowD % 2 == 0) ? iRow : 0);
	}
	
	public String GetE2Name(final int p1, final int p2) {
		return vPE2.get(0).val + (p1 == 1 ? "" : "c_" + p1 + "_" + p2);
	}
	public Polynom GetE2(final int p1, final int p2) {
		final String sE2 = GetE2Name(p1, p2);
		return mapPR2.get(sE2);
	}
	
	public Polynom E2Pow(final int iPow) {
		// E2^iPow
		return new Polynom(new Monom(vPE2.get(0).val, iPow), 1, sS);
	}
	public Polynom E3Pow(final int iPow, final double dCoeff) {
		// E2^iPow
		return new Polynom(new Monom("E3", iPow), dCoeff, sS);
	}
	
	// +++ Encode +++
	
	public Polynom EncodeE1(final Polynom p, final int nPow) {
		final String sReplace = (nPow > 1) ? "E1_" + nPow : sS;
		final Polynom pRE = math.DivExact(p, vPE1.get(nPow - 1), sReplace);
		return pRE;
	}
	
	public Polynom EncodeE2(final Polynom p, final int nPow) {
		// vE2:: 0: E2; 1: x^2y; 2: x^2y^2; 3: x^3y;
		if(nPow < 2) { return null; }
		final Pair<Polynom, String> pairP = vPE2.get(nPow - 2);
		final String sReplace = pairP.val;
		final Polynom pRE = math.DivExact(p, pairP.key, sReplace);
		if(pRE == null) {
			System.out.println("Null: " + sReplace);
		}
		return pRE;
	}
	
	public Polynom EncodeE3V3(final Polynom p) {
		return math.Replace(p, mE3, "E3");
	}
	public Polynom EncodeEV3(final Polynom p, final int nPow) {
		// E3
		Polynom pE = EncodeE3V3(p);
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
		pR = math.Replace(pR, "E2c_4_3", mapPR2.get("E2c_4_3")); // hack ???
		pR = math.Replace(pR, "E2c_3_3", mapPR2.get("E2c_3_3")); // hack ???
		pR = math.Replace(pR, "E2c_3_2", mapPR2.get("E2c_3_2")); // hack ???
		pR = math.Replace(pR, "E2c_3_1", mapPR2.get("E2c_3_1")); // hack ???
		pR = math.Replace(pR, "E2c_2_2", parser.Parse("E2^2 - 2*E3*" + sS, sS));
		pR = math.Replace(pR, "E2c_2_1", parser.Parse(sS + "*E2 - 3*E3", sS));
		
		return pR;
	}
}
