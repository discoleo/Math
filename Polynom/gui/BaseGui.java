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

import java.util.Vector;

import data.PolySeq;
import data.Polynom;
import io.Parser;
import math.MathTools;
import math.PolyFactory;

public class BaseGui implements IDisplay {
	
	// minimalistic GUI

	protected final Parser parser;
	protected final MathTools math;
	protected final PolyFactory polyFact;
	
	public BaseGui() {
		parser = new Parser();
		math = new MathTools();
		polyFact = new PolyFactory(math);
	}
	public BaseGui(final BaseGui base) {
		parser = base.parser;
		math = base.math;
		polyFact = base.polyFact;
	}
	
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
	@Override
	public void Display(final PolySeq p) {
		System.out.println(p.Print());
	}
	@Override
	public void Display(final Vector<Polynom> vP) {
		for(final Polynom p : vP) {
			this.Display(p);
		}
	}
	
	// +++ Other +++
	
	public void TestFractionDecomposition() {
		final TestFractions test = new TestFractions(parser, math, polyFact, this);
		test.Test();
	}
}
