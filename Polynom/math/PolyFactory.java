package math;

import java.util.Map;
import java.util.Vector;

import data.Monom;
import data.PolyResult;
import data.Polynom;

public class PolyFactory extends BaseFactory {
	
	public PolyFactory() {
		this(new MathTools());
	}
	public PolyFactory(final MathTools math) {
		super(math);
	}
	
	// +++++++++ MEMBER FUNCTIONS ++++++++++

	public PolyResult Create(final Polynom pBase, final int nOrder) {
		return this.Create(pBase, nOrder, "x");
	}
	public PolyResult Create(final Polynom polynomRoot, final int nOrder, final String sVar) {
		// coefficientii polinomului
		final Vector<Polynom> vCoeff = new Vector<> ();
		
		Polynom poly = new Polynom(1, polynomRoot.MainRootName());
		// Coeff 1 for x^nOrder
		vCoeff.add(new Polynom(1, polynomRoot.MainRootName()));
		
		for(int nIteratie=1; nIteratie < nOrder; nIteratie++) {
			poly = math.Mult(poly, polynomRoot);
			// termenii rationali
			final Polynom polyRational = GetRational(poly, nOrder);
			final Polynom polyCoeff = math.Mult(polyRational, nOrder, nIteratie);
			poly = math.Diff(poly, polyCoeff);
			vCoeff.add(math.Mult(polyCoeff, -1, 1));
		}
		// coeff b0
		poly = math.Mult(poly, polynomRoot);
		poly = math.Mult(poly, -1, 1);
		// evental: testat daca b0 e rational
		vCoeff.add(poly);
		
		return new PolyResult(this.Create(vCoeff, sVar),
				polynomRoot, vCoeff, nOrder);
	}

	public PolyResult CreateFromUnity(final Polynom polynomRoot, final int nOrder, final String sVar) {
		return this.CreateFromUnity(polynomRoot, nOrder - 1, nOrder, sVar);
	}
	public PolyResult CreateFromUnity(final Polynom polynomRoot, final int nMaxRootPow, final int nOrder, final String sVar) {
		final String sUnity = "m"; // TODO
		Polynom polyRez = null;
		final int [] iiCoeffBase = new int [] {0, 1};
		
		for(int iOrder=1; iOrder <= nMaxRootPow; iOrder++) {
			final Polynom pCopyRoot = new Polynom(polynomRoot, sVar);
			final Polynom pRoot;
			if(iOrder == 0) {
				pRoot = pCopyRoot; // NOT used!
			} else {
				final Monom mUnity = new Monom(sUnity, iOrder);
				final Polynom pUnity = new Polynom(mUnity, 1.0d, sUnity);
				final Polynom pRootExpansion = math.Replace(pCopyRoot, polynomRoot.sRootName, pUnity);
				pRoot = math.Replace(pRootExpansion, sUnity, nOrder, 1.0d);
			}
			// Term = (x - root)
			Polynom polyTerm = new Polynom(sVar);
			polyTerm.Add(iiCoeffBase);
			polyTerm = math.Diff(polyTerm, pRoot);
			
			if(iOrder == 1) {
				polyRez = polyTerm;
			} else {
				polyRez = math.Mult(polyRez, polyTerm);
				polyRez = math.Replace(polyRez, sUnity, nOrder, 1.0d);
			}
		}

		return new PolyResult(polyRez,
				polynomRoot, null, nOrder); // TODO
	}
	
	public Polynom CreateSimple(final int nOrder, final String sVar, final String sCoeff,
			final boolean addB0, final boolean doIncCoeff) {
		// x^n + b[i]*x^i + b0
		// x^n + b*x^i + b; [doIncCoeff == false]
		final Polynom p = new Polynom(sVar);
		final int iStart = addB0 ? 0 : 1;
		
		for(int iPow=iStart; iPow < nOrder; iPow++) {
			final String sCoeff_i = doIncCoeff ? sCoeff + iPow : sCoeff;
			final Monom m = new Monom(sCoeff_i, 1);
			if(iPow > 0) {
				m.Add(sVar, iPow);
			}
			p.Add(m, 1);
		}
		p.Add(new Monom(sVar, nOrder), 1);
		return p;
	}
	
	public Polynom CreateAlternating(final String [] sVars, final int idMinus, final int iPow1, final int iPow2) {
		final Polynom p = new Polynom();
		
		for(int i=0; i < sVars.length; i++) {
			final int iCoeff;
			final Monom m;
			if(i == idMinus) {
				m = new Monom(sVars[i], iPow2);
				iCoeff = -1;
			} else {
				m = new Monom(sVars[i], iPow1);
				iCoeff = 1;
			}
			p.Add(m, iCoeff);
		}
		return p;
	}
	
	// ++++ Polynomial Systems ++++
	
	public Vector<Polynom> FromPolynom(final String [] sVars, final Polynom p, final String sVarInitial) {
		// sVarInitial is replaced;
		final Vector<Polynom> vP = new Vector<> ();
		
		for(final String sVar : sVars) {
			final Polynom pR = math.Replace(p, sVarInitial, 1, sVar);
			vP.add(pR);
		}
		
		return vP;
	}
	
