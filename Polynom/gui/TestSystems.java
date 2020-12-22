package gui;

import java.util.Vector;

import data.Monom;
import data.Polynom;
import io.Parser;
import math.MathTools;
import math.PolyFactory;

public class TestSystems {

	protected final Parser parser;
	protected final MathTools math;
	// PolyFactory is an enhanced version of BaseFactory
	// many functions may be available in BaseFactory
	protected final PolyFactory polyFact;
	
	protected IDisplay display;
	
	public TestSystems(final Parser parser, final MathTools math, final PolyFactory polyFact,
			final IDisplay display) {
		this.parser = parser;
		this.math = math;
		this.polyFact = polyFact;
		//
		this.display = display;
	}
	
	// ++++++++++++++++++++++++
	
	public void Test() {
		System.out.println("Testing Derived Systems:");
		
		// Symmetric => Assym
		this.TestSym();
		this.TestSystem2();
		//
		// this.ExpandHtS3Diff3();
		// this.DivHt3Variant();
		// this.ExpandDiffPQ();
		this.ExpandDiffPQFinal();
		// this.TestGCDSpecial();
	}
	
	// +++++++++++++++++++++++
	
	public void TestSystem2() {
		// from System (x1, x2) => System(x, y, z);
		final String [] sVars  = new String [] {"x1", "x2"};
		final String [] sVars3 = new String [] {"x1", "x2", "x3"};
		final String [] sVarsRepl = new String [] {"x", "y", "z"};
		// x^n + y^n + z^n
		final Vector<Polynom> vxPSym1 = polyFact.SymmetricSimple(sVarsRepl, 2);
		// x*y^2 + y*z^2 + z*x^2
		final Vector<Polynom> vxPAsSimple = polyFact.AsymmetricSimple(sVarsRepl, 1, 2);
		// x^2 + b*y
		final Vector<Polynom> vPAsHt2 = polyFact.HtAsymmetric(sVars, "b1", 2, 1);
		final Vector<Polynom> vxPAsHt3 = polyFact.HtAsymmetric(sVarsRepl, "b1", 2, 1);
		// x^3 - 3*c*x => (x, y, z)
		final Vector<Polynom> vP3 = polyFact.FromPolynom(sVars3, parser.Parse("x^3-3*c*x", "x"), "x");

		// V1
		Vector<Polynom> vPAs = math.Replace(vPAsHt2, sVars, vxPSym1);
		display.Display(vPAsHt2);
		display.Display(vPAs);
		// V2
		vPAs = math.Replace(vPAsHt2, sVars, vxPAsSimple);
		display.Display(vxPAsSimple);
		display.Display(vPAs);
		
		// x^3 - 3*c*x => (x, y, z)
		vPAs = math.Replace(vP3, sVars3, vxPAsHt3);
		display.Display(vxPAsHt3);
		display.Display(vPAs);
	}
	
	public void TestSym() {
		final String [] sVars = new String [] {"x1", "x2", "x3"};
		final String [] sVarsRepl = new String [] {"x", "y", "z"};
		// x^n + y^n + z^n
		final Vector<Polynom> vPSym2 = polyFact.SymmetricSimple(sVars, 2);
		// x^2 + b*y
		final Vector<Polynom> vPAsHt = polyFact.HtAsymmetric(sVars, "b1", 2, 1);
		// x*y^2 + y*z^2 + z*x^2
		final Vector<Polynom> vPAsSimple = polyFact.AsymmetricSimple(sVars, 1, 2);
		final Vector<Polynom> vxPAsSimple = polyFact.AsymmetricSimple(sVarsRepl, 1, 2);
		//
		final Vector<Polynom> vxPS1 = polyFact.SymmetricSimple(sVarsRepl, 1);
		// x^2 - b*x, y^2 - x*y, z^2 - y*z
		final Vector<Polynom> vxPAs = polyFact.AsymmetricLinkedSym(sVarsRepl, "b", 2, 1);
		// x+y, y+z, z-x
		final Vector<Polynom> vxPAsSum = polyFact.AsymmetricSumDiff(sVarsRepl, 1, 1);
		// final Vector<Polynom> vReplace = polyFact.AsymmetricDiff(sVarsRepl, 2, 1);
		// (x+y+z, x-y+z, x+y-z)
		final Vector<Polynom> vPAsDx = polyFact.AsymmetricAlternating(sVarsRepl, 1, 1);
		
		Vector<Polynom> vPAs = math.Replace(vPAsSimple, sVars, vxPAsSum);
		display.Display(vPAsSimple);
		display.Display(vPAs);
		//
		vPAs = math.Replace(vPSym2, sVars, vxPAs);
		display.Display(vPSym2);
		display.Display(vPAs);
		//
		vPAs = math.Replace(vPSym2, sVars, vxPAsSum);
		display.Display("Sym 2 + Sum/Diff");
		display.Display(vPSym2);
		display.Display(vPAs);
		//
		vPAs = math.Replace(vPSym2, sVars, vPAsDx);
		display.Display(vPSym2);
		display.Display(vPAs);
		// Ht-As
		vPAs = math.Replace(vPAsHt, sVars, vxPAsSimple);
		display.Display(vxPAsSimple);
		display.Display(vPAs);
		// Ht-As
		vPAs = math.Replace(vPAsHt, sVars, vxPAsSum);
		display.Display("HtAs + x+y, y+z, z-x");
		display.Display(vPAsHt);
		display.Display(vPAs);
	}
	
