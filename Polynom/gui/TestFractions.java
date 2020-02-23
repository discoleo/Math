/*
 * Test Fraction Decomposition
 * of Polynomial Fractions
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
package gui;

import data.PolySeq;
import data.Polynom;
import data.PowGrade;
import io.Parser;
import math.MathTools;
import math.PolyFactory;
import math.Solver;


public class TestFractions {

	protected final Parser parser;
	protected final MathTools math;
	protected final PolyFactory polyFact;
	
	protected final Solver solver;
	
	protected IDisplay display;
	
	protected final boolean bTEST_CONJ_FRACTIONS = false;
	
	public TestFractions(final Parser parser, final MathTools math, final PolyFactory polyFact,
			final IDisplay display) {
		this.parser = parser;
		this.math = math;
		this.polyFact = polyFact;
		//
		this.display = display;
		//
		this.solver = new Solver(math);
	}
	
	// +++++++++++ MEMBER FUNCTIONS ++++++++++++

	public void Test() {
		display.Display("Testing Fractions:");

		// Roots of Unity: 1/(x^n - 1)
		final int nPow = 11;
		this.TestFraction(nPow);

		// Fractions 2:
		if( ! bTEST_CONJ_FRACTIONS) {
			return;
		}
		final int nPowConj = 11;
		this.TestConjFractions(nPowConj);
	}
	
	public void TestFraction(final int nPow) {
		final PowGrade powGrade = new PowGrade(nPow, 1);
		Polynom p_test = polyFact.BuildFractionUnity(nPow);
		p_test = p_test.Add(-1);
		
		display.Display(
				polyFact.ToSeq(p_test, "x").Print());
		final PolySeq seq = polyFact.ToSeq(
				polyFact.ReplaceSubSequence(p_test, new String [] {"a", "b"}, powGrade), "x");
		display.Display(seq.Print());
		display.Display(
				solver.SimpleLinearZero(seq, "b0").toString());
	}
	
	public void TestConjFractions(final int nPow) {
		final Polynom pFraction = polyFact.BuildFractionConj(nPow);
		display.Display("\nFraction:");
		display.Display(pFraction);
	}
	
	public void ValidateRationalization() {
		// TODO:
		Polynom p_test = math.Mult(
				parser.Parse("m^3 + m^4 - m^1 - m^6", "m"),
				parser.Parse("m^2 + m^5 - m^3 - m^4", "m"));
		p_test = math.Add(p_test,
				math.Mult(
						parser.Parse("m^1 + m^6 - m^3 - m^4", "m"),
						parser.Parse("-m^3 - m^4 + m^1 + m^6", "m")));
		p_test = math.Replace(p_test, "m", 7, 1);
		display.Display(p_test);
		p_test = math.ReplaceSeq(p_test, "m", new PowGrade(7, 1));
		display.Display(p_test);
	}
}