	// +++ Symmetric Systems +++
	
	public Vector<Polynom> SymmetricSimple(final String [] sVars, final int nOrder) {
		final Vector<Polynom> vP = new Vector<> ();
		// TODO: generalize
		vP.add(this.SimplePow(sVars, nOrder));
		vP.add(this.SimpleE2(sVars, 1));
		vP.add(this.SimpleProduct(sVars, 1));
		return vP;
	}
	
	public Polynom SimplePow(final String [] sVars, final int nOrder) {
		// TODO: base-Variable
		// x^n + y^n + z^n
		final Polynom p = new Polynom();
		for(final String sV : sVars) {
			p.Add(new Monom(sV, nOrder), 1);
		}
		return p;
	}
	public Polynom SimpleE2(final String [] sVars, final int iPow) {
		// TODO: base-Variable
		final Polynom p = new Polynom();
		for(int id1=0; id1 < sVars.length - 1; id1++) {
			for(int id2 = id1 + 1; id2 < sVars.length; id2++) {
				final Monom m = new Monom(sVars[id1], iPow)
						.Add(sVars[id2], iPow);
				p.Add(m, 1);
			}
		}
		return p;
	}
	public Polynom SimpleProduct(final String [] sVars, final int nOrder) {
		final Monom m = new Monom();
		for(final String sV : sVars) {
			m.Add(sV, nOrder);
		}
		// TODO: base-Variable
		final Polynom p = new Polynom();
		p.Add(m, 1);
		return p;
	}
	
	// +++ Asymmetric +++

	public Vector<Polynom> AsymmetricSimple(final String [] sVars, final int iPow1, final int iPow2) {
		// x*y^n + y*z^n + z*x^n
		final Vector<Polynom> vP = new Vector<> ();
		// TODO: generalize
		vP.add(this.AsymmetricE2(sVars, iPow1, iPow2));
		vP.add(this.SimpleE2(sVars, 1));
		vP.add(this.SimpleProduct(sVars, 1));
		return vP;
	}
	public Vector<Polynom> AsymmetricDual(final String [] sVars, final int iPow1, final int iPow2) {
		// x*y^n + y*z^n + z*x^n
		// x^n*y + y^n*z + z^n*x
		final Vector<Polynom> vP = new Vector<> ();
		// TODO: generalize
		vP.add(this.AsymmetricE2(sVars, iPow1, iPow2));
		vP.add(this.AsymmetricE2(sVars, iPow2, iPow1));
		vP.add(this.SimpleProduct(sVars, 1));
		return vP;
	}
	public Vector<Polynom> HtAsymmetric(final String [] sVars, final String sCoef, final int iPow1, final int iPow2) {
		// x^p1 + b*y^p2
		final Vector<Polynom> vP = new Vector<> ();
		for(int i=0; i < sVars.length - 1; i++) {
			final Polynom p = new Polynom();
			p.Add(new Monom(sVars[i], iPow1), 1);
			p.Add(new Monom(sVars[i+1], iPow2).Add(sCoef, 1), 1);
			vP.add(p);
		}
		final Polynom p = new Polynom();
		p.Add(new Monom(sVars[sVars.length - 1], iPow1), 1);
		p.Add(new Monom(sVars[0], iPow2).Add(sCoef, 1), 1);
		vP.add(p);
		return vP;
	}
	public Vector<Polynom> AsymmetricLinked(final String [] sVars, final String sCoeff,
			final int iPow1, final int iPow2) {
		// P[1]: x^p1 + b*x
		// P[2]: y^p1 + x*y
		// P[i]: z^p1 + y*z
		final Vector<Polynom> vP = new Vector<> ();
		vP.add(this.CreateSimple(iPow1, sVars[0], sCoeff, false, true));
		// TODO: iPow2;
		// "Linked" coefficients;
		// TODO: for(...)
		vP.add(this.CreateSimple(iPow1, sVars[1], sVars[0], false, false));
		vP.add(this.CreateSimple(iPow1, sVars[2], sVars[1], false, false));
		return vP;
	}
	public Vector<Polynom> AsymmetricLinkedSym(final String [] sVars, final String sCoeff,
			final int iPow1, final int iPow2) {
		// P[1]: x^p1 + b*x
		// P[2]: y^p1 + x*y
		// P[i]: z^p1 + x*z
		final Vector<Polynom> vP = new Vector<> ();
		vP.add(this.CreateSimple(iPow1, sVars[0], sCoeff, false, true));
		// TODO: iPow2;
		// same sVars[0] in all subsequent;
		// TODO: for(...)
		vP.add(this.CreateSimple(iPow1, sVars[1], sVars[0], false, false));
		vP.add(this.CreateSimple(iPow1, sVars[2], sVars[0], false, false));
		return vP;
	}
	