	// ++++++++++++++++++
	
	public void TestGCDSpecial() {
		final Polynom p1 = parser.Parse("x^5 - x^5*y^5 + 2*b*x^3*y^3 + b^2*y^3 + b*x^2 + x*y + x*y^3", "x");
		final Polynom p2 = parser.Parse("x^5 + x^5*y^4 + 2*b*x^3*y^3 - b^2*y^3 + b*y^2 + x*y^2 + x*y", "x");
		
		Polynom pR = math.GcdExtract(p1, p2, "y");
		display.Display("Extract P(y, x):");
		display.Display(polyFact.ToSeq(pR, "y"));
	}
	
	public void ExpandDiffPQFinal() {
		final Polynom pEq = parser.Parse("b^2*E3^3*S - b*R*S^2*E3*div^2 + 6*R^2*div^3", "S");
		final Polynom pDiv = parser.Parse("R*S^10*b^5 - 63*R^2*S^5*b^4 - 12*R^3*S^6*b^5 + 243*R^4*S*b^4"
				+ "- R^4*S^7*b^6 + 135*R^5*S^2*b^5", "S");
		final Polynom pRepl = math.Mult(
				parser.Parse("- 6*R^2*S^8*b^4 - 162*R^3*S^3*b^3 + 99*R^4*S^4*b^4 + 6*R^5*S^5*b^5 - 486*R^6*b^4", "S"), -1);
		
		Polynom pR = math.Expand(pEq, new String [] {"E3", "div"}, new Polynom [] {pRepl, pDiv});
		
		display.Display("\nHtS3 P2: Diff type variant");
		display.Display("R^minPow = " + math.MinPow(pR, "R"));
		display.Display("b^minPow = " + math.MinPow(pR, "b"));
		pR = math.DivAbs(pR, new Monom("R", 6));
		pR = math.DivAbs(pR, new Monom("b", 11));
		pR = math.DivAbs(pR, new Monom("S", 1));
		pR = math.Mult(pR, 1, 27);
		display.Display(polyFact.ToSeq(pR, "S"));
		
		final Polynom pDivSimple  = parser.Parse("b*S^5 - 243*R", "S");
		final Polynom pDivSimple2 = parser.Parse("b*S^7 - b^2*R^3*S^4 - 8*b*R^2*S^3 - 3*R*S^2 - 4*b^2*R^5", "S");
		pR = math.Div(pR, pDivSimple).key;
		pR = math.Div(pR, pDivSimple2).key;
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	public void ExpandDiffPQ() {
		final Polynom p2 = parser.Parse("b^2*S^4 - 9*b^2*R^2", "S");
		final Polynom p1 = parser.Parse("b^2*R^2*S^3 - 6*b*R*S^2", "S");
		final Polynom pR = parser.Parse("6*b*R^3*S + 27*R^2", "S");
		final Polynom pP3_c = parser.Parse("b*R*S^2", "S");
		final Polynom pP3_cDiv = parser.Parse("3*b^2*S", "S");
		final Polynom pP3_d = parser.Parse("-9*R^2", "S");
		final Polynom pP3_dDiv = parser.Parse("3*b^2*S", "S");
		//
		final Polynom pE32_M = parser.Parse("6*R*S^3*b^3 - R^2*S^4*b^4", "S");
		//
		final Polynom pE3  = new Polynom(new Monom("E3", 1), 1, "S");
		final Polynom pE32 = new Polynom(new Monom("E3", 2), 1, "S");
		//
		final Polynom pE3Base = parser.Parse("b^2*S", "S");
		final Polynom pE3Eq = math.Add(
				math.Mult(pE3Base, math.Pow(pE3, 3)),
				parser.Parse("- b*R*S^2*E3 + 6*R^2", "S"));
		
		Polynom pEq3 = math.Add(
				math.Mult(p2, pE32),
				math.Mult(p1, pE3));
		pEq3 = math.Diff(pEq3, pR);
		Polynom pEq3T = math.Mult(pEq3, math.Mult(pE3Base, pE3));
		pEq3T = math.Diff(pEq3T, math.Mult(pE3Eq, p2));
		
		pEq3T = math.Mult(pEq3T, p2);
		pEq3T = math.Add(pEq3T, math.Mult(pEq3, pE32_M));
		
		display.Display("\nHtS3 P2: Diff type variant");
		display.Display(polyFact.ToSeq(pEq3T, "E3"));
	}
	
	public void DivHt3Variant() {
		final Polynom p1 = parser.Parse("b^2*S^6 + b^3*R*S^5 - 6*b*R*S^3 - 9*b^2*R^2*S^2 - 27*R^2", "S");
		//
		final Polynom pDiv = parser.Parse("b*S^3 - 9*R", "S");
		
		Polynom pR = math.Div(p1, pDiv).key;
		
		display.Display("\nHtS3 P2: Diff type variant");
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void ExpandHtS3Diff() {
		final Polynom p1a = parser.Parse("b^2*R*S^2 - b*R*S^2 + 6*R^2 - 9*b*R^2", "S");
		final Polynom p1b = parser.Parse("2*b*R*S^2 - 12*R^2", "S");
		//
		final Polynom p2a = parser.Parse("b^3*R*S^6 - 14*b^2*R^2*S^4 + 15*R^3*b*S^2 + 279*R^4", "S");
		final Polynom p2b = parser.Parse("1", "S");
		//
		final Polynom p3a = p1a;
		final Polynom p3b = p1a;
		//
		final Polynom pDiv = parser.Parse("b*S^2 - 9*R", "S");
		//
		Polynom pR = math.DotProd(
				new Polynom [] {p1a, p2a, p3a},
				new Polynom [] {p1b, p2b, p3b});
		pR = math.Div(pR, new Polynom(new Monom("R", 1), 1, "R")).key;
		pR = math.Div(pR, pDiv).key;
		pR = math.Div(pR, pDiv).key;
		
		display.Display("\nHtS3 P2: Diff type");
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void ExpandHtS3Diff3() {
		final Polynom p1 = parser.Parse("2*b1^3*S^6 - 36*b1^2*R*S^4 + 162*b1*R^2*S^2 - 162*R^3 - 6*b1^3*R^2", "S");
		final Polynom p2 = parser.Parse("12*b1^3*S^3 + 2*b1^5*S - 54*b1^2*R*S", "S");
		final Polynom pMult1 = parser.Parse("b1^2*S^2 - 9*b1*R", "S");
		final Polynom pMult2 = parser.Parse("b1*R*S^3 - 9*R^2*S)", "S");
		//
		final Polynom pDiv  = parser.Parse("b1*S^2 - 9*R", "S");
		final Polynom pDiv2 = parser.Parse("b1*S^2 - 3*R", "S");
		
		Polynom pRez = math.Add(
				math.Mult(p1, pMult1),
				math.Mult(p2, pMult2));
		pRez = math.Mult(pRez, 1, 2);

		pRez = math.Div(pRez, pDiv).key;
		pRez = math.Div(pRez, pDiv2).key;
		
		display.Display("\nHtS3 P3: Diff type");
		display.Display(polyFact.ToSeq(pRez, "S"));
		
		pRez = math.Replace(pRez, "R", parser.Parse("R1 - b2*S - b3*S^2 - b4*S^3", "S"));
		display.Display(polyFact.ToSeq(pRez, "S"));
	}
}
