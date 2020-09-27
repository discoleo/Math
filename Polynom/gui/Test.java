package gui;

import data.Monom;
import data.PolyResult;
import data.Polynom;
import data.PowGrade;

public class Test extends BaseGui {
	
	// private String sInput = "(x^3 + 3*pr)*((x^3 + 3*pr)^4 - 5*c*(x^3 + 3*pr)^2";
	
	private String sInput = "(x^2 - 8*(0.125 x^3 - 3)) + x^3 - 2*s1s2(3*x - 4x) =  0.125*s1s2*x";

	public Polynom Start() {
		return parser.Parse(sInput, "x");
	}
	public void TestPoly() {
		// this.TestMath();
		this.TestGeneration();
		// this.TestReduction();
		// this.TestRootDecomp();
		this.TestFractionDecomposition();
		// this.TestRationalization();
		P5_3();
		//
		new TestDerived(parser, math, polyFact, this).Test();
		// new TestConj(this).Test();
	}
	
	public void P5_3() {
		final Polynom p1 = parser.Parse("5*x^12 + 30*d*x^7 - 30*d^2*x^2", "x");
		final Polynom p2 = parser.Parse("11*x^10 + 26*d*x^5 + 4*d^2", "x");
		final Polynom p3 = parser.Parse("x^5 + 8*d", "x");
		//
		Polynom r = math.Mult(p1, p1);
		r = math.Mult(r, p1);
		r = math.Mult(r, new Monom("x", 1));
		r = math.Mult(r, 5);
		//
		Polynom r2 = math.Mult(p2, p2);
		r2 = math.Mult(r2, p1);
		r2 = math.Mult(r2, p3);
		r2 = math.Mult(r2, 3);
		//
		r = math.Add(r, r2);
		//
		r = math.Mult(p1, p1);
		r = math.Mult(r, p2);
		r = math.Mult(r, new Monom("x", 1));
		r = math.Mult(r, 15);
		//
		r2 = math.Mult(p2, p2);
		r2 = math.Mult(r2, p2);
		r2 = math.Mult(r2, p3);
		//
		r = math.Add(r, r2);
		// Complete Polynomial P_5_3
		final Polynom pF_1 = parser.Parse("- 576*d^7*x^2 - 13734*d^6*x^7 - 534*d^5*x^12 - 11205*d^4*x^17"
				+ "+ 21840*d^3*x^22 + 13203*d^2*x^27 + 2262*d*x^32 + 122*x^37 + 31250*C^2*x^7", "x");
		final Polynom pF_2 = parser.Parse("128*d^7 + 16012*d^6*x^5 + 78342*d^5*x^10 - 78335*d^4*x^15"
				+ "+ 42380*d^3*x^20 + 79191*d^2*x^25 + 19834*d*x^30 + 1364*x^35", "x");
		//
		r = math.Mult(pF_1, pF_1);
		r = math.Mult(r, new Monom("x", 1));
		r = math.Mult(r, 125);
		//
		r2 = math.Mult(pF_2, pF_2);
		r2 = math.Mult(r2, p3);
		r2 = math.Mult(r2, -1);
		//
		r = math.Add(r, r2);
		//
		System.out.println(r.toString());
	}
	
	public void TestReduction() {
		System.out.println("Testing Replacements:");
		final Polynom p1 = parser.Parse("k^13 + k^8 + 3*k^7 + 2*k^6 - k^5 + k^3 + 1", "x");

		Polynom p_test = math.Replace(p1, "k", 5, 3);
		System.out.println(p_test.toString());
		
		int iPow = math.Contains(new Monom("x", 5), new Monom("x", 2));
		System.out.println("Monom Power = " + iPow);
		
		iPow = math.Contains(
				new Monom("x", 5).Add("k", 2),
				new Monom("x", 2).Add("k", 1));
		System.out.println("Monom Power = " + iPow);
		
		final Polynom p2 = parser.Parse("x^5 - p q x^3 + p^2*q^2*x - 2*d", "x");
		System.out.println("Reduced:\n" +
				math.Replace(p2, new Monom("p", 1).Add("q", 1), "c"));
	}
	public void TestMath() {
		// ++++ Math operations
		final Polynom p1 = parser.Parse("x^2 + 3*x + 2", "x");
		
		// Test 1: scalar mult
		// TODO: NOT in-place!
		Polynom p_test = math.Mult(p1, 4, 2);
		System.out.println(p_test.toString());
		
		// Test 2: mult 2 polys
		final Polynom p2 = parser.Parse("x^3 - 3*x + 2", "x");
		
		p_test = math.Mult(p1, p2);
		System.out.println(p_test.toString());

		// Test 3: mult 2 polys with term cancelation
		final Polynom p3 = parser.Parse("x - 1", "x");
		final Polynom p4 = parser.Parse("x^2 + x + 1", "x");

		p_test = math.Mult(p3, p4);
		System.out.println(p_test.toString());
	}

