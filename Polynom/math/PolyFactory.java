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

	public PolyResult Create(final Polynom polynomRoot, final int nOrder) {
		return this.Create(polynomRoot, nOrder, "x");
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
