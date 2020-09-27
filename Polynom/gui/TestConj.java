package gui;

import data.Monom;
import data.Polynom;
import data.PowGrade;

public class TestConj extends BaseGui {

	public TestConj(final BaseGui base) {
		super(base);
	}
	
	public void Test() {
		this.TestConj6();
	}
	
	public void TestConj6() {
		this.Display("\nP6");
		
		final Polynom p1 = parser.Parse("x^2 + s2*p^2*x + s2*q^2*x + s1*p*x + s1*q*x + s0*x + p + q", "x");
		final Polynom p2 = parser.Parse("x^2 + s2*p^2*m^2*x + s2*q^2*m*x + s1*p*m*x + s1*q*m^2*x + s0*x + p*m + q*m^2", "x");
		final Polynom p3 = parser.Parse("x^2 + s2*p^2*m*x + s2*q^2*m^2*x + s1*p*m^2*x + s1*q*m*x + s0*x + p*m^2 + q*m", "x");
		
		final Monom mPQ = new Monom().Add("p", 1).Add("q", 1);
		final PowGrade powGrade = new PowGrade(3, 1);
		
		Polynom pR = math.Mult(p1, p2);
		pR = math.Mult(pR, p3);
		pR = math.Replace(pR, "m", 3, 1);
		pR = math.Replace(pR, mPQ, "c");
		pR = math.ReplaceSeqMult(pR, "m", powGrade, 1);
		this.Display(polyFact.ToSeq(pR, "x"));
	}
}
