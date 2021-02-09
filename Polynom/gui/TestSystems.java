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
		// this.ExpandDiffPQFinal();
		// this.TestGCDSpecial();
		// this.ClassicPoly();
		// this.ClassicPolyP4_ExtXY();
		// this.ClassicPolyS3_AsymDual(); // TODO: optimize
		// this.ClassicPolyS3_AsymDualCorect();
		// this.ExpandAsymS2P3();
		// this.ExpandAsymCoeffS2P2();
		// this.ExpandAsymCoeffS2P3();
		// this.HtS3P2();
		// this.ClassicHtMixt21S2P2();
		// this.ClassicHtMixt31S2P2();
		// this.ExpandS3_HtP3YZ();
		
		// S4:
		// this.ExpandS4_AsymV3Simple();
		
		// Asym Basic
		// this.AssymS3P2Simple();
		
		// S2:
		// this.S2P3_Classic();
		// this.ClassicS2P2_SimpleEnt2();
		
		// S3:
		// this.S3P2_Classic();
		// this.ShiftS3P2_Simple();
		// this.ClassicHt2HP_S3P2();
		// TODO:
		this.SolveS3P3_Y2Y1();
		// this.SolveS3P2_3A();
		// TODO:
		// this.SolveS3P3_3A();
		this.SolveMixtS3P2_Omega();
		
		// S3: Mixt Ht+Sym:
		// this.SolveS3P3_MixtHtSym();
		// this.AsymS3P1();
		// still massive Overflow
		// this.SolveS2P3_Asym();
		
		// this.SolveS22P3_Ent();
	}
	
	public Polynom Div(final String sP1, final String sDiv, final String sVar) {
		final Polynom pR =
				math.Div(parser.Parse(sP1, "x"),
						parser.Parse(sDiv, sVar)).key;
		return pR;
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
	public void ClassicPolyP3XY() {
		final Polynom p1 = parser.Parse("x^3 + b2*x*y + b1*y - R", "x");
		final Polynom p2 = parser.Parse("y^3 + b2*x*y + b1*x - R", "x");
		final Polynom pDiv = parser.Parse("x^3 + b2*x^2 + b1*x - R", "x");

		Polynom pR = polyFact.ClassicPolynomial(p2, p1, pDiv);
		pR = math.Mult(pR, -1);
		display.Display("Extract P(y, x):");
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	public void ClassicPolyP3XYExt() {
		final Polynom p1 = parser.Parse("x^3 + b3*x*y + b2*y^2 + b1*y - R", "x");
		final Polynom p2 = parser.Parse("y^3 + b3*x*y + b2*x^2 + b1*x - R", "x");
		final Polynom pDiv = parser.Parse("x^3 + b3*x^2 + b2*x^2 + b1*x - R", "x");

		Polynom pR = polyFact.ClassicPolynomial(p1, p2, pDiv);
		pR = math.Div(pR, parser.Parse("b2^2", "b2")).key;
		display.Display("Extract P(y, x):");
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	public void ClassicPolyX3aY3() {
		final Polynom p1 = parser.Parse("a1*x^3 + a2*y^3 + b1*x - R", "x");
		final Polynom p2 = parser.Parse("a1*y^3 + a2*x^3 + b1*y - R", "x");
		final Polynom pDiv = parser.Parse("a1*x^3 + a2*x^3 + b1*x - R", "x");

		Polynom pR = polyFact.ClassicPolynomial(p1, p2, pDiv);
		display.Display("Extract P(y, x):");
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	public void ClassicPolyX3aY3Ext() {
		final Polynom p1 = parser.Parse("a1*x^3 + a2*y^3 + b2*x*y + b1*x - R", "x");
		final Polynom p2 = parser.Parse("a1*y^3 + a2*x^3 + b2*x*y + b1*y - R", "x");
		final Polynom pDiv = parser.Parse("a1*x^3 + a2*x^3 + b2*x^2 + b1*x - R", "x");

		Polynom pR = polyFact.ClassicPolynomial(p1, p2, pDiv);
		pR = math.Div(pR, parser.Parse("a2", "a2")).key;
		// pR = math.Div(pR, parser.Parse("a2^2 + a1^2 - 2*a1*a2", "a2")).key;
		// pR = math.Div(pR, parser.Parse("a2 - a1", "a2")).key;
		display.Display("x^3 + y^3: Extract P(y, x):");
		display.Display(polyFact.ToSeq(pR, "x"));
		//
		display.Display(polyFact.ToSeq(
				this.Div("- 3*R*a1*a2^2*b2 + 3*R*a1^2*a2*b2 - R*a1^3*b2 + R*a2^3*b2 + 2*a1*a2*b1*b2^2"
						+ "- a1^2*b1*b2^2 - a1^3*b1^2 - a2^2*b1*b2^2 + a2^3*b1^2",
						"a2 - a1", "a2"), "x"));
		//
		display.Display(polyFact.ToSeq(
				this.Div("2*a1*a2^3 - 2*a1^3*a2 + a1^4 - a2^4",
						"a2^2+a1^2-2*a1*a2", "a2"), "x"));
	}
	public void ClassicPolyX3YXY3() {
		final Polynom p1 = parser.Parse("a1*x^3*y + a2*x*y^3 + b1*x - R", "x");
		final Polynom p2 = parser.Parse("a1*x*y^3 + a2*x^3*y + b1*y - R", "x");
		final Polynom pDiv = parser.Parse("a1*x^4 + a2*x^4 + b1*x - R", "x");

		Polynom pR = polyFact.ClassicPolynomial(p1, p2, pDiv);
		display.Display("Extract P(y, x):");
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	public void ClassicPolyP3_XY2() {
		final Polynom p1 = parser.Parse("x^3 + b3*x^2*y + b2*x*y^2 + b1*x - R", "x");
		final Polynom p2 = parser.Parse("y^3 + b3*y^2*x + b2*y*x^2 + b1*y - R", "x");
		final Polynom pDiv = parser.Parse("x^3 + b3*x^3 + b2*x^3 + b1*x - R", "b3");

		Polynom pR = polyFact.ClassicPolynomial(p1, p2, pDiv);
		pR = math.Div(pR, parser.Parse("b2^2", "b2")).key;
		display.Display("Extract P(y, x):");
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	public void ClassicPolyP4_ExtXY() {
		final Polynom p1 = parser.Parse("x^4 + b2*x*y + b1*y - R", "x");
		final Polynom p2 = parser.Parse("y^4 + b2*x*y + b1*x - R", "x");
		final Polynom pDiv = parser.Parse("x^4 + b2*x^2 + b1*x - R", "x");

		Polynom pR = polyFact.ClassicPolynomial(p2, p1, pDiv);
		// pR = math.Div(pR, parser.Parse("b2^2", "b2")).key;
		display.Display("Extract P(y, x):");
		display.Display(polyFact.ToSeq(pR, "x"));
	}

	public void ClassicPolyS3_AsymDualCorect() {
		final Polynom p = parser.Parse("x*y^2 + y*z^2 + z*x^2 - R1", "x");
		final Polynom pR3 = parser.Parse("R3", "x");
		final Polynom pMultR3 = parser.Parse("x*z", "x");
		//
		final Polynom pY = parser.Parse("x*z^2*R1 - x^2*z*R2", "x");
		final Polynom pMultY = parser.Parse("z^3*R3 - x^3*R3", "x");
		
		Polynom pR = math.ReplaceOrMult(p, "y", pR3, pMultR3);
		pR = polyFact.ClassicPolynomial(pR, math.Diff(pMultY, pY), null, "z");
		// pR = math.DivAbs(pR, new Monom("R2", 1));
		pR = math.DivAbs(pR, new Monom("x", 4));
		pR = math.Mult(pR, -1);
		display.Display("S3 Asym Dual: the Polynomial " + pR.size());
		display.Display(polyFact.ToSeq(pR, "x")); // x^39, but should be x^18;
	}
	public void ClassicPolyS3_AsymDual() {
		final Polynom p1 = parser.Parse("x*y^2 + y*z^2 + z*x^2 - R1", "x");
		final Polynom p2 = parser.Parse("x*z^2 + y*x^2 + z*y^2 - R2", "x");
		final Polynom p3 = parser.Parse("x*y*z - R3", "x");

		display.Display("S3 Asym Dual:");
		// Polynom pR = polyFact.ClassicPolynomial(new Polynom [] {p1, p2, math.Add(p1, p2), p3}, null,
		//		new String [] {"y", null, "z"});
		Polynom pR = polyFact.ClassicPolynomial(new Polynom [] {p3, p1, p2, math.Add(p1, p2)}, null,
				new String [] {"y", "z", null}); // very fast! (but x^72)
		pR = math.DivAbs(pR, new Monom("x", 1).Add("R1", 4).Add("R3", 6));
		// pR = math.DivAbs(pR, new Monom("x", 12));
		// pR = math.Div(pR, parser.Parse("b2^2", "b2")).key;
		// pR = math.Replace(pR, "R1", 1, 1);
		// pR = math.Replace(pR, "R2", 1, 1);
		// pR = math.Replace(pR, "R3", 1, 1);
		display.Display("S3 Asym Dual: the Polynomial");
		display.Display(polyFact.ToSeq(pR, "x"));
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
	
	public void ExpandAsymS2P3() {
		final Polynom pR12 = parser.Parse("R1+R2", "S");
		final Polynom p1 = math.Replace(
				parser.Parse("-S^6 - 2*b*S^4 + 8*b^2*S^2 - 10*b*R12*S + 2*R12^2", "S"),
				"R12", pR12);
		final Polynom pMult = parser.Parse("2*S^3 - 4*b*S + R1 + R2", "S");
		final Polynom p2 = math.Replace(
				parser.Parse("7*R12*S^6 - 14*b*R12*S^4", "S"),
				"R12", pR12);
		final Polynom p3 = parser.Parse("-10*R1^2*S^3 - 10*R2^2*S^3 + 34*R1*R2*S^3", "S");
		//
		Polynom pR = math.Mult(p1, pMult);
		pR = math.Add(pR, p2);
		pR = math.Add(pR, p3);
		pR = math.Mult(pR, 1, -2);
		//
		display.Display(polyFact.ToSeq(pR, "S"));
		
		//
		final Polynom pDiv = parser.Parse("xy^2 + 2*b*xy + b^2", "xy");
		final Polynom pS1 = parser.Parse("-3*xy*S + S^3 + b*S - R1 - R2", "S");
		final Polynom pS2 =
				parser.Parse("xy^3 - b*xy*S^2 - b^2*S^2 + b*R1*S + b*R2*S + 2*b*xy^2 + b^2*xy - R1*R2", "S");
		pR = polyFact.ClassicPolynomial(pS2, pS1, pDiv, "S");
		display.Display(polyFact.ToSeq(pR, "xy"));
	}
	
	public void ExpandAsymCoeffS2P2() {
		final Polynom pS1 = parser.Parse("S^3 - 3*xy*S + b1*xy + b2*xy - R*S", "S");
		final Polynom pS2 = parser.Parse("R*S^2 - xy^2 - 2*R*xy + b1*b2*xy - R^2", "S");
		final Polynom pDiv = parser.Parse("S^2 - R", "S");
		Polynom pR = polyFact.ClassicPolynomial(pS2, pS1, pDiv, "xy");
		pR = math.Mult(pR, -1);
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void ExpandAsymCoeffS2P3() {
		final Polynom pS1 = parser.Parse("S^4 - 4*xy*S^2 + 2*xy^2 + b1*xy + b2*xy - R*S", "S");
		final Polynom pS2 = parser.Parse("R*S^3 - xy^3 - 3*R*S*xy + b1*b2*xy - R^2", "S");
		final Polynom pDiv = parser.Parse("S^3 - R", "S");
		Polynom pR = polyFact.ClassicPolynomial(pS1, pS2, pDiv, "xy");
		pR = math.Mult(pR, 1, 4);
		display.Display(polyFact.ToSeq(pR, "S"));

		final Polynom pSp1 = parser.Parse("S^4 - 4*xy*S^2 + 2*xy^2 - R*S", "S");
		final Polynom pSp2 = parser.Parse("S^5 - 4*xy*S^3 + 3*xy^2*S - 2*R*S^2 + b1^2*S + 2*R*xy", "S");
		final Polynom pSpDiv = parser.Parse("S", "S");
		pR = polyFact.ClassicPolynomial(pSp1, pSp2, pSpDiv, "xy");
		pR = math.Mult(pR, 1, 2);
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void HtS3P2() {
		final Polynom pSp1 = parser.Parse("S^4 + 2*b1*S^3 - 10*R*S^2 - b1^2*S^2 +"
				+ "6*b1*R*S + 6*b1^3*S - 18*b1^2*R + 9*R^2", "S");
		final Polynom pDiv = parser.Parse("S^2 + 3*b1*S - 9*R", "S");
		Polynom pR = math.Div(pSp1, pDiv).key;
		display.Display("\nS3P2: Ht");
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void ClassicHtMixt21S2P2() {
		final Polynom p1 = parser.Parse("x^2*y + b3*x*y + b2*x^2 + b1*x - R", "x");
		final Polynom p2 = parser.Parse("y^2*x + b3*x*y + b2*y^2 + b1*y - R", "x");
		final Polynom pDiv = parser.Parse("x^3 + b3*x^2 + b2*x^2 + b1*x - R", "x");
		Polynom pR = polyFact.ClassicPolynomial(p2, p1, pDiv, "y");
		pR = math.Mult(pR, 1, 1);
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	public void ClassicHtMixt31S2P2() {
		final Polynom p1 = parser.Parse("x^3*y + b3*x^2*y^2 + b2*x*y + b1*y - R", "x");
		final Polynom p2 = parser.Parse("y^3*x + b3*x^2*y^2 + b2*x*y + b1*x - R", "x");
		final Polynom pDiv = parser.Parse("x^4 + b3*x^4 + b2*x^2 + b1*x - R", "x");
		final Polynom pMult = parser.Parse("b3^2 - 1", "x");
		display.Display("\nStarting Classic Polynomial:");
		Polynom pR = polyFact.ClassicPolynomial(p2, p1, pDiv, "y");
		pR = math.Mult(pR, 1, 1);
		display.Display(polyFact.ToSeq(pR, "x"));
		display.Display("Size = " + pR.size());
		// if(true) return;
		//
		final String [] ssP = new String [] {"b1*b3^4 - 2*b1*b3^2 + b1",
				"- R*b3^4 + 2*R*b3^2 - R",
				"3*b1*b2 - 4*b1*b2*b3^2 + b1*b2*b3^4",
				"3*b1^2 - 5*b1^2*b3^2 - b1^2*b3^3 + 2*b1^2*b3^4 + b1^2*b3^5 + 2*R*b2*b3^2 - 2*R*b2", // x^7
				"-3*b1*R + 3*b1*b2^2 + 3*R*b1*b3 + 5*R*b1*b3^2 -3*b1*b2^2*b3^2 -3*R*b1*b3^3 -2*R*b1*b3^4",
				"6*b1^2*b2 -1*R*b2^2 -2*R^2*b3 -7*b1^2*b2*b3^2 + 1*R*b2^2*b3^2 + 2*R^2*b3^3 + 1*b1^2*b2*b3^4",
				"3*b1^3 - 4*R*b1*b2 + 1*b1*b2^3 + 3*R*b1*b2*b3 - 4*b1^3*b3^2 + 4*R*b1*b2*b3^2"
				+ "- 1*b1*b2^3*b3^2 - 3*R*b1*b2*b3^3 + 1*b1^3*b3^4",
				"-3*R*b1^2 + 3*b1^2*b2^2 + 3*R*b1^2*b3^1 + 4*R*b1^2*b3^2 - 3*b1^2*b2^2*b3^2 - 3*R*b1^2*b3^3 - R*b1^2*b3^4",
				"3*b1^3*b2 - 1*R*b1*b2^2 - 2*R^2*b1*b3 - 3*b1^3*b2*b3^2 + 1*R*b1*b2^2*b3^2 + 2*R^2*b1*b3^3",
				"R^3 + 1*b1^4 - 2*R*b1^2*b2 - R^3*b3^2 - 1*b1^4*b3^2 + 2*R*b1^2*b2^1*b3^2",
				"-R*b1^3 + R*b1^3*b3^2"};
		final Polynom pCl = math.DivRobust(
				polyFact.Create(parser.Parse(ssP, "x"), "x", true), pDiv).key;
		display.Display(polyFact.ToSeq(pCl, "x"));
		pR = math.DivRobust(math.Mult(pR, pMult), pCl).key;
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	// +++++++++++++++++++
	

	public void ExpandS4_AsymV3Simple() {
		final Polynom p1 = parser.Parse("S^2 - 2*E2 + b*E3 - 4*R", "S");
		final Polynom p2 = parser.Parse("E4*S - 2*b*E2*E4 + b*E3^2 - R*E3", "S");
		final Polynom p3 = parser.Parse("S^3 - 3*E2*S + 3*E3 + 4*b*E4 - R*S", "S");
		final Polynom p4 = parser.Parse("S^4 - R*S^2 + 2*E2^2 - 4*E2*S^2 + 2*R*E2 + 4*E3*S + b*E4*S - 4*E4", "S");

		display.Display("S4 Asym V3:");
		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {p1, p2, p3, p4, p3}, null,
				new String [] {"E2", "E4", "E3", null});
		pR = math.Mult(pR, 1, 12);
		// pR = math.DivAbs(pR, new Monom("x", 1).Add("R1", 4).Add("R3", 6));
		// pR = math.DivAbs(pR, new Monom("x", 12));
		// pR = math.Div(pR, parser.Parse("b2^2", "b2")).key;
		display.Display("S4 Asym V3: the s-Polynomial");
		display.Display(polyFact.ToSeq(pR, "S"));
		
		final Polynom pSubst = math.Mult(
				parser.Parse("560*R*S^2 - 780*R*S^3*b - 224*R*S^4*b^2 + 213*R*S^5*b^3 - 9*R*S^6*b^4"
				+ "+ 3024*R^2*S*b - 564*R^2*S^2*b^2 - 768*R^2*S^3*b^3 + 24*R^2*S^4*b^4 + 816*R^3*S*b^3 - 16*R^3*S^2*b^4"
				+ "+ 3136*R^3*b^2 - 56*S^4 + 36*S^5*b + 37*S^6*b^2 - 18*S^7*b^3 + S^8*b^4", "S"), -1);
		final Polynom pE3Div = math.Mult(
				parser.Parse("- 324*R*S*b^2 - 256*R*S^2*b^3 + 249*R*S^3*b^4 - 5*R*S^4*b^5 + 1008*R*b"
				+ "- 252*R^2*S*b^4 + 4*R^2*S^2*b^5 - 1408*R^2*b^3 + 336*S - 188*S^2*b - 240*S^3*b^2 + 199*S^4*b^3"
				+ "- 48*S^5*b^4 + S^6*b^5", "S"), 1, 1);
		final Polynom pR0 = parser.Parse("10*R*S^2 - 14*R*S^3*b + 40*R^2*S*b - S^4 + S^5*b", "S");
		final Polynom pR1 = parser.Parse("- 22*R*S*b^2 + 32*R*b + 6*S - 9*S^2*b + 4*S^3*b^2", "S");
		final Polynom pR2 = parser.Parse("3*S*b^3 - 14*b^2", "S");
		// (R0 * pE3Div + R1 * pSubst)*pE3div
		pR = math.Add(
				math.Mult(pR2, pSubst),
				math.Mult(pR1, pE3Div));
		pR = math.Mult(pR, pSubst);
		pR = math.Add(pR, math.Mult(pR0, math.Mult(pE3Div, pE3Div)));
		pR = math.Mult(pR, 1, 6);
		// pR = math.Div(pR, pDiv).val; ERROR: Numeric Overflow!
		display.Display(polyFact.ToSeq(pR, "S"));
		this.DivS4_AsymV3Simple();
		// this.DivS4_AsymV3SimpleEq3();
	}
	public void DivS4_AsymV3Simple() {
		final Polynom pDiv  = parser.Parse("4*S^2 + b*S^3 - 64*R", "S");
		final Polynom pDiv2 = parser.Parse("b*S + 1", "S");
		final Polynom pDiv3 = parser.Parse("b*S^2 - 2*S - 4*b*R", "S");
		final Polynom ppS10 = polyFact.Create(parser.Parse(
				new String [] {"b^5",
				"2*b^4", " - 2*b^3 - 6*R*b^5", "19*b^2 - 110*R*b^4",
				"-34*b + 16*R*b^3 + 9*R^2*b^5", "-56 - 432*R*b^2 + 523*R^2*b^4",
				"568*b*R + 2026*R^2*b^3 - 4*R^3*b^5", "1120*R + 2472*R^2*b^2 - 596*R^3*b^4",
				"- 2624*b*R^2 - 6608*b^3*R^3", "-3584*R^2 - 13184*b^2*R^3 + 256*b^4*R^4",
				"-7168*b*R^3 + 256*b^3*R^4"}, "S"), "S", true);
		Polynom pR = math.Div(ppS10, pDiv).key;
		pR = math.Div(pR, pDiv2).key;
		pR = math.Div(pR, pDiv3).key;
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	public void DivS4_AsymV3SimpleEq3() {
		final Polynom pDiv = parser.Parse("x^2 + b*x^3 - R", "x");
		final Polynom ppS10 = parser.Parse("b^3*x^7 - b^2*R*x^4 + x^4 - 2*R*x^2 + R^2", "x");
		Polynom pR = math.Div(ppS10, pDiv).key;
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	public void ExpandS3_HtP3YZ() {
		final Polynom pS1 = parser.Parse("S^3 - 3*E2*S + 3*E3 + b1*E2 - 3*R", "S");
		final Polynom pS2 = parser.Parse("S^4 - 4*E2*S^2 + 4*E3*S + 2*E2^2 + 3*b1*E3 - R*S", "S");
		final Polynom pS3 = parser.Parse("E3*S^2 - 2*E2*E3 + b1*E2^2 - 2*b1*E3*S - R*E2", "S");
		//
		final Polynom pDiv = parser.Parse("S^3 + 3*b1*S^2 - 27*R", "S");
		final Polynom pDiv2 = parser.Parse("6*S - 5*b1", "S");
		final Polynom pDiv3 = parser.Parse("S + b1", "S");
		//
		display.Display("S3 Ht P3: + b*y*z");
		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {pS1, pS2, pS3}, null,
				new String [] {"E3", "E2"});
		pR = math.Mult(pR, 1, 2);
		//
		pR = math.Div(pR, pDiv).key;
		// pR = math.Mult(pR, new Monom("R", 8));
		pR = math.Div(pR, pDiv2).key;
		pR = math.Div(pR, pDiv3).key;
		pR = math.Mult(pR, 1, 3);
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void AssymS3P2Simple() {
		final Polynom pPr1 = parser.Parse("x^2 + b1*x + b2*S", "x");
		final Polynom pPr2 = parser.Parse("y^2 + b1*y + b2*S", "x");
		final Polynom pPr3 = parser.Parse("z^2 + b1*z + b2*S", "x");
		Polynom pR = math.Mult(pPr1, pPr2);
		pR = math.Mult(pR, pPr3);
		pR = math.Replace(pR, new Monom("x", 1).Add("y", 1).Add("z", 1), "E3");
		// display.Display(pR);
		// s
		final Polynom pS1 = parser.Parse("S^2 + b1*S + 3*b2*S - 2*E2 - R1 - R2 - R3", "S");
		final Polynom pS2 = parser.Parse("E3^2 + b1*E3*E2 + b1^2*E3*S + b1^3*E3" + 
				"+ b2^3*S^3 - b2^2*R1*S^2 - b2^2*R2*S^2 - b2^2*R3*S^2" +
				"+ b2*R1*R2*S + b2*R1*R3*S + b2*R2*R3*S - R1*R2*R3", "S");
		final Polynom pS3 = parser.Parse("b1*E2*E3 - 2*b2*E3*S^2 + b1^2*E3*S - 3*b1*b2*E3*S + b1^3*E3 + E3^2" + 
				"+ b1*b2*E2*S^2 - 2*b2^2*E2*S^2 + b1^2*b2*E2*S + b2*E2^2*S" + 
				"+ b2^2*S^4 + b2^3*S^3 + b1*b2^2*S^3 - R1*R2*R3", "S");
		//
		pR = polyFact.ClassicPolynomial(
				new Polynom [] {pS1, pS2, pS3}, null,
				new String [] {"E2", "E3"});
		pR = math.Mult(pR, 1, 16);
		pR = math.DivAbs(pR, new Monom("b2", 2));
		display.Display(polyFact.ToSeq(pR, "S"));
		//
		this.AssymS3P2Simple_Classic();
	}
	public void AssymS3P2Simple_Classic() {
		final Polynom p1 = parser.Parse("x^2 + b1*x + b2*x + b2*y + b2*z - R1", "x");
		final Polynom p2 = parser.Parse("y^2 + b1*y + b2*x + b2*y + b2*z - R2", "x");
		final Polynom p3 = parser.Parse("z^2 + b1*z + b2*x + b2*y + b2*z - R3", "x");

		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {p1, p2, p3}, null,
				new String [] {"y", "z"});
		pR = math.Mult(pR, 1, 1);
		pR = math.DivAbs(pR, new Monom("b2", 1));
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	// +++++++++++ S2 Systems +++++++++++
	
	public void S2P3_Classic() {
		final Polynom p1 = parser.Parse("x^3 + y^3 + b3*x^2 + b3*y^2 + 2*b3*x*y + b1*x + b1*y - R1", "x");
		final Polynom p2M = parser.Parse("x^2*y + x*y^2 - R2", "x");
		final Polynom p2MAddB2 = parser.Parse("x^2*y + x*y^2 + b2*x + b2*y - R2", "x");
		final Polynom pRepl1 = parser.Parse("-2*R2", "R2");
		final Polynom pRepl2 = parser.Parse("-4*R2", "R2");
		final Polynom pRepl_b1 = parser.Parse("-1.5*b2", "b2");

		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {p1, p2MAddB2}, null,
				new String [] {"y"});
		pR = math.Mult(pR, -1, 1);
		pR = math.DivAbs(pR, new Monom("x", 2));
		display.Display(polyFact.ToSeq(pR, "x"));
		//
		pR = math.Replace(pR, "R1", pRepl1);
		pR = math.DivAbs(pR, new Monom("R2", 1));
		display.Display(polyFact.ToSeq(pR, "x"));
		//
		pR = math.Replace(pR, "b1", pRepl_b1);
		pR = math.Mult(pR, 2, 1);
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	// +++++++++++ S3 Systems +++++++++++
	
	public void S3P2_Classic() {
		final Polynom p1 = parser.Parse("x^2 + y^2 + z^2 + b1*x + b1*y + b1*z - R1", "x");
		final Polynom p2 = parser.Parse("x*y + x*z + y*z - R2*x - R2*y - R2*z", "x");
		final Polynom p3_1 = parser.Parse("x^2*y*z + x*y^2*z + x*y*z^2 - R3", "x");
		final Polynom p3_D1 = parser.Parse("x*y*z - R3*x - R3*y - R3*z", "x");
		final Polynom pRepl1 = parser.Parse("", "R2");
		final Polynom pDiv1 = parser.Parse("x", "x");

		display.Display("\nS3P2: Classic Poly");
		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {p2, p1, p3_D1}, null,
				new String [] {"y", "z"});
				// , new Polynom [] {null, pDiv1, null});
		pR = math.Mult(pR, -1, 1);
		// pR = math.DivAbs(pR, new Monom("x", 2));
		display.Display(polyFact.ToSeq(pR, "x"));
		//
		// pR = math.Replace(pR, "R1", pRepl1);
		// pR = math.DivAbs(pR, new Monom("R2", 1));
		// display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	public void ShiftS3P2_Simple() {
		final Polynom p1 = parser.Parse("- R*b[1]^4 + 2*R^2*b[1]^2 - R^3 + b[1]^6" +
				"+ 2*R*b[1]^3*x - R^2*b[1]*x - b[1]^5*x + 3*R^2*x^2 - 3*R*b[1]^2*x^2 + b[1]^4*x^2" + 
				"+ 2*R*b[1]*x^3 - b[1]^3*x^3 - 3*R*x^4 + b[1]^2*x^4 - b[1]*x^5 + x^6", "x");
		final Polynom pRepl  = parser.Parse("6*b1", "x");
		final Polynom pRepl2 = parser.Parse("x + b1", "x");
		Polynom pR = math.Replace(p1, "b1", pRepl);
		pR = math.Replace(pR, "x", pRepl2);
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	public void ClassicHt2HP_S3P2() {
		//
		final String [] ssP = new String [] {
				"-1", "3*b1 + b2",
				"6*R - 3*b1^2 + 2*b1*b2 + b2^2 + 3*b3*R + 6*b1^2*b3 - 2*b1*b2*b3 + 6*b1^2*b3^2",
				"-12*b1*R + b1^3 - 4*b2*R - 11*b1^2*b2 - 5*b1*b2^2 - b2^3 - 6*b1*b3*R - 6*b1^3*b3 - 2*b2*b3*R" + 
					"- 4*b1^2*b2*b3 + 2*b1*b2^2*b3 - 6*b1^3*b3^2 - 6*b1^2*b2*b3^2 + 2*b1^3*b3^3",
				"6*R^2 - 7*b1^2*R - 2*b1^4 + 6*b1*b2*R - 8*b1^3*b2 + b2^2*R + 2*b1^2*b2^2 + 3*b3*R^2" + 
					"+ 8*b1^2*b3*R - 5*b1^4*b3 - 2*b1^3*b2*b3 + 3*b1^2*b2^2*b3 + 8*b1^2*b3^2*R + 7*b1^4*b3^2" + 
					"- 5*b1^3*b2*b3^2 + 9*b1^4*b3^3",
				"3*b1*R^2 - 2*b1^3*R - b1^5 + b2*R^2 + 8*b1^2*b2*R - 7*b1^4*b2 + 2*b1*b2^2*R + 5*b1^3*b2^2 + 3*b1^2*b2^3" + 
					"- 2*b1^3*b3*R + 5*b1^5*b3 + 2*b1^2*b2*b3*R - 6*b1^4*b2*b3 - 7*b1^3*b2^2*b3 - 2*b1^3*b3^2*R" + 
					"+ 5*b1^5*b3^2 + 11*b1^4*b2*b3^2 - 6*b1^5*b3^3",
				"-R^3 + 2*b1^2*R^2 + 3*b1^4*R + b1^6 - 2*b1*b2*R^2 + 4*b1^3*b2*R + b1^5*b2 - 3*b1^2*b2^2*R + 7*b1^4*b2^2" + 
					"- b1^3*b2^3 - 2*b1^2*b3*R^2 + 5*b1^4*b3*R + 6*b1^6*b3 + 3*b1^3*b2*b3*R - 12*b1^5*b2*b3" + 
					"+ 2*b1^4*b2^2*b3 - 5*b1^4*b3^2*R + 17*b1^6*b3^2 - 3*b1^5*b2*b3^2 + b1^6*b3^3"};
		final Polynom pCl = polyFact.Create(parser.Parse(ssP, "x"), "x", true);
		display.Display(polyFact.ToSeq(pCl, "x"));
		Polynom pR = math.Replace(pCl, "b3", 1, -3);
		pR = math.Mult(pR, -1, 1);
		display.Display(polyFact.ToSeq(pR, "x"));
		//
		pR = math.Replace(pR, "b1", 1, parser.Parse("2*b1", "x"));
		pR = math.Replace(pR, "b2", 1, parser.Parse("6*b2", "x"));
		pR = math.Replace(pR, "x", 1, parser.Parse("x + b1 + b2", "x"));
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	public void SolveS3P3_Y2Y1() {
		final Polynom p1 = parser.Parse("S^3 + b2*S^2 + b1*S - 3*E2*S - 2*b2*E2 + 3*E3 - 3*R", "S");
		final Polynom p2 = math.Mult(parser.Parse(
				"b2*S^5 + b1*S^4 - 5*b2*E2*S^3 + 5*b2*E3*S^2 - 5*b2*E2*E3 + 4*b1*E3*S - 2*b2^2*E3*S" + 
				"- 4*b1*E2*S^2 + 5*b2*E2^2*S - 2*b1*b2*E2*S" + 
				"+ 2*b1*E2^2 + b2^2*E2^2 + b1^2*E2 - 2*b1*b2^2*E2" + 
				"+ b1*b2*S^3 + b1*b2^2*S^2 + b1^2*b2*S" + 
				"- b2*R*S^2 + 2*b2*R*E2 - b1*R*S - 3*b1*b2*R", "S"), 1);
		final Polynom p3 = math.Mult(parser.Parse(
				"E2^3 - 3*E3*E2*S + 3*E3^2" + 
				"+ b2*S^5 - 5*b2*E2*S^3 + 5*b2*E3*S^2 + 5*b2*E2^2*S - 5*b2*E2*E3" + 
				"+ b1*S^4 - 4*b1*E2*S^2 + 4*b1*E3*S + 2*b1*E2^2" + 
				"- R*S^3 + 3*R*E2*S - 3*R*E3", "S"), 1);
		// final Polynom pRepl1 = parser.Parse("", "R2");
		final Polynom pDiv1 = parser.Parse("3", "S");

		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {p1, p2, p3}, null,
				new String [] {"E3", "E2"},
				new Polynom [] {null, pDiv1, null});
		pR = math.Mult(pR, 1, 1);
		// pR = math.DivAbs(pR, new Monom("x", 2));
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void SolveS3P2_3A() {
		final Polynom p1 = parser.Parse(
				"a1*S^4 - 4*a1*E2*S^2 + 2*a1*ESd + 2*a1*E2^2"
				+ "+ E2^2 - ESd + a2*E2^2 - a2*ESd - R*S^2 + 2*R*E2", "S");
		final Polynom pa2 = parser.Parse("a1 + a2 - 2", "S");
		// 2*(a1 + a2 - 2)*E3*S =
		final Polynom pE3 = parser.Parse("S^4 - R*S^2 - 4*E2*S^2 + a1*E2^2 + a2*E2^2 + 2*E2^2 + 2*R*E2", "S");
		// 2*(a1 + a2 + 1)*E2 =
		final Polynom pE2 = parser.Parse("a1*S^2 + a2*S^2 + S^2 - 3*R", "S");
		final Polynom pa1 = parser.Parse("2*a1 + 2*a2 + 2", "S");
		final Polynom pDiv = parser.Parse("a1*S^2 + a2*S^2 + S^2 - 9*R", "S");
		final Polynom pDiv2 = parser.Parse("a1*S^2 + a2*S^2 + S^2 - R", "S");
		//
		//
		Polynom pR = math.ReplaceOrMult(p1, "ESd", pE3, pa2);
		pR = math.ReplaceOrMult(pR, "E2", pE2, pa1, 2);
		// pR = math.Div(pR, pDiv).key;
		//
		display.Display("S3P2: x^2 + a1*y^2 + a2*z^2");
		display.Display(polyFact.ToSeq(pR, "S"));
		//
		final Polynom pEq3M1 = parser.Parse("a2*S^3 - 3*a2*E2*S + E2*S + 3*a2*E3 - 3*E3 - R*S", "S");
		final Polynom pEq3M2 = parser.Parse("a2*S^3 - 3*a2*E2*S + a1*E2*S + 3*a2*E3 - 3*a1*E3 - R*S", "S");
		final Polynom pEq3Ma = parser.Parse("a1^2 - 2*a1 + 1", "S");
		final Polynom pEq3M0 = parser.Parse("E3*S^3 + E2^3 - 6*E3*E2*S + 9*E3^2", "S");
		final Polynom pa2Full = parser.Parse("2*a1*S + 2*a2*S - 4*S", "S");
		pR = math.Mult(pEq3M1, pEq3M2);
		pR = math.Add(pR, math.Mult(pEq3M0, pEq3Ma));
		pR = math.ReplaceOrMult(pR, "E3", pE3, pa2Full);
		pR = math.ReplaceOrMult(pR, "E2", pE2, pa1);
		pR = math.Div(pR, pDiv).key;
		pR = math.Div(pR, pDiv2).key;
		pR = math.Div(pR, pDiv2).key;
		pR = math.Div(pR, pDiv).key;
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void SolveS3P3_3A() {
		final Polynom pa2 = parser.Parse("a2 - 1", "S");
		final Polynom p31 = parser.Parse(
				"E3*S^5 - 5*E2*E3*S^3 + 7*E3^2*S^2 + E2^2*E3*S + E2^4", "S");
		final Polynom p32 = parser.Parse(
				"a1*S^4 - 4*a1*E2*S^2 + E2*S^2 + 4*a1*E3*S - E3*S + 2*a1*E2^2 - 2*E2^2 - R*S", "S");
		final Polynom p33 = parser.Parse(
				"E2*S^2 - 2*E2^2 - E3*S", "S");
		//
		Polynom pR = math.Pow(p32, 2);
		pR = math.Add(pR, math.Mult(p33, pa2));
		pR = math.Add(pR, math.Mult(p31, math.Pow(pa2, 2)));
		//
		final Polynom pa1 = parser.Parse("a1 + a2 + 1", "S");
		final Polynom p1 = math.Replace(
				parser.Parse("as*S^3 - 3*as*E2*S + 3*as*E3 - 3*R", "S"), "as", pa1);
		final Polynom p2 = parser.Parse(
				"a1*E2^3 + a2*E2^3 - 2*E2^3 + 3*a1*E3^2 + 3*a2*E3^2 + 3*E3^2 - 3*a1*E2*E3*S - 3*a2*E2*E3*S - 12*E2*E3*S"
				+ "+ 9*E2^2*S^2 - 6*E2*S^4 + 3*R*E2*S + 6*E3*S^3 - 3*R*E3 + S^6 - R*S^3", "S");
		//
		final double a1 = -2;
		final double a2 = 2;
		this.SolveS3P3_3A(
				math.Replace(p1, "a1", 1, a1),
				math.Replace(p2, "a1", 1, a1),
				math.Replace(pR, "a1", 1, a1)
				//
				// math.Replace(math.Replace(p1, "a1", 1, a1), "a2", 1, a2),
				// math.Mult(math.Replace(math.Replace(p2, "a1", 1, a1), "a2", 1, a2), 1, 6),
				// math.Replace(math.Replace(pR, "a1", 1, a1), "a2", 1, a2)
				);
	}
	
	public void SolveS3P3_3A(final Polynom p1, final Polynom p2, final Polynom p3) {
		final Polynom pDiv = parser.Parse("a1*S^2 + a2*S^2 + S^2 - 9*R", "S");
		final Polynom pDiv2 = parser.Parse("a1*S^2 + a2*S^2 + S^2 - R", "S");
		final Polynom pDiv3 = parser.Parse("13122", "S"); // 13122 // 157464
		//
		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {p1, p2, p3}, null,
				new String [] {"E3", "E2"} // );
				, new Polynom [] {null, pDiv3, null});
		// pR = math.Mult(pR, 1, 1);
		// pR = math.Div(pR, pDiv).key;
		//
		display.Display("S3P3: x^3 + a1*y^3 + a2*z^3");
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void SolveMixtS3P2_Omega() {
		final Polynom pS1 = parser.Parse("S^4 + E2^2 - 4*E2*S^2 + 6*R3*S", "S");
		final Polynom pS2 = parser.Parse("R3*S^3 - R2*E2*S - 6*E2*R3*S + E2^3 + R2^2 + 3*R2*R3 + 9*R3^2", "S");
		//
		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {pS1, pS2}, null,
				new String [] {"E2"} );
				// , new Polynom [] {null, pDiv3, null});
		// pR = math.Mult(pR, 1, 1);
		// pR = math.Div(pR, pDiv).key;
		//
		display.Display("S3P2 Mixt Omega: x^2 + m*y^2 + m^2*z^2");
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void SolveS3P3_MixtHtSym() {
		final Polynom p1 = parser.Parse("5*S^3 + 9*d*S^2 + 9*d^2*S + R3", "S");
		final Polynom p2 = parser.Parse("9*d*S^2 + 15*d^2*S + 16*d^3 - 2*R3 + 2*R1", "S");
		final Polynom pDiv = parser.Parse("5*S^3 + 16*R1 - 16*R3", "S");
		//
		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {p1, p2}, null,
				new String [] {"d"} );
				// , new Polynom [] {null, pDiv3, null});
		pR = math.Mult(pR, 1, 1458);
		pR = math.Div(pR, pDiv).key;
		//
		display.Display("S3P3: (x+d)^3 + y^3");
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void AsymS3P1() {
		final Polynom p1 = parser.Parse("x + b1*y + b2*z", "x");
		final Polynom p2 = parser.Parse("b1*x + b2*y + z", "x");
		final Polynom p3 = parser.Parse("b2*x + y + b1*z", "x");
		final Polynom p4 = parser.Parse("x + b2*y + b1*z", "x");
		final Polynom p5 = parser.Parse("b2*x + b1*y + z", "x");
		final Polynom p6 = parser.Parse("b1*x + y + b2*z", "x");
		//
		Polynom pR = math.Mult(new Polynom [] {p1, p2, p3, p4, p5, p6});
		display.Display("S3P1: Asymm");
		// display.Display(pR.toString());
		display.Display(math.ExtractMonoms(pR, new Monom("x", 2).Add("y", 2).Add("z", 2)));
	}
	
	public void ClassicS2P2_SimpleEnt() {
		final Polynom p1 = parser.Parse("x^2 + y^2 + b*x + b*y - R1", "x");
		final Polynom p2 = parser.Parse("x^2*y + x*y^2 - R2", "x");
		final Polynom pDiv = parser.Parse("x", "x");
		//
		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {p1, p2}, null,
				new String [] {"y"} );
				// , new Polynom [] {null, pDiv3, null});
		// pR = math.Mult(pR, 1, 1);
		pR = math.Div(pR, pDiv).key;
		//
		display.Display("S2P2 Simple: x*y*(x+y)");
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	public void ClassicS2P2_SimpleEnt2() {
		final Polynom p1 = parser.Parse("x^3 + y^3 + 3*x^2*y + 3*x*y^2 + b2*x*y + b1*x + b1*y - R1", "x");
		final Polynom p2 = parser.Parse("x*y - R2*x^2 - R2*y^2 - 2*R2*x*y", "x");
		final Polynom pDiv = parser.Parse("R2^2", "x");
		//
		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {p1, p2}, null,
				new String [] {"y"} );
				// , new Polynom [] {null, pDiv3, null});
		pR = math.Mult(pR, -1, 1);
		pR = math.Div(pR, pDiv).key;
		//
		display.Display("S2P2 Simple: x*y*(x+y)");
		display.Display(polyFact.ToSeq(pR, "x"));
	}
	
	public void SolveS22P3_Ent() {
		final Polynom p1 = parser.Parse("S^2 - xy - t", "S");
		final Polynom p2 = parser.Parse("S^3 - 3*xy*S + t*S + 2*B*S - 2*R1", "S");
		final Polynom p3 = parser.Parse("xy*B - R2", "S");
		final Polynom p4 = parser.Parse("B*t*S - R3", "S");
		//
		final Polynom pDiv = parser.Parse("2*S*R3", "S");
		//
		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {p1, p4, p3, p2}, null,
				new String [] {"t", "B", "xy"} );
				// , new Polynom [] {null, pDiv3, null});
		pR = math.Mult(pR, -1, 1);
		pR = math.Div(pR, pDiv).key;
		//
		display.Display("S2P2 Simple: B*t*(x+y)");
		display.Display(polyFact.ToSeq(pR, "S"));
	}
	
	public void SolveS2P3_Asym() {
		final Polynom p1 = parser.Parse("S^3 - 3*xy*S - R1 - R2 + R3", "S");
		final Polynom p2 = parser.Parse("3*d*S^2 - 6*d*xy + 3*d^2*S + 2*d^3 + R1 + R2 - 2*R3", "S");
		final Polynom p31 = parser.Parse("9*d^2*(S^2 - 4*xy)", "S");
		final Polynom p32 = parser.Parse("S^2 + 2*d*S + d^2", "S");
		final Polynom p33 = parser.Parse("R1^2 + R2^2 - 2*R1*R2", "S");
		final Polynom p3 = math.Diff(math.Mult(p31, p32), p33);
		final Polynom p4 = parser.Parse("- 12*R1*d^4 - 12*R2*d^4 + 12*R3*d^4 - 2*R1*R2*S - 24*R1*S*d^3 + R1^2*S"
				+ "- 24*R2*S*d^3 + R2^2*S + 24*R3*S*d^3 - 12*R1*S^2*d^2 - 12*R2*S^2*d^2 + 12*R3*S^2*d^2 + 3*S^3*d^4"
				+ "+ 6*S^4*d^3 + 6*S^5*d^2", "S");
		//
		final Polynom pDiv = parser.Parse("", "S");
		//
		display.Display("\nS2P3 Asym: (x+d)^3 + y^3 = R1\n");
		//
		Polynom pR = polyFact.ClassicPolynomial(
				new Polynom [] {p1, p2, p3, p2}, null,
				new String [] {"xy", null, "d"} );
				// , new Polynom [] {null, new Polynom(3, "S"), null});
		// pR = math.Mult(pR, -1, 1);
		// pR = math.Div(pR, pDiv).key;
		//
		display.Display("S2P3 Asym: (x+d)^3 + y^3 = R1");
		display.Display(polyFact.ToSeq(pR, "S"));
	}
}
