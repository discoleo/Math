/*
 * Minimalistic Solver
 * for Liniar Systems
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
package math;

import java.util.Iterator;
import java.util.Map;

import data.Monom;
import data.Pair;
import data.PolySeq;
import data.Polynom;
import data.SolutionPoly;
import data.SolutionSystem;

public class Solver {
	
	protected final MathTools math;
	
	public Solver(final MathTools math) {
		this.math = math;
	}
	
	// TODO:
	// - filter non-unique variables;
	// - ...;
	
	public SolutionPoly SimpleLinearZero(final PolySeq pSeq, final String sVarKnown) {
		final SolutionPoly sol = new SolutionPoly();
		{
			// 1st known variable
			final Polynom pSol = new Polynom();
			pSol.Add(new Monom(sVarKnown, 1), 1);
			sol.add(new Pair<> (sVarKnown, pSol));
		}
		
		final SolutionSystem system = this.ToSolutionSystem(pSeq);
		this.SimpleLinearZero(system, sol);
		
		if( ! system.isEmpty()) {
			this.SolveLinearCombine(system, sol);
		}
		if( ! system.isEmpty()) {
			this.SimpleLinearZero(system, sol);
		}
		System.out.println(system.toString());
		return sol;
	}

	public void SimpleLinearZero(final SolutionSystem system, final SolutionPoly sol) {
		int nposVar = 0;
		while(nposVar < sol.size()) {
			// move free Terms
			this.Move(system);
			
			final Pair<String, Polynom> pairSolution = sol.get(nposVar);
			// System.out.println("Var = " + pairSolution.key);
			
			for(int nposSeq=0; nposSeq < system.size(); nposSeq++) {
				final Pair<Polynom, Polynom> pairPoly = system.get(nposSeq);
				this.Move(pairPoly.key, pairPoly.val, pairSolution.key, pairSolution.val);
				// only 1 Variable: Solve
				if(pairPoly.key.size() == 1) {
					final Map.Entry<Monom, Double> entrySolved = pairPoly.key.firstEntry();
					final Monom mSolved = entrySolved.getKey();
					if(mSolved.size() == 1) {
						final String sVarSolved = mSolved.firstKey();
						// TODO: assumes pow == 1;
						if(entrySolved.getValue() == 1) {
							sol.add(new Pair<String, Polynom>(sVarSolved, pairPoly.val));
						} else {
							sol.add(new Pair<String, Polynom>(sVarSolved,
									math.MultInPlace(pairPoly.val, 1, entrySolved.getValue())));
						}
						// remove solved var
						system.remove(nposSeq);
						nposSeq --;
					}
				}
			}
			nposVar ++;
			// System.out.println(system.toString());
		}
	}
	
	public void SolveLinearCombine(final SolutionSystem system, final SolutionPoly sol) {
		for(int nFirst=0; nFirst < system.size() - 1; nFirst++) {
			final Pair<Polynom, Polynom> pair1 = system.get(nFirst);
			for(int nSecond = nFirst + 1; nSecond < system.size(); nSecond++) {
				final Pair<Polynom, Polynom> pair2 = system.get(nSecond);
				final Polynom pSum = math.Add(pair1.key, pair2.key);
				if(pSum.size() == 1) {
					final Polynom pRez = math.Add(pair1.val, pair2.val);
					
					final Map.Entry<Monom, Double> entrySolved = pSum.firstEntry();
					final Monom mSolved = entrySolved.getKey();
					if(mSolved.size() == 1) {
						final String sVarSolved = mSolved.firstKey();
						// TODO: assumes pow == 1;
						if(entrySolved.getValue() == 1) {
							sol.add(new Pair<String, Polynom>(sVarSolved, pRez));
						} else {
							sol.add(new Pair<String, Polynom>(sVarSolved,
									math.MultInPlace(pRez, 1, entrySolved.getValue())));
						}
						// remove solved var
						system.remove(nSecond);
						nSecond --;
					}
				}
			}
		}
	}
	
	// ++++ helper ++++
	
	public SolutionSystem ToSolutionSystem(final PolySeq pSeq) {
		final SolutionSystem system = new SolutionSystem();
		// TODO: sorting descending size
		for(final Polynom p : pSeq.values()) {
			system.add(new Pair<> (new Polynom(p), new Polynom(p.sRootName)));
		}
		
		return system;
	}
	
	public void Move(final SolutionSystem system) {
		// moves free Terms
		final Monom mFree = new Monom();
		for(final Pair<Polynom, Polynom> pair : system) {
			final Double dCoeff = pair.key.get(mFree);
			if(dCoeff != null) {
				pair.key.remove(mFree);
				pair.val.Add( - dCoeff);
			}
		}
	}
	public boolean Move(final Polynom pSrc, final Polynom pDest, final String sVar, final Polynom pReplacement) {
		final Iterator<Map.Entry<Monom, Double>> itSrc = pSrc.entrySet().iterator();
		
		boolean hasChanged = false;
		while(itSrc.hasNext()) {
			final Map.Entry<Monom, Double> entry = itSrc.next();
			if(entry.getKey().containsKey(sVar)) {
				final Monom mKnown = entry.getKey();
				final double dCoeff = entry.getValue();
				itSrc.remove();
				final boolean isOneVar = (mKnown.size() == 1);
				
				final Polynom pFromMonom = new Polynom(mKnown, dCoeff, pSrc.sRootName);
				final Polynom pRez = math.Replace(pFromMonom, sVar, pReplacement);
				// if(sVar.equals("b0")) {
				//	System.out.println(pRez.toString() + ", " + pReplacement.toString());
				// }
				
				if(isOneVar) {
					math.AddInPlace(pDest, math.Mult(pRez, -1));
				} else {
					// TODO: if all variables are solved, move to pDest
					math.AddInPlace(pSrc, pRez);
					hasChanged = true;
					System.out.println("Add back: should NOT happen!");
				}
			}
		}
		return hasChanged;
	}
}
