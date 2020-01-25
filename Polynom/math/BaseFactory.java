package math;

import java.util.Vector;

public class BaseFactory {
	
	protected final MathTools math;
	
	public final String ROOT_UNITY = "m";
	
	public BaseFactory() {
		this(new MathTools());
	}
	public BaseFactory(final MathTools math) {
		this.math = math;
	}
	
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
		
		return math.ReplaceSeq(
				math.ReplaceSeq(pSum, ROOT_UNITY, nOrder - 1, -1), ROOT_UNITY, nOrder - 1, -1);
	}
}
