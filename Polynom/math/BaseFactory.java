/*
 * Various MathTools for Polynomials
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
package math;

import java.util.Map;
import java.util.Vector;

import data.Monom;
import data.PolySeq;
import data.Polynom;
import data.PowGrade;

public class BaseFactory {
	
	protected final MathTools math;
	
	public final String ROOT_UNITY = "m";
	
	public BaseFactory() {
		this(new MathTools());
	}
	public BaseFactory(final MathTools math) {
		this.math = math;
	}
	
	// +++++++++++++++++++++++++++++
	
	public Polynom PolyX() {
		return new Polynom(new Monom("x", 1), 1, "x");
	}
	public Polynom Simple1(final Double dFreeTerm) {
		return new Polynom(new Monom("x", 1), 1, "x")
				.Add(new Monom(), dFreeTerm);
	}
	public Polynom SimpleP1(final String sB1, final String sB0) {
		return new Polynom(new Monom("x", 1)
				.Add(sB1, 1), 1, "x")
				.Add(new Monom(sB0, 1), 1);
	}
	
	// ++++ Roots of Unity ++++
	public Vector<Monom> GetRootsUnity(final int nOrder) {
		final Vector<Monom> mRoots = new Vector<> ();
		for(int n=1; n < nOrder; n++) {
			mRoots.add(new Monom(ROOT_UNITY, n));
		}
		
		return mRoots;
	}
	public Vector<Polynom> GetConjRootsUnity(final int nOrder) {
		final Vector<Monom> mRoots = this.GetRootsUnity(nOrder);
		final Vector<Polynom> vRoots = new Vector<> ();
		
		// from: 0 ... (n-2)
		final int nLast = nOrder - 2;
		final boolean isEven = (nOrder % 2 == 0);
		// even: skip root "-1"
		final int size = (isEven? nLast : nOrder) / 2;
		
		for(int n=0; n < size; n++) {
			final Polynom p1 = PolyX();
			p1.Add(mRoots.get(n), -1);
			final Polynom p2 = PolyX();
			p2.Add(mRoots.get(nLast - n), -1);
			//
			final Polynom p = math.Replace(math.Mult(p1, p2), ROOT_UNITY, nOrder, 1);
			vRoots.add(p);
			// System.out.println(p.toString());
		}
		
		return vRoots;
	}
	// +++ Fraction Decomposition
	public Polynom BuildFractionUnity(final int nOrder) {
		final Vector<Polynom> vTerms = this.GetConjRootsUnity(nOrder);
		final String sB1 = "a";
		final String sB0 = "b";
		
		final boolean isEven = (nOrder % 2 == 0);
		
		final Polynom pFirstTerm;
		final Polynom p1 = Simple1(-1d);
		if(isEven) {
			final Polynom p2 = Simple1(1d);
			pFirstTerm = math.Mult(p1, p2);
		} else {
			pFirstTerm = p1;
		}
		
		final Polynom pFirstCoeff;
		if(isEven) {
			// a0*x + b0
			pFirstCoeff = SimpleP1(sB1 + 0, sB0 + 0);
		} else {
			// b0
			pFirstCoeff = new Polynom(new Monom(sB0 + 0, 1), 1, "x");
		}
		
		final Polynom pSum = new Polynom();
		
		for(int n= -1; n < vTerms.size(); n++) {
			final int id = n + 1;
			final Polynom pFractCoeff = (n >= 0) ?
					math.Mult(SimpleP1(sB1 + id, sB0 + id), pFirstTerm) : pFirstCoeff;
			
			final Polynom pFractionTerm =
					math.Mult(
						math.Replace(this.MultTerms(vTerms, n), ROOT_UNITY, nOrder, 1),
						pFractCoeff);
			// System.out.println(math.ReplaceSeq(pFractionTerm, ROOT_UNITY, nOrder - 1, -1));
			math.AddInPlace(pSum, pFractionTerm);
		}
		
		final PowGrade powGrade = new PowGrade(nOrder, 1);
		// return math.ReplaceSeq(pSum, ROOT_UNITY, powGrade);
		return math.ReplaceSeqMult(
				math.ReplaceSeqMult(pSum, ROOT_UNITY, powGrade, 3),
				ROOT_UNITY, powGrade, 3);
	}
	
	// ++++ Conjugated Roots ++++
	
	public Polynom GetConjRoot(final int nOrder, final int iRoot) {
		// p
		final Monom mP = new Monom("p",1);
		if(iRoot > 0) {
			mP.Add(ROOT_UNITY, iRoot);
		}
		final Monom mQ = new Monom("q",1);
		if(iRoot > 0) {
			mQ.Add(ROOT_UNITY, nOrder - iRoot);
		}
		final Polynom pRoot = new Polynom();
		pRoot.Add(mP, 1.0d);
		pRoot.Add(mQ, 1.0d);
		return pRoot;
	}
	public Vector<Polynom> GetConjRoots(final int nOrder) {
		final Vector<Polynom> vRoots = new Vector<> ();
		for(int n=0; n < nOrder; n++) {
			vRoots.add(this.GetConjRoot(nOrder, n));
		}
		
		return vRoots;
	}
	public Vector<Polynom> GetConjRootFactors(final int nOrder) {
		final Vector<Polynom> vRoots = this.GetConjRoots(nOrder);
		final Monom mX = new Monom("x", 1);
		
		for(int n=0; n < vRoots.size(); n++) {
			final Polynom p = math.MultInPlace(vRoots.get(n), -1);
			p.Add(new Monom(mX), 1);
			vRoots.set(n, p); // should be already changed in place
		}
		
		return vRoots;
	}
	// multiply ALL terms
	public Polynom MultTerms(final Vector<Polynom> vTerms, final int nExclude) {
		Polynom pResult = null;
		
		for(int n=0; n < vTerms.size(); n++) {
			if(n == nExclude) { continue; }
			if(pResult == null) {
				pResult = vTerms.get(n);
			} else {
				pResult = math.Mult(pResult, vTerms.get(n));
			}
		}
		return pResult;
	}
	
	public Polynom BuildFractionConj(final int nOrder) {
		final Vector<Polynom> vTerms = this.GetConjRootFactors(nOrder);
		final String sCoeff = "a";
		
		final Polynom pSum = new Polynom();
		
		final Monom mReduce = new Monom("p", 1).Add("q", 1);
		
		for(int n=0; n < vTerms.size(); n++) {
			final Monom mCoeff = new Monom(sCoeff + n, 1);
			final Polynom pFraction =
					math.Replace(
					math.Replace(
							math.Mult(
									this.MultTerms(vTerms, n), mCoeff),
							mReduce, "c"),
							ROOT_UNITY, nOrder, 1);
			math.AddInPlace(pSum, pFraction);
		}
		
		final PowGrade grade = new PowGrade(nOrder, 1);
		return math.ReplaceSeq(
				math.ReplaceSeq(pSum, ROOT_UNITY, grade), ROOT_UNITY, grade);
	}
	
	// +++ helper +++
	
	public PolySeq ToSeq(final Polynom p, final String sVar) {
		final PolySeq pSeq = new PolySeq(sVar);
		
		for(final Map.Entry<Monom, Double> entryP : p.entrySet()) {
			final Integer iPow = entryP.getKey().getOrDefault(sVar, 0);
			
			final Polynom pSubSeq = pSeq.GetNew(iPow);
			final Monom m = new Monom(entryP.getKey());
			m.remove(sVar); // remove the variable
			pSubSeq.Add(m, entryP.getValue());
		}
		
		return pSeq;
	}
}