	public void TestRootDecomp() {
		// clasa care genereaza polinoame
		// final PolyFactory polyFact = new PolyFactory(math);
		
		final int nOrder = 6;
		final Polynom pBase = parser.Parse("x^6 + b5*x^5 + b4*x^4 + b3*x^3 + b2*x^2 + b1*x + b0", "x");
		final Polynom pRoot = parser.Parse("s5*k^5 + s4*k^4 + s3*k^3 + s2*k^2 + s1*k + s0", "k");
		
		Polynom pReplaceRoot = math.Replace(pBase, "x", pRoot);
		pReplaceRoot = math.Replace(pReplaceRoot, "k", nOrder, "K");
		
		final int powMonom = 5;
		final int powSubSeq = 0;
		// final int [] powM = new int [] {0, 1, 2, 3, 4};
		// final int [] powM = new int [] {0, 2, 4, 1, 3};
		// final int [] powM = new int [] {0, 3, 1, 4, 2};
		final int [] powM = new int [] {0, 4, 3, 2, 1};
		//
		Polynom pT4 = math.SubSequence(pReplaceRoot, "k", powSubSeq);
		pT4 = math.Replace(pT4, "k", 1, 1);
		System.out.println("\nRoot Decomposition:");
		System.out.println(pT4.toString());
		System.out.println(pT4.size());
		System.out.println();
		
		final Monom mR = new Monom("k", 1).Add("m", powMonom);
		Polynom pReplaceRoot2 = math.Replace(pBase, "x",
				math.Replace(
				math.Replace(pRoot, "k", mR), "m", nOrder, 1));
		pReplaceRoot2 = math.Replace(pReplaceRoot2, "m", nOrder, 1);
		pReplaceRoot2 = math.Replace(pReplaceRoot2, "k", nOrder, "K");
		
		Polynom pT42 = math.SubSequence(pReplaceRoot2, "k", powSubSeq);
		pT42 = math.Replace(pT42, "k", 1, 1);
		System.out.println(pT42.toString());
		System.out.println(pT42.size());
		pT42 = math.SubSequence(pT42, "m", powM[powSubSeq]); // 4
		pT42 = math.Replace(pT42, "m", 1, 1);
		System.out.println(pT42.toString());
		System.out.println(pT42.size());
		pT42 = math.Diff(pT4, pT42);
		System.out.println(pT42.size());
		
		final int [] powSub = math.SubSequencePow(pReplaceRoot, pReplaceRoot2, nOrder, "k", "m");
		System.out.println("\nPower of Unity:");
		for(int i : powSub) {
			System.out.print("" + i + ", ");
		}
	}
	public void TestRationalization() {
		Polynom p_test = math.Mult(
				parser.Parse("k^4 - k^3 - 3*k + 1", "k"),
				parser.Parse("161*k^4 + 85*k^3 + 165*k^2 - 21*k + 73", "k"));
		p_test = math.Replace(p_test, "k", 5, "K");
		System.out.println("\nValidation:");
		System.out.println(p_test.toString());
	}
	public void TestGeneration() {
		// ++++ Roots
		System.out.println("\nTesting roots:");
		// Example 1
		// final Polynom pR_7 = parser.Parse("k^6 - k^4 + s3*k^3 + s2*k^2 + s1*k", "k");
		// final Polynom pR_7 = parser.Parse("s^2*k^3 + 2*s*k^2 - 2*k", "k");
		final Polynom pR_7 = parser.Parse("s*k^4 - s*k^3 + k^2 + k", "k");
		
		PolyResult pRez = polyFact.Create(pR_7, 5);
		Polynom p_test = math.Replace(pRez.GetPoly(), "k", 5, "K");
		System.out.println(p_test.toString());
		System.out.println(p_test.size());
		System.out.println(pRez.toR());
		
		// Example 2
		final Polynom pR_7s4 = parser.Parse("k^6 + s4*k^4 + s3*k^3 + s2*k^2 + s1*k", "k");
		pRez = polyFact.Create(pR_7s4, 7);
		p_test = pRez.GetPoly();
		System.out.println(p_test.toString());
		System.out.println(p_test.size());

		// Example 3: correlated coeffs
		final Polynom pR_nx3 = parser.Parse("k^4 - 2*s3*k^3 + s3^2*k^2", "k");
		pRez = polyFact.Create(pR_nx3, 5);
		p_test = pRez.GetPoly();
		System.out.println("P5: k^4 - s3*k^3 + s3^2*k^2 + s3^3*k");
		System.out.println(p_test.toString());
		System.out.println(p_test.size());

		// Example 4: much simpler P5
		final Polynom pR3 = parser.Parse("k^3 - 3*k^2 - 2*k", "k");
		pRez = polyFact.Create(pR3, 5);
		p_test = pRez.GetPoly();
		System.out.println("P5: simple P5");
		System.out.println(p_test.toString());
		System.out.println(p_test.size());
		// Test Ex4( m^3 + v2*m^2 + v1*m )
		final Polynom pReplace3 = parser.Parse("m^3 + v2*m^2 + v1*m", "m");
		p_test = math.Replace(p_test, "x", pReplace3);
		System.out.println(p_test.toString());
		System.out.println(p_test.size());

		final Polynom pR3_k = parser.Parse("k^3 + s2*k^2 + s1*k", "k");
		p_test = polyFact.Create(pR3_k, 5).GetPoly();
		System.out.println("P5: r = k^3 + s2*k^2 + s1*k");
		System.out.println(p_test.toString());
		p_test = math.Replace(p_test, "x", pReplace3);
		System.out.println("Generic Replacement");
		System.out.println(p_test.toString());
		System.out.println(p_test.size());
		final Polynom pR3_2 = new Polynom(0, "x");
		pR3_2.Add(new int [] {2348, -20, 140, 30, 0, 1});
		System.out.println(pR3_2.toString());
		p_test = math.Replace(pR3_2, "x", pReplace3);
		System.out.println(p_test.toString());
		System.out.println(p_test.size());
		// special k^3
		final Polynom pR3_ks = parser.Parse("k^3 + s2*k^2 - s2^2*k", "k");
		p_test = polyFact.Create(pR3_ks, 5).GetPoly();
		System.out.println("P5 special: r = k^3 + s2*k^2 - s2^2*k");
		System.out.println(p_test.toString());
		
		final Polynom pR_xy = new Polynom(0, "x");
		pR_xy.put(new Monom("x", 1), 1d);
		pR_xy.put(new Monom("y", 1), 1d);
		//
		pR3_k.clear();
		pR3_k.put(new Monom("k", 3), 1d);
		pR3_k.put(new Monom("k", 1), -1d);
		pRez = polyFact.Create(pR3_k, 5);
		p_test = pRez.GetPoly();
		System.out.println(p_test.toString());
		System.out.println(p_test.size());
		p_test = math.Replace(p_test, "x", pR_xy);
		System.out.println(p_test.toString());
		
		// Example 5: P5
		final Polynom pR5 = parser.Parse("-s^2*s*k^4 - s^2*k^3 - s*k^2 + k", "k");
		pRez = polyFact.Create(pR5, 5, "x");
		p_test = pRez.GetPoly();
		System.out.println("\nP5: special");
		System.out.println(p_test.toString());
		
		// Example 5: P5 Cube // k^3 + 3*s*k^2 + 3*s^2*k
		final Polynom pR5p3 = parser.Parse("k^3 + 3*s*k^2 + 3*s^2*k", "k");
		pRez = polyFact.Create(pR5p3, 5, "x");
		p_test = pRez.GetPoly();
		System.out.println("\nP5 Cube:");
		System.out.println(p_test.toString());
		
		// Example 5: P5 Sq3 //
		final Polynom pR5p2 = parser.Parse("k^4 + 2*s1*k^3 + 2*s0*k^2 + s1^2*k^2 + 2*s1*s0*k", "k");
		pRez = polyFact.Create(pR5p2, 5, "x");
		p_test = pRez.GetPoly();
		System.out.println("\nP5 Sq3:");
		System.out.println(p_test.toString());
		
		// Example 5: P5 Cube //
		final Polynom pR5p20 = parser.Parse("3*k^4 + 6*s1*k^3 - 2*s1^2*k^2 + 3*s1^2*k^2 - 2*s1^3*k", "k");
		pRez = polyFact.Create(pR5p20, 5, "x");
		p_test = pRez.GetPoly();
		System.out.println("\nP5 Cube:");
		System.out.println(p_test.toString());
		
		// Example 6: P5 3 Terms //
		final Polynom pR5p3T = parser.Parse("k + B*k^-1 - B^-1*k^3", "k");
		pRez = polyFact.Create(pR5p3T, 5, "x");
		p_test = pRez.GetPoly();
		System.out.println("\nP5 Sq:");
		System.out.println(p_test.toString());
		
		// Example 7: P10 - Terms //
		final Polynom pR10 = parser.Parse("k^5 + k^4 + k^3 + k^2 + k + 1", "k");
		pRez = polyFact.Create(pR10, 10, "x");
		p_test = pRez.GetPoly();
		System.out.println("\nP10:");
		System.out.println(p_test.toString());
		System.out.println(math.Replace(p_test, "k", 10, "K").toString());
		System.out.println(math.Replace(p_test, "k", 10, -2).toString());
		
		// Roots of Unity
		final Polynom pRUnity = parser.Parse("k^2 + a*k", "k");
		pRez = polyFact.CreateFromUnity(pRUnity, 7, "x");
		p_test = pRez.GetPoly();
		System.out.println("\nP Unity:\n" + p_test);
		p_test = math.ReplaceSeq(p_test, "m", new PowGrade(7, 1));
		System.out.println("\nP Unity:\n" + p_test);

		final Polynom pRUnity3 = parser.Parse("a*k^3 + k^2 + c*k", "k");
		pRez = polyFact.CreateFromUnity(pRUnity3, 7, "x");
		p_test = pRez.GetPoly();
		System.out.println("\nP Unity:\n" + p_test);
		p_test = math.ReplaceSeq(p_test, "m", new PowGrade(7, 1));
		System.out.println("\nP Unity:\n" + p_test);

		// final Polynom pRUnity4 = parser.Parse("a*k^10 + a*k + b*k^9 + b*k^2 + c*k^8 + c*k^3", "k");
		final Polynom pRUnity4 = parser.Parse("a*k^12 + a*k + b*k^11 + b*k^2 + c*k^10 + c*k^3", "k");
		pRez = polyFact.CreateFromUnity(pRUnity4, 6, 13, "x"); // 5, 11
		p_test = pRez.GetPoly();
		System.out.println("\nP Unity:\n" + p_test);
		p_test = math.ReplaceSeq(p_test, "m", new PowGrade(13, 1)); // 11
		System.out.println("\nP Unity:\n" + p_test);

		// Example 3: Simple root
		final Polynom pR_s2 = parser.Parse("k^4 - 3*k^2 - k", "k");
		pRez = polyFact.Create(pR_s2, 5);
		p_test = math.Replace(pRez.GetPoly(), "k", 5, 2);
		final Polynom pR2 = new Polynom(p_test, "R");
		System.out.println("\nP5: k^4 - 3*k^2 - k; // old k^2 + 2*s1*k");
		System.out.println(p_test.toString());
		System.out.println(p_test.size());
		// R0
		final Polynom pR_RootBase = parser.Parse("s4*k^4 + s3*k^3 + s2*k^2 + s1*k", "r");
		final Polynom pR_Root = math.Replace(pR_RootBase, "k", 1, "r");
		p_test = math.Replace(math.Replace(pR2, "x", pR_Root), "r", 5, "R");
		System.out.println(p_test.toString());
		// R1
		final Monom mRoot = new Monom("r", 1).Add("w", 1);
		final Polynom pR_Root1 = math.Replace(pR_RootBase, "k", new Polynom(mRoot, 1, "r"));
		p_test = math.Replace(math.Replace(math.Replace(pR2, "x", pR_Root1), "w", 5, 1), "r", 5, "R");
		System.out.println(p_test.toString());
		// R2
		mRoot.Add("w", 1);
		final Polynom pR_Root2 = math.Replace(math.Replace(pR_RootBase, "k", new Polynom(mRoot, 1, "r")), "w", 5, 1);
		p_test = math.Replace(math.Replace(math.Replace(pR2, "x", pR_Root2), "w", 5, 1), "r", 5, "R");
		System.out.println(p_test.toString());
	}

	public static void main(String[] args) {
		final Test test = new Test();
		final Polynom pTest = test.Start();
		System.out.println(pTest.toString());
		//
		test.TestPoly();
    }
}
