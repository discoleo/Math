package gui;

import data.Monom;
import data.Pair;
import data.PolyResult;
import data.Polynom;
import io.Parser;
import math.MathTools;
import math.PolyFactory;


public class TestDerived {

	protected final Parser parser;
	protected final MathTools math;
	// PolyFactory is an enhanced version of BaseFactory
	// many functions may be available in BaseFactory
	protected final PolyFactory polyFact;
	
	protected IDisplay display;
	
	public TestDerived(final Parser parser, final MathTools math, final PolyFactory polyFact,
			final IDisplay display) {
		this.parser = parser;
		this.math = math;
		this.polyFact = polyFact;
		//
		this.display = display;
	}
	
	// ++++++++++++++++++++++++
	
	public void Test() {
		System.out.println("Testing Derived Polynoms:");
		
		// some P6
		this.P6();
		
		// +++ calculations for a P2 System
		// this.P2S();
		// this.P2X4Y_Classic();
		// this.P2X4_Classic();
		// this.P2X4_Shifted();
		// this.P3X3();
		// this.P2X5_Classic();
		// this.E3();
		// this.P6FromP3();
		this.S3X2(); // TODO: factorise polynomial
		this.S3X2Ext();
		
		// ++++ other Examples ++++
		// Examples of Derived Polynomials:
		final Polynom pBase = parser.Parse("x^5 - x - 1", "x");
		// Root = r^3 + r^2 + r
		final Polynom pDerived = parser.Parse("-4 - 14*x - 21*x^2 - 11*x^3 + x^5", "x");
		// descending Coeffs
		final Polynom pDiv = polyFact.Derived(pBase, new int [] {1, 1, 1, 0}, pDerived);
		
		display.Display(pDiv);
		
		// Examples 2 & 3:
		// this.Example2();
		
		// Examples 4 & 5:
		// this.Example4();
	}
	
	public void Example2() {
		// Example 2:
		display.Display("\nP5: Ex 2");
		final int iPow = 5;
		final int iRootVal = 3;
		final Polynom pP5Base = parser.Parse("x^" + iPow + " - " + iRootVal, "x");
		final Polynom pRoot = parser.Parse("k^3 + k^2 + k", "k");
		final Polynom pP5R = math.Replace(polyFact.Create(pRoot, iPow).GetPoly(), "k", iPow, iRootVal);
		final Polynom pP5Div = polyFact.Derived(pP5Base, pRoot, pP5R);
		display.Display(pP5R.toString());
		display.Display(pP5Div);

		// Example 3:
		display.Display("\nP5: Ex 3");
		final Polynom pRoot2 = parser.Parse("k^3 - k^2 + k", "k");
		final Polynom pP5R2 = math.Replace(polyFact.Create(pRoot2, iPow).GetPoly(), "k", iPow, iRootVal);
		final Polynom pP5Div2 = polyFact.Derived(pP5Base, pRoot2, pP5R2);
		display.Display(pP5R2.toString());
		display.Display(pP5Div2);
	}
	
	public void Example4() {
		// Example 4:
		display.Display("\nP5: Ex 4");
		final Polynom pBase4 = parser.Parse("-1 - 5*x - 9*x^2 - 2*x^3 + x^5", "x");
		final Polynom pRoot_2 = parser.Parse("k^2 + k", "k");
		final Polynom pRoot_2m = parser.Parse("k^2 - k", "k");
		final Polynom pDerived4 = parser.Parse("-4 - 32*x - 77*x^2 - 35*x^3 - 4*x^4 + x^5", "x");
		final Polynom pP5Div4 = polyFact.Derived(pBase4, pRoot_2, pDerived4);
		display.Display(pDerived4.toString());
		display.Display(pP5Div4);
		
		// Example 5:
		display.Display("\nP5: Ex 5 / cos()");
		final Polynom pBaseCos = parser.Parse("1 + 3*x - 3*x^2 - 4*x^3 + x^4 + x^5", "x");
		final Polynom pDerivedCos2 = parser.Parse("1 + 3*x - 25*x^2 + 29*x^3 - 10*x^4 + x^5", "x");
		final Polynom pP5DivCos = polyFact.Derived(pBaseCos, pRoot_2m, pDerivedCos2);
		display.Display(pDerivedCos2.toString());
		display.Display(pP5DivCos);
	}
	