	// Sum & Diff-Type
	public Vector<Polynom> AsymmetricSum(final String [] sVars, final int iPow1, final int iPow2) {
		return this.AsymmetricSum(sVars, iPow1, iPow2, new int [] {1, 1});
	}
	public Vector<Polynom> AsymmetricDiff(final String [] sVars, final int iPow1, final int iPow2) {
		return this.AsymmetricSum(sVars, iPow1, iPow2, new int [] {1, -1});
	}
	public Vector<Polynom> AsymmetricSum(final String [] sVars, final int iPow1, final int iPow2, final int [] iCoeff) {
		// asymmetric Sum/Difference
		// b[0]*x^p1 + b[1]*y^p2, ...
		final Vector<Polynom> vP = AsSumPart(new Vector<> (), sVars, iPow1, iPow2, iCoeff);
		// last Polynom
		final Polynom p = new Polynom();
		p.Add(new Monom(sVars[sVars.length - 1], iPow1), iCoeff[0]);
		p.Add(new Monom(sVars[0], iPow2), iCoeff[1]);
		vP.add(p);
		return vP;
	}
	public Vector<Polynom> AsymmetricSumDiff(final String [] sVars, final int iPow1, final int iPow2) {
		// asymmetric Sum/Difference
		// TODO: b[0]*x^p1 + b[1]*y^p2, ...
		final Vector<Polynom> vP = AsSumPart(new Vector<> (), sVars, iPow1, iPow2, new int [] {1, 1});
		// last Polynom
		final Polynom p = new Polynom();
		p.Add(new Monom(sVars[sVars.length - 1], iPow1), 1);
		p.Add(new Monom(sVars[0], iPow2), -1); // last is different
		vP.add(p);
		return vP;
	}
	public Vector<Polynom> AsSumPart(final Vector<Polynom> vP,
			final String [] sVars, final int iPow1, final int iPow2, final int [] iCoeff) {
		// asymmetric Sum/Difference: without the last polynom
		// b[0]*x^p1 + b[1]*y^p2, ...
		for(int id=0; id < sVars.length - 1; id++) {
			final Polynom p = new Polynom();
			p.Add(new Monom(sVars[id], iPow1), iCoeff[0]);
			p.Add(new Monom(sVars[id + 1], iPow2), iCoeff[1]);
			vP.add(p);
		}
		return vP;
	}
	public Vector<Polynom> AsymmetricDiff3(final String [] sVars, final int iPow1, final int iPow2) {
		// asymmetric Difference
		final Vector<Polynom> vP = new Vector<> ();
		for(int id=0; id < sVars.length - 2; id++) {
			final Polynom p = new Polynom();
			p.Add(new Monom(sVars[id], iPow1), 1);
			p.Add(new Monom(sVars[id + 1], iPow2), 1);
			p.Add(new Monom(sVars[id + 2], iPow2), -1);
			vP.add(p);
		}
		// last 2 Polynoms
		Polynom p = new Polynom();
		p.Add(new Monom(sVars[sVars.length - 2], iPow1), 1);
		p.Add(new Monom(sVars[sVars.length - 1], iPow1), 1);
		p.Add(new Monom(sVars[0], iPow2), -1);
		vP.add(p);

		p = new Polynom();
		p.Add(new Monom(sVars[sVars.length - 1], iPow1), 1);
		p.Add(new Monom(sVars[0], iPow1), 1);
		p.Add(new Monom(sVars[1], iPow2), -1);
		vP.add(p);
		return vP;
	}

	public Vector<Polynom> AsymmetricAlternating(final String [] sVars, final int iPow1, final int iPow2) {
		final Vector<Polynom> vP = new Vector<> ();
		// first: only Sum
		vP.add(this.CreateAlternating(sVars, -1, iPow1, iPow2));
		for(int i=1; i < sVars.length; i++) {
			vP.add(this.CreateAlternating(sVars, i, iPow1, iPow2));
		}
		return vP;
	}
	
	public Polynom AsymmetricE2(final String [] sVars, final int iPow1, final int iPow2) {
		// TODO: base-Variable
		final Polynom p = new Polynom();
		for(int id=0; id < sVars.length - 1; id++) {
			final Monom m = new Monom(sVars[id], iPow1)
					.Add(sVars[id + 1], iPow2);
			p.Add(m, 1);
		}
		// last Monom
		final Monom m = new Monom(sVars[sVars.length - 1], iPow1)
				.Add(sVars[0], iPow2);
		p.Add(m, 1);
		return p;
	}

	// ++++ helper ++++
	
	public Polynom GetRational(final Polynom p, final int nOrder) {
		// variabila care contine radicali
		final String sVarName = p.MainRootName();
		
		final Polynom polyRational = new Polynom(sVarName);
		for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
			// citim puterea variabilei relevante
			final Integer nPow = entryM.getKey().get(sVarName);
			if(nPow == null) {
				// free term (este termenul liber)
				polyRational.put(entryM.getKey(), entryM.getValue());
			} else if(nPow % nOrder == 0) {
				// put(): no need for Add();
				polyRational.put(entryM.getKey(), entryM.getValue());
			}
		}
		return polyRational;
	}
}
