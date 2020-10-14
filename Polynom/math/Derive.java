/*
 * Various MathTools for Polynomials:
 * Differentiate & generate ODEs;
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
package math;

import java.util.Map;

import data.Monom;
import data.Pair;
import data.PolySeq;
import data.Polynom;


public class Derive {
	
	protected final MathTools math;
	protected final PolyFactory polyFact;
	
	public Derive(final MathTools math, final PolyFactory polyFact) {
		this.math = math;
		this.polyFact = polyFact;
	}

	public Polynom ODE(final Polynom p, final String sX, final String sY) {
		final int iPow = math.MaxPow(p, sY); // TODO: also the Coeff needed;
		final Polynom pDRez = math.Mult(this.D(p, sY, sX), new Monom(sY, 1));
		// TODO: evaluate alternative?
		// final Monom mY5 = new Monom("y", 5);
		// pRez.remove(mY5);
		// final Polynom pY5 = math.Mult(pRez, -1);
		// pDRez = math.Replace(pDRez, "y", 5, pY5);
		return math.Diff(pDRez, math.Mult(p, new Monom("dy", 1), iPow));
	}
	
	public Polynom D(final Polynom p, final String sY, final String sX) {
		return this.D(polyFact.ToSeq(p, sY), sX);
	}
	
	public Polynom D(final PolySeq seq, final String sVar) {
		final String sY = seq.GetVar();
		final String dy = "d" + seq.GetVar();
		Polynom pRez = new Polynom(dy);
		
		for(final Map.Entry<Integer, Polynom> entryP : seq.entrySet()) {
			// D(P) * y^iPow
			final Polynom pDP = this.D(entryP.getValue(), sVar);
			final int iPow = entryP.getKey();
			if(iPow == 0) {
				pRez = math.Add(pRez, pDP);
				continue;
			}
			final Monom mY = new Monom(sY, iPow);
			pRez = math.Add(pRez, math.Mult(pDP, mY));
			// P * D(y^iPow)
			final Polynom pBase = math.Mult(entryP.getValue(), iPow);
			final Monom mdY = new Monom(sY, iPow - 1).Add(dy, 1);
			pRez = math.Add(pRez, math.Mult(pBase, mdY));
		}
		
		return pRez;
	}
	
	public Polynom D(final Polynom p, final String sVar) {
		final Polynom pRez = new Polynom(sVar);
		
		for(final Map.Entry<Monom, Double> entryP : p.entrySet()) {
			final Pair<Monom, Integer> dm = this.D(entryP.getKey(), sVar);
			if(dm != null) {
				pRez.Add(dm.key, entryP.getValue() * dm.val);
			}
		}
		
		return pRez;
	}
	
	public Pair<Monom, Integer> D(final Monom m, final String sVar) {
		final Integer iPow = m.get(sVar);
		if(iPow == null) { return null; }
		
		final Monom dm = new Monom(m);
		dm.remove(sVar);
		dm.Add(sVar, iPow - 1);
		return new Pair<> (dm, iPow);
	}
}
