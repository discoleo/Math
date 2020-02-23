/*
 * Minimalistic GUI
 * to test the functionality
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
package gui;

import data.Polynom;
import io.Parser;
import math.MathTools;
import math.PolyFactory;

public class BaseGui implements IDisplay {
	
	// minimalistic GUI

	protected final Parser parser = new Parser();
	protected final MathTools math = new MathTools();
	protected final PolyFactory polyFact = new PolyFactory(math);
	
	// ++++++++ MEMBER FUNCTIONS ++++++++++
	
	/* (non-Javadoc)
	 * @see gui.IDisplay#Display(java.lang.String)
	 */
	@Override
	public void Display(final String sTitle) {
		System.out.println(sTitle);
	}
	/* (non-Javadoc)
	 * @see gui.IDisplay#Display(data.Polynom)
	 */
	@Override
	public void Display(final Polynom p) {
		System.out.println(p.toString());
	}
	
	public void TestFractionDecomposition() {
		final TestFractions test = new TestFractions(parser, math, polyFact, this);
		test.Test();
	}
}
