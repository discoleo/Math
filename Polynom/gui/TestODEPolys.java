package gui;

import data.Polynom;
import io.Parser;
import math.MathTools;
import math.PolyFactory;
import math.Solver;

public class TestODEPolys {

	protected final Parser parser;
	protected final MathTools math;
	protected final PolyFactory polyFact;
	
	protected final Solver solver;
	
	protected IDisplay display;
	
	protected final boolean bTEST_CONJ_FRACTIONS = true;
	
	public TestODEPolys(final Parser parser, final MathTools math, final PolyFactory polyFact,
			final IDisplay display) {
		this.parser = parser;
		this.math = math;
		this.polyFact = polyFact;
		//
		this.display = display;
		//
		this.solver = new Solver(math);
	}
	
	public void Test() {
		// this.Generate2Log();
		this.Generate2Sin();
	}
	
	public void Generate2Log() {
		final Polynom py0n = parser.Parse("y - x", "y");
		final Polynom py0p = parser.Parse("y + x", "y");
		//
		final Polynom pD1yn = parser.Parse("dy - 1", "y");
		final Polynom pD1yp = parser.Parse("dy + 1", "y");
		//
		final Polynom pD2yn = parser.Parse("dy2*y - dy2*x + dy^2 - 1", "y");
		final Polynom pD2yp = parser.Parse("dy2*y + dy2*x + dy^2 - 1", "y");
		final Polynom pY2 = parser.Parse("dy^2 - y*dy + x - 1", "y");
		//
		Polynom pR1 = math.Mult(new Polynom [] {py0n, pD1yp, pD2yp});
		Polynom pR2 = math.Mult(new Polynom [] {py0p, pD1yn, pD2yn});
		Polynom pR = math.Diff(pR1, pR2);
		//
		display.Display("ODE: log() * log()");
		display.Display(polyFact.ToSeq(pR, "y"));
		//
		pR1 = math.Mult(new Polynom [] {math.Mult(pD1yp, 2), pY2});
		pR2 = math.Mult(new Polynom [] {py0p, pD2yn});
		pR = math.Add(pR1, pR2);
		display.Display(polyFact.ToSeq(pR, "y"));
	}
	
	public void Generate2Sin() {
		//
		final Polynom pD1yn = parser.Parse("p*dy - p + x*dp", "y");
		final Polynom pD1yp = parser.Parse("x*p*dy - dp", "y");
		//
		final Polynom pD2yn = parser.Parse("x*dy2 - dy^2 + 2*dy", "y");
		final Polynom pD2yp = parser.Parse("dy2 + x*dy^2", "y");
		//
		Polynom pR1 = math.Mult(new Polynom [] {pD1yp, pD2yp});
		Polynom pR2 = math.Mult(new Polynom [] {pD1yn, pD2yn});
		Polynom pR = math.Diff(pR1, pR2);
		//
		display.Display("ODE: sin() + cos()");
		display.Display(polyFact.ToSeq(pR, "dy"));
	}
}
