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
import data.Pair;
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

	public Polynom Create(final Vector<Polynom> vPCoef, final String sVarName) {
		// create a multivariable polynomial from its parametric coefficients;
		Polynom polyRez =  new Polynom(sVarName);
		int nPow = vPCoef.size() - 1;
		for(final Polynom p : vPCoef) {
			final Polynom pRez = math.AddVar(p, sVarName, nPow);
			polyRez = math.Add(polyRez, pRez);
			nPow --;
		}
		
		return polyRez;
	}
	
	// ++++ Derived Polynom ++++

	public Polynom Derived(final Polynom pBase, final int [] iCoeffDerivedRoot, final Polynom pDerived) {
		// TODO: derive poly from the Roots;
		final Polynom pRoot = new Polynom(iCoeffDerivedRoot, "k");
		return this.Derived(pBase, pRoot, pDerived);
	}
	public Polynom Derived(final Polynom pBase, final Polynom pRoot, final Polynom pDerived) {
		final Polynom pSubst =
				math.Replace(
				math.Replace(pDerived, pDerived.sRootName, pRoot), pRoot.sRootName, 1, pBase.sRootName);
		System.out.println(pSubst.toString());
		final Polynom pDiv1 = math.Gcd(pSubst, pBase);
		return pDiv1;
		// TODO: return also Remainder;
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
	// +++ Fraction Decomposition: Roots of Unity
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
		final int iMaxMissing = (nOrder + 1) / 3;
		// return math.ReplaceSeq(pSum, ROOT_UNITY, powGrade);
		return math.ReplaceSeqMult(
				math.ReplaceSeqMult(pSum, ROOT_UNITY, powGrade, iMaxMissing),
				ROOT_UNITY, powGrade, iMaxMissing);
	}
	
	// ++++ Conjugated Roots ++++
	// Cardan-type Polynomials
	
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
		// P = x - p*m^j - q*m^(n - j);
		final Vector<Polynom> vRoots = this.GetConjRoots(nOrder);
		final Monom mX = new Monom("x", 1);
		
		for(int n=0; n < vRoots.size(); n++) {
			final Polynom p = math.MultInPlace(vRoots.get(n), -1);
			p.Add(new Monom(mX), 1);
			vRoots.set(n, p); // should be already changed in place
		}
		
		return vRoots;
	}
	public Vector<Polynom> GetConjRootFactors_2(final int nOrder) {
		// P = (x - p*m^j - q*m^(n - j)) * (x - p*m^(n - j) - q*m^j);
		final Vector<Polynom> vRoots = new Vector<Polynom> ();
		
		final Vector<Polynom> vPolyRoots = this.GetConjRootFactors(nOrder);
		// TODO: nOrder == EVEN;
		final int nLen = nOrder / 2;
		// simple Root
		vRoots.add(vPolyRoots.get(0));
		
		for(int n=1; n <= nLen; n++) {
			final Polynom p =
					math.Replace(
							math.Mult(vPolyRoots.get(n), vPolyRoots.get(nOrder - n)),
							ROOT_UNITY, nOrder, 1);
			vRoots.add(p);
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
		// +++ simple Terms
		// final Vector<Polynom> vTerms = this.GetConjRootFactors(nOrder);
		// +++ conjugate Terms
		final Vector<Polynom> vTerms = this.GetConjRootFactors_2(nOrder);
		
		final boolean IS_CONJ = true;
		if(IS_CONJ) {
			return this.BuildFractionConj(vTerms, nOrder);
		}
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
	public Polynom BuildFractionConj(final Vector<Polynom> vTerms, final int nOrder) {
		final String sB1 = "a";
		final String sB0 = "b";
		
		final Polynom pSum = new Polynom();
		
		final Monom mReduce = new Monom("p", 1).Add("q", 1);
		final Monom mCoeff0 = new Monom(sB0 + 0, 1);
		final Polynom pCoeff0 = new Polynom(mCoeff0, 1, "x");
		
		for(int n=0; n < vTerms.size(); n++) {
			final Polynom pF = (n > 0) ? SimpleP1(sB1 + n, sB0 + n) : pCoeff0;
			final Polynom pFraction =
					math.Replace(
					math.Replace(
							math.Mult(
									this.MultTerms(vTerms, n), pF),
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
	
	// +++ Replace Sub-Sequences +++
	
	public Polynom ReplaceSubSequence(final Polynom p, final String [] sVarNames, final PowGrade powGrade) {
		Polynom pRez = new Polynom(p);
		final int iPowHalf = powGrade.iPow / 2;
		final String sROOTS_UNITY = "m";
		
		for(int iPow = 1; iPow <= iPowHalf; iPow++) {
			for(final String sVarName : sVarNames) {
				final String sVarNew = sVarName + "S" + iPow;
				pRez = math.ReplaceSeqByVar(pRez,
						this.SubSequence(sVarName, sROOTS_UNITY, iPow, powGrade), sVarNew, powGrade);
			}
		}
		
		final Polynom pMissed = math.FindAll(pRez, sROOTS_UNITY);
		if(pMissed.size() > 0) {
			// Error: NOT everything replaced
			for(int iPow = 1; iPow <= iPowHalf; iPow++) {
				for(final String sVarName : sVarNames) {
					final String sVarNew = sVarName + "S" + iPow;
					final Pair<Polynom, Polynom> pairMatch = math.Match(pMissed,
							this.SubSequence(sVarName, sROOTS_UNITY, iPow, powGrade),
							sVarNew, powGrade);
					final Polynom pMatch = pairMatch.key;
					if(pMatch != null && pMatch.size() > 0) {
						System.out.println("Found Missing Terms");
						System.out.println(pMatch.toString());
						// Cancel the old Monoms
						math.AddInPlace(pRez, pMatch);
						// add the Replacement
						math.AddInPlace(pRez, math.Mult(pairMatch.val, new Monom(sVarNew, 1)));
						
						for(final Monom mRemove : pMatch.keySet()) {
							pMissed.remove(mRemove);
						}
					}
				}
			}
		}
		
		// Zero: optimization at the end
		for(final String sVarName : sVarNames) {
			final String sVarNew = sVarName;
			pRez = math.ReplaceSeqByVar(pRez,
					this.SubSequence(sVarName, sROOTS_UNITY, 0, powGrade), sVarNew, powGrade);
		}
		
		return math.Simplify(pRez, powGrade);
	}
	
	// Generate Sub-Sequences
	public Polynom SubSequence(final String sCoeff, final String sUnity, final PowGrade powGrade) {
		return this.SubSequence(sCoeff, sUnity, 1, powGrade);
	}
	public Polynom SubSequence(final String sCoeff, final String sUnity, final int iOffset, final PowGrade powGrade) {
		if(iOffset == 0) {
			return this.SubSequenceZero(sCoeff, powGrade);
		}
		final int iPowHalf = powGrade.iPow / 2;
		final Polynom pRez = new Polynom(sUnity);
		
		for(int iPowBase = 1; iPowBase <= iPowHalf; iPowBase++) {
			final int iPowOffset = (iPowBase * iOffset) % powGrade.iPow;
			final String sCoeffI = sCoeff + iPowBase;
			final Monom m1 = new Monom(sCoeffI, 1).Add(sUnity, iPowOffset);
			final Monom m1Inv = new Monom(sCoeffI, 1).Add(sUnity, powGrade.iPow - iPowOffset);
			pRez.Add(m1, 1);
			pRez.Add(m1Inv, 1);
		}
		
		return pRez;
	}
	public Polynom SubSequenceZero(final String sCoeff, final PowGrade powGrade) {
		final int iPowHalf = powGrade.iPow / 2;
		final Polynom pRez = new Polynom();
		
		for(int iPowBase = 1; iPowBase <= iPowHalf; iPowBase++) {
			final String sCoeffI = sCoeff + iPowBase;
			final Monom m1 = new Monom(sCoeffI, 1);
			pRez.Add(m1, 1);
		}
		
		return pRez;
	}
}