	public void P10() {
		display.Display("P10");
		
		final Polynom p1 = parser.Parse("x^2 - r1*x + 1", "x");
		final Polynom p2 = parser.Parse("x^2 - r2*x + 1", "x");
		final Polynom p3 = parser.Parse("x^2 - r3*x + 1", "x");
		final Polynom p4 = parser.Parse("x^2 - r4*x + 1", "x");
		final Polynom p5 = parser.Parse("x^2 - r5*x + 1", "x");
		
		Polynom pR = math.Mult(p1, p2);
		pR = math.Mult(pR, p3);
		pR = math.Mult(pR, p4);
		pR = math.Mult(pR, p5);
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	public void P6() {
		display.Display("\nP6");
		
		final Polynom p1 = parser.Parse("x^2 - n*r1*x + r2*r3", "x");
		final Polynom p2 = parser.Parse("x^2 - n*r2*x + r1*r3", "x");
		final Polynom p3 = parser.Parse("x^2 - n*r3*x + r1*r2", "x");
		
		Polynom pR = math.Mult(p1, p2);
		pR = math.Mult(pR, p3);
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	public void P2S() {
		display.Display("\nP2 System");
		final String sX = "Z";

		final Polynom pDiv = parser.Parse("2*Z - 4*s", sX);
		final Polynom pDivsq = math.Mult(pDiv, pDiv);
		final Polynom pxy = parser.Parse("Z^3 - 4*s*Z^2 + 6*s^2*Z - 4*s^3 - b1", sX);
		
		Polynom p1 = parser.Parse(
				"Z^4 - 4*s*Z^3 + 6*s^2*Z^2 - 4*s^3*Z + b1*Z + 2*s^4 - 2*R", sX);
		
		// - 4*(Z^2 - 3*s*Z + 3*s^2)*x*y
		Polynom p3xy = parser.Parse("- 4*Z^2 + 12*s*Z - 12*s^2", sX);
		p3xy = math.Mult(p3xy, pDiv);
		p3xy = math.Mult(p3xy, pxy);
		
		p1 = math.Mult(p1, pDivsq);
		p1 = math.Add(p1, p3xy);
		
		// 2*(xy)^2
		Polynom pxysq = math.Mult(pxy, pxy);
		pxysq = math.Mult(pxysq, 2);
		p1 = math.Add(p1, pxysq);
		p1 = math.Mult(p1, 1, -2);
		
		display.Display(polyFact.ToSeq(p1, sX));
	}

	public void P2X4Y_Classic() {
		display.Display("\nP2 System: X^4*Y Classic");
		final String sX = "x";
		
		final Polynom pBase = parser.Parse("R - b1*x", sX);
		final Polynom pB4 = math.Pow(pBase, 4);
		
		Polynom pClassic = parser.Parse("R*x^15 + b1^2*x^12 - b1*R*x^11", sX);
		pClassic = math.Add(pClassic, math.Mult(pB4, -1));
		
		final Polynom pBaseX = parser.Parse("x^5 + b1*x - R", "x");
		Polynom pDerived = parser.Parse(
				"R*x^10 + b1^2*x^7 - 2*R*b1*x^6 + R^2*x^5 - b1^3*x^3 + 3*R*b1^2*x^2 - 3*R^2*b1*x + R^3", "x");
		
		display.Display(polyFact.ToSeq(pClassic, sX));
		display.Display(polyFact.ToSeq(math.Mult(pDerived, pBaseX), "x"));
	}

	public void P2X4_Classic() {
		display.Display("\nP2 System: X^4 Classic");
		final String sX = "x";
		
		final Polynom pBase = parser.Parse("R - x^4", sX);
		final Polynom pB4 = math.Pow(pBase, 4);
		
		Polynom pClassic = parser.Parse("b1^5*x - R*b1^4", sX);
		pClassic = math.Add(pB4, pClassic);
		
		final Polynom pBaseX = parser.Parse("x^4 + b1*x - R", "x");
		Polynom pDerived = parser.Parse(
				"x^12 - b1*x^9 - 3*R*x^8 + b1^2*x^6 + 2*R*b1*x^5 + 3*R^2*x^4 - b1^3*x^3 - R*b1^2*x^2 - R^2*b[1]*x + b1^4 - R^3", "x");
		
		display.Display(polyFact.ToSeq(pClassic, sX));
		display.Display(polyFact.ToSeq(math.Mult(pDerived, pBaseX), "x"));
	}

	public void P2X5_Classic() {
		display.Display("\nP2 System: X^5 Classic");
		final String sX = "x";
		
		final Polynom pBase = parser.Parse("x^5 - R", sX);
		final Polynom pB = math.Pow(pBase, 5);
		
		Polynom pClassic = parser.Parse("-b1^6*x + R*b1^5", sX);
		pClassic = math.Add(pB, pClassic);
		
		final Polynom pBaseX = parser.Parse("x^5 + b1*x - R", "x");
		Polynom pDerived = parser.Parse(
				"x^20 - b1*x^16 - 4*R*x^15 + b1^2*x^12 + 3*R*b1*x^11 + 6*R^2*x^10 - b1^3*x^8"
				+ "- 2*R*b1^2*x^7 - 3*R^2*b1*x^6 - 4*R^3*x^5 + b1^4*x^4 + R*b1^3*x^3"
				+ "+ R^2*b1^2*x^2 + R^3*b1*x + R^4 - b1^5", "x");
		
		display.Display(polyFact.ToSeq(pClassic, sX));
		display.Display(polyFact.ToSeq(math.Mult(pDerived, pBaseX), "x"));
	}

	public void P2X4_Shifted() {
		display.Display("\nP2 System: X^4 Shifted");
		final String sX = "x";
		
		final Polynom pBase = parser.Parse("x^4 + b1*s - R", sX);
		final Polynom pB4 = math.Pow(pBase, 4);
		
		Polynom pClassic = parser.Parse("b1^5*x + b1^5*s - b1^4*R", sX);
		pClassic = math.Add(pB4, pClassic);
		
		final Polynom pBaseX = parser.Parse("x^4 + b1*x + b1*s - R", "x");
		Polynom pDerived = parser.Parse(
				"x^12 - b1*x^9 - 3*R*x^8 + 3*b1*s*x^8 + b1^2*x^6 + 2*R*b1*x^5 - 2*b1^2*s*x^5"
				+ "- 6*R*b1*s*x^4 + 3*R^2*x^4 + 3*b1^2*s^2*x^4 - b1^3*x^3"
				+ "- R*b1^2*x^2 + b1^3*s*x^2"
				+ "+ 2*R*b1^2*s*x - R^2*b1*x - b1^3*s^2*x"
				+ "- 3*R*b1^2*s^2 + 3*R^2*b1*s - R^3 + b1^3*s^3 + b1^4", "x");
		
		display.Display(polyFact.ToSeq(pClassic, sX));
		display.Display(polyFact.ToSeq(math.Mult(pDerived, pBaseX), "x"));
	}

	public void P3X3() {
		display.Display("\nP3 System: X^3 Base");
		final String sX = "x";
		
		final Polynom pBase = parser.Parse("x^3 - R", sX);
		final Polynom pB3 = math.Pow(pBase, 3);
		
		Polynom pClassic = parser.Parse("R*b1^3", sX);
		pClassic = math.Add(pB3, pClassic);
		pClassic = math.Pow(pClassic, 3);
		
		pClassic = math.Add(pClassic, parser.Parse("b1^13*x - R*b1^12", sX));
		
		final Polynom pBaseX = parser.Parse("x^3 + b1*x - R", "x");
		Polynom pDerived = parser.Parse(
				"x", sX);
		
		display.Display(polyFact.ToSeq(pClassic, sX));
		display.Display(polyFact.ToSeq(math.Mult(pDerived, pBaseX), "x"));
		
		// Elementary P
		final Polynom pPxy = parser.Parse("x^2 + y^2 + x*y", sX);
		final Polynom pPxz = parser.Parse("x^2 + z^2 + x*z", sX);
		final Polynom pPyz = parser.Parse("y^2 + z^2 + y*z", sX);
		Polynom pM = math.Mult(pPxy, pPxz);
		pM = math.Mult(pM, pPyz);
		display.Display(pM);
	}
	
	public void P6FromP3() {
		display.Display("\nP6: Roots");
		final String sX = "x";
		
		final Polynom pE1 = parser.Parse("S1 + S2 + S3", sX);
		final Polynom pE12 = parser.Parse("x^2 + y^2 + z^2", sX);
		final Polynom pE3 = parser.Parse("S1*S2*S3", sX);
		final Monom mE3 = pE3.firstKey();
		
		// E3
		final Polynom pPBase = parser.Parse("S^2 - b2*S - b1", sX);
		Polynom pP = math.Replace(pPBase, "S", 1, "S1");
		pP = math.Mult(pP, math.Replace(pPBase, "S", 1, "S2"));
		pP = math.Mult(pP, math.Replace(pPBase, "S", 1, "S3"));
		
		pP = math.Replace(pP, mE3, "E3");
		
		display.Display(pP);
		
		// E5
		pP = P6E5FromP3();
		pP = math.Replace(pP, mE3, "E3");
		
		display.Display(pP);
	}
	
	public Polynom P6E5FromP3() {
		final String sS = "S";
		final int len = 3;
		
		final Polynom pP = parser.Parse("S^2 - b2*S - b1", sS);
		final Polynom [] ppP = new Polynom [len];
		for(int i=0; i < len; i++) {
			ppP[i] = math.Replace(pP, sS, 1, sS + (i + 1));
		}

		Polynom pE5 = new Polynom(sS);
		for(int id1=0; id1 < len; id1++) {
			Polynom pTemp = new Polynom(1, sS);
			for(int id2=0; id2 < len; id2++) {
				if(id2 == id1) {
					pTemp = math.Mult(pTemp, new Monom(sS + (id1 + 1), 1));
				} else {
					pTemp = math.Mult(pTemp, ppP[id2]);
				}
			}
			pE5 = math.Add(pE5, pTemp);
		}
		
		return pE5;
	}
	
	public void E3() {
		display.Display("\nE: ^3");
		final String sX = "x";
		
		final Polynom pE1 = parser.Parse("x + y + z", sX);
		final Polynom pE12 = parser.Parse("x^2 + y^2 + z^2", sX);
		final Polynom pE13 = parser.Parse("x^3 + y^3 + z^3", sX);
		final Polynom pE2 = parser.Parse("x*y + x*z + y*z", sX);
		final Polynom pE3 = parser.Parse("x*y*z", sX);
		
		// x^3*y^3
		Polynom pP = math.Pow(pE2, 3);
		
		Polynom pX2Y = math.Mult(pE12, pE1);
		pX2Y = math.Mult(pX2Y, pE3);
		pP = math.Add(pP, math.Mult(pX2Y, -3));
		
		Polynom pX3YZ = math.Mult(pE13, pE3);
		pP = math.Add(pP, math.Mult(pX3YZ, 3));
		
		// x*y^2
		Polynom pXY2 = math.Mult(pE2, pE1);
		pXY2 = math.Add(pXY2, math.Mult(pE3, -2));
		pXY2 = math.Mult(pXY2, pE3);
		// display.Display(pP);
		
		// x^4*y^2
		pP = math.Pow(pE2, 2);
		pP = math.Mult(pP, pE12);
		//
		pP = math.Add(pP, math.Mult(pXY2, -2));
		pP = math.Add(pP, math.Mult(pX3YZ, -2));
		
		display.Display(pP);
	}
	
	public void TestDiv() {
		final String sX = "x";
		final Pair<Polynom, Polynom> pairDiv = math.Div(
				parser.Parse("b1*x^3 - b2*b1*x^2", sX), parser.Parse("x^2 - b2*x", sX));
		display.Display(pairDiv.key);
	}
	
	public void S3X2() {
		display.Display("\nS3: x^2 + b1*y + b2*z");
		final String sX = "x";
		
		final Polynom pB1 = parser.Parse("4*b2^2*x^4 - 8*b2^2*R*x^2 +"
				+ "4*b1^3*b2*x^2 - 4*b1^2*b2^3*x - 4*b1^3*b2*R + 4*b1^2*b2^2*R +"
				+ "4*b1^3*b2*x^2 + 2*b1^2*b2^4 - 4*b1^3*b2*R + 4*b1*b2^4*x - 4*b2^4*R + 4*b2^2*R^2 + 2*b1^6", sX);
		
		final Polynom pB2 = parser.Parse("2*b1^4 + 2*b2^4 + 4*b1*b2*x^2 - 4*b1*b2*R", sX);
		final Polynom pSqrt = parser.Parse("b1^4 + 4*b1*b2*x^2 - 4*b2^3*x - 4*b1*b2*R + 4*b2^2*R", sX);
		
		Polynom pClassic = math.Pow(pB2, 2);
		pClassic = math.Mult(pClassic, -1);
		pClassic = math.Mult(pClassic, pSqrt);
		
		pClassic = math.Add(math.Pow(pB1, 2), pClassic);
		display.Display(polyFact.ToSeq(pClassic, sX));
		// Div
		final Polynom pDiv = parser.Parse("x^2 + b1*x + b2*x - R", sX);
		final Pair<Polynom, Polynom> pairDiv = math.Div(pClassic, pDiv);
		// Test
		// display.Display(polyFact.ToSeq(math.Mult(pairDiv.key, pDiv), sX));
		display.Display("");
		display.Display(polyFact.ToSeq(pairDiv.key, sX));
		display.Display(pairDiv.val);
	}
	
	public void S3X2Ext() {
		display.Display("\nS3: Extension for x^2 + b1*y + b2*z");
		final String sX = "x";
		
		final Polynom pB1 = math.Pow(parser.Parse("x^2 + s*x + b3*x*y*z + b1*y + b1*z", sX), 2);
		final Polynom pB2 = math.Pow(parser.Parse("y^2 + s*y + b3*x*y*z + b1*x + b1*z", sX), 2);
		final Polynom pB3 = math.Pow(parser.Parse("z^2 + s*z + b3*x*y*z + b1*x + b1*y", sX), 2);
		Polynom pR = math.Add(math.Add(pB1, pB2), pB3);
		
		final Polynom pX = math.Mult(math.Pow(parser.Parse("R + b1*x - b2*x", sX), 2), -1);
		final Polynom pY = math.Mult(math.Pow(parser.Parse("R + b1*y - b2*y", sX), 2), -1);
		final Polynom pZ = math.Mult(math.Pow(parser.Parse("R + b1*z - b2*z", sX), 2), -1);
		
		pR = math.Add(math.Add(math.Add(pR, pX), pY), pZ);
		//
		final ElementaryPoly elementary = EPolyFactory();
		
		// decompose in Elementary Polynomials
		pR = elementary.EncodeEV3(pR);
		pR = elementary.ReplaceEV3(pR);
		
		final Polynom pE2 = math.Mult(parser.Parse("S^2 + s*S+b1*S+b2*S + 3*b3*E3 - 3*R", "S"), 1, 2);
		pR = math.Mult(pR, 2);
		pR = math.Replace(pR, "E2", pE2);
		//
		final Polynom pE3Repl = parser.Parse(
				"-S^3 - 3*s*S^2 - 2*b1*S^2 - 2*b2*S^2 - 2*s^2*S - s*b1*S - s*b2*S +"
				+ "b1^2*S + b2^2*S + 2*b1*b2*S + 7*R*S + 6*s*R - 3*b1*R - 3*b2*R", "S");
		final Polynom pE3Mult = parser.Parse("7*b3*S - 3*b3*b1 - 3*b3*b2 + 6*s*b3 - 6", "S");
		pR = math.ReplaceOrMult(pR, "E3", pE3Repl, pE3Mult, 2);
		
		// display.Display(pR);
		display.Display(polyFact.ToSeq(pR, "S"));
		display.Display("\n");
	}
	
	public ElementaryPoly EPolyFactory() {
		return new ElementaryPoly(parser, math);
	}
	
	// ++++ helper Classes ++++
	
	public static class ElementaryPoly {
		final MathTools math;
		final Parser parser;
		
		final String sX = "x";
		final String sS = "S"; // E1
		
		final Monom mE3 = new Monom("x", 1).Add("y", 1).Add("z", 1);
		final Polynom pE1_4;
		final Polynom pE1_3;
		final Polynom pE1_2;
		final Polynom pE1;
		final Polynom pE2c1;
		final Polynom pE2;
		
		public ElementaryPoly(final Parser parser, final MathTools math) {
			this.parser = parser;
			this.math = math;
			pE1_4 = parser.Parse("x^4+y^4+z^4", sX);
			pE1_3 = parser.Parse("x^3+y^3+z^3", sX);
			pE1_2 = parser.Parse("x^2+y^2+z^2", sX);
			pE1 = parser.Parse("x+y+z", sX);
			pE2c1 = parser.Parse("x^2*y+x^2*z+y^2*x+y^2*z+z^2*x+z^2*y", sX);
			pE2 = parser.Parse("x*y+x*z+y*z", sX);
		}
		
		public Polynom EncodeEV3(final Polynom p) {
			Polynom pE = math.Replace(p, mE3, "E3");
			// E1^4
			Polynom pRE = math.DivExact(pE, pE1_4, "E1_4");
			if(pRE != null) {
				pE = pRE;
			}
			// E1^3
			pRE = math.DivExact(pE, pE1_3, "E1_3");
			if(pRE != null) {
				pE = pRE;
			}
			// x^2*y
			pRE = math.DivExact(pE, pE2c1, "E2c1");
			if(pRE != null) {
				pE = pRE;
			}
			// E1^2
			pRE = math.DivExact(pE, pE1_2, "E1_2");
			if(pRE != null) {
				pE = pRE;
			}
			// E2
			pRE = math.DivExact(pE, pE2, "E2");
			if(pRE != null) {
				pE = pRE;
			}
			// E1
			pRE = math.DivExact(pE, pE1, sS);
			if(pRE != null) {
				pE = pRE;
			}
			
			return pE;
		}
		public Polynom ReplaceEV3(final Polynom p) {
			Polynom pR = math.Replace(p, "E1_2", parser.Parse(sS + "^2 - 2*E2", sS));
			pR = math.Replace(pR, "E1_3", parser.Parse(sS + "^3 - 3*E2*" + sS + "+3*E3", sS));
			pR = math.Replace(pR, "E1_4", parser.Parse(sS + "^4 - 4*E2*" + sS + "^2 + 4*E3*" + sS + " + 2*E2^2", sS));
			// E2...
			pR = math.Replace(pR, "E2c1", parser.Parse(sS + "*E2 - 3*E3", sS));
			
			return pR;
		}
	}
}
