package math;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import data.Monom;
import data.P2RootObj;
import data.Pair;
import data.PolyResult;
import data.PolySeq;
import data.Polynom;

public class PolyFactory extends BaseFactory {
	
	public PolyFactory() {
		this(new MathTools());
	}
	public PolyFactory(final MathTools math) {
		super(math);
	}
	
	// +++++++++ MEMBER FUNCTIONS ++++++++++
	
	public Polynom Substitute(final Polynom p, final P2RootObj root, final String sVar, final int iDiv) {
		final Polynom pR = math.ReplaceOrMult(p, sVar, root.pRoot, root.pCoeff);
		return this.Square(pR, root.pSqrt, root.sSqrt, iDiv);
	}
	public Polynom Square(final Polynom p, final Polynom pSqrt, final String sVar, final int iDiv) {
		// square a polynomial containing a sqrt;
		final PolySeq seq = this.ToSeq(p, sVar);
		Polynom p0 = new Polynom(p.sRootName);
		Polynom p1 = new Polynom(p.sRootName);
		for(final Map.Entry<Integer, Polynom> entryP : seq.entrySet()) {
			if(entryP.getKey() == 0) {
				p0 = math.Add(p0, entryP.getValue());
			} else if(entryP.getKey() % 2 == 0) {
				final int iPowHalf = entryP.getKey() / 2;
				p0 = math.Add(p0, math.Mult(entryP.getValue(), new Monom(sVar, iPowHalf)));
			} else if(entryP.getKey() == 1) {
				p1 = math.Add(p1, entryP.getValue());
			} else {
				final int iPowHalf = (entryP.getKey() - 1) / 2;
				p1 = math.Add(p1, math.Mult(entryP.getValue(), new Monom(sVar, iPowHalf)));
			}
		}
		Polynom pR = math.Mult(math.Pow(p1, 2), pSqrt);
		pR = math.Diff(pR, math.Pow(p0, 2));
		final int iGcd = math.GcdNum(pR, 0);
		this.Display("GCD = " + iGcd);
		if(iGcd > 1) { pR = math.Mult(pR, 1, iGcd); }
		pR = math.Replace(pR, sVar, pSqrt);
		pR = math.Mult(pR, 1, iDiv);
		return pR;
	}
	public P2RootObj SolveP2(final Polynom p, final String sVar) {
		final PolySeq seq = this.ToSeq(p, sVar);
		if(seq.lastKey() > 2) { Display("ERROR: NOT an order 2 polynomial!"); }
		final P2RootObj root = new P2RootObj();
		root.sSqrt = sVar + "_sq";
		root.pRoot = math.Add(math.Mult(seq.get(1), -1),
				new Monom(root.sSqrt, 1), -1);
		root.pSqrt = math.Diff(math.Pow(seq.get(1), 2),
				math.Mult(math.Mult(seq.get(0), seq.get(2)), 4));
		root.pCoeff = math.Mult(seq.get(2), 2);
		return root;
	}
	
	public Polynom [] Cycle(final Polynom p, final String [] sVars) {
		final Polynom [] pAll = new Polynom [sVars.length];
		pAll[0] = p;
		
		for(int id=1; id < sVars.length; id++) {
			pAll[id] = new Polynom(p.sRootName);
			for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
				final Monom m = new Monom(entryM.getKey());
				for(int id0 = 0; id0 < sVars.length; id0++) {
					final int idNew = (id0 + id) % sVars.length;
					final Integer iPow = entryM.getKey().get(sVars[id0]);
					if(iPow != null && iPow != 0) {
						m.Put(sVars[idNew], iPow);
					} else {
						m.remove(sVars[idNew]);
					}
				}
				pAll[id].Add(m, entryM.getValue());
			}
		}
		return pAll;
	}

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
	
	public Polynom Create(final Polynom [] p, final String sVar, final boolean isDesc) {
		Polynom pR = new Polynom(sVar);
		
		for(int i=0; i < p.length; i++) {
			final int iPow = isDesc ? p.length - i - 1 : i;
			if(iPow != 0) {
				pR = math.Add(pR, math.Mult(p[i], new Monom(sVar, iPow)));
			} else {
				pR = math.Add(pR, p[i]);
			}
		}
		
		return pR;
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
	
	public void Display(final PolySeq seq) {
		if(seq.size() != 2) {
			System.out.println(seq.toString());
		} else {
			System.out.println(seq.get(0).toString());
			System.out.println(seq.get(1).toString());
		}
	}
	public void Display(final Polynom p) {
		System.out.println(p.toString());
	}
	public void Display(final String str) {
		System.out.println(str);
	}

	public Polynom ClassicPolynomial(final Polynom p1, final Polynom p2, final Polynom pDiv) {
		return this.ClassicPolynomial(p1, p2, pDiv, "y");
	}
	public Polynom ClassicPolynomial(final Polynom p1, final Polynom p2, final Polynom pDiv, final String sVar) {
		final String sVarDiv = "pDiv";
		final Monom mDiv = new Monom(sVarDiv, 1);
		
		Polynom pR = math.GcdExtract(p1, p2, sVar);
		final PolySeq seq = this.ToSeq(pR, sVar);
		
		final Polynom pY = math.Mult(math.Mult(seq.get(0), mDiv), -1);
		final Polynom pDivY = seq.get(1);
		
		pR = math.Replace(p1, sVar, pY);
		// Debug:
		System.out.println("\nClassic Poly:\nExtract P(y, x):");
		this.Display(seq);
		// this.Display(this.ToSeq(pR, "x"));
		this.Display(pR.toString());
		
		final int iMaxPow = math.MaxPow(pR, sVarDiv);
		if(iMaxPow > 0) {
			pR = math.Mult(pR, new Monom("pDivInv", iMaxPow));
			mDiv.Add("pDivInv", 1);
			pR = math.Replace(pR, mDiv, "Identity");
			pR = math.Replace(pR, "Identity", 1, 1);
			System.out.println("\nDIV routine: R\n" + pR.toString());
			System.out.println("\nDIV routine: Div\n" + pDivY.toString());
			pR = math.Replace(pR, "pDivInv", pDivY);
			System.out.println("\nFinished replacement.\n");
		}

		if(pDiv != null) {
			final Pair<Polynom, Polynom> pdDiv = math.DivRobust(pR, pDiv);
			pR = pdDiv.key;
			System.out.println(pdDiv.val.toString());
		}
		return pR;
	}
	public Polynom ClassicPolynomial(final Polynom [] p, final Polynom pDiv, final String [] sVar) {
		return this.ClassicPolynomial(p, pDiv, sVar, null);
	}
	public Polynom ClassicPolynomial(final Polynom [] p, final Polynom pDiv, final String [] sVar,
			final Polynom [] pReduce) {
		return this.ClassicPolynomial(p, pDiv, sVar, pReduce, false);
	}
	public Polynom ClassicPolynomial(final Polynom [] p, final Polynom pDiv, final String [] sVar,
			final Polynom [] pReduce, final boolean doCoeffs) {
		final String sVarDiv = "pDiv";
		final Monom mDiv = new Monom(sVarDiv, 1);
		final Monom mIdent = new Monom(mDiv).Add("pDivInv", 1);
		
		for(int id=0; id < p.length - 1; id++) {
			if(sVar[id] == null) { continue; }
			Polynom pR = math.GcdExtract(p[id], p[id + 1], sVar[id]);
			final PolySeq seq = this.ToSeq(pR, sVar[id]);
			// final PolySeq seq;
			// if(id == p.length - 3) seq = math.Mult(this.ToSeq(pR, sVar[id]), 1, 32);
			// else seq = this.ToSeq(pR, sVar[id]);
			// Debug
			this.Display("Polynoms: p" + id);
			this.Display(p[id]);
			final int iGcd = math.GcdNum(seq);
			if(iGcd > 1) {
				math.DivInPlace(seq, iGcd);
			}
			// this.Display("GCD = " + iGcd);
			// this.Display(p[id+1]);
			this.Display(seq);
			if(pReduce != null && pReduce[id] != null) {
				final Pair<Polynom, Polynom> ppDiv1 = math.Div(seq.get(0), pReduce[id]);
				this.Display("Div Reduce:\n" + ppDiv1.val.toString());
				seq.put(0, ppDiv1.key);
				final Pair<Polynom, Polynom> ppDiv2 = math.Div(seq.get(1), pReduce[id]);
				this.Display("Div Reduce:\n" + ppDiv2.val.toString());
				seq.put(1, ppDiv2.key);
				this.Display(seq);
			}
			if(doCoeffs) {
				this.Display(this.Coeffs(seq.get(0), 4));
				this.Display(this.Coeffs(seq.get(1), 4));
			}
			
			final Polynom pY = math.Mult(math.Mult(seq.get(0), mDiv), -1);
			final Polynom pDivY = seq.get(1);
			for(int idAll = id + 1; idAll < p.length; idAll++) {
				pR = math.Replace(p[idAll], sVar[id], pY);
				final int iMaxPow = math.MaxPow(pR, sVarDiv);
				if(iMaxPow > 0) {
					pR = math.Mult(pR, new Monom("pDivInv", iMaxPow));
					pR = math.Replace(pR, mIdent, "Identity");
					pR = math.Replace(pR, "Identity", 1, 1);
					// System.out.println("\nDIV routine: R\n" + pR.toString());
					// System.out.println("\nDIV routine: Div\n" + pDivY.toString());
					if(pDivY != null) {
						pR = math.Replace(pR, "pDivInv", pDivY);
					} else {
						System.out.println("\nDIV routine: Div == NULL\n");
					}
				}
				p[idAll] = math.Simplify(pR); // pR;
			}
		}

		if(pDiv != null) {
			return math.Div(p[p.length - 1], pDiv).key;
		}
		System.out.println("Finished!");
		System.out.println("Polynom terms = " + p[p.length - 1].size());
		return p[p.length - 1];
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
	
	public String Coeffs(final Polynom p, int countVars) {
		final Vector<Double> vCoeffs = new Vector<> ();
		final TreeMap<String, Integer> dictVars = new TreeMap<> ();
		final Vector<Vector<Integer>> vPowsPoly = new Vector<> ();
		
		for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
			vCoeffs.add(entryM.getValue());
			final Vector<Integer> vPowsMonom = new Vector<> ();
			vPowsPoly.add(vPowsMonom);
			for(int i=0; i < countVars; i++) { vPowsMonom.add(0); }
			
			for(final Map.Entry<String, Integer> entryV : entryM.getKey().entrySet()) {
				final Integer idVar = dictVars.get(entryV.getKey());
				if(idVar == null) {
					final int idNew = dictVars.size();
					dictVars.put(entryV.getKey(), idNew);
					if(dictVars.size() > countVars) {
						countVars ++;
						vPowsMonom.add(entryV.getValue()); // power of Var;
					} else {
						vPowsMonom.set(idNew, entryV.getValue()); // power of Var;
					}
				} else {
					vPowsMonom.set(idVar, entryV.getValue()); // power of Var;
				}
			}
		}

		final StringBuilder sb = new StringBuilder ();
		sb.append("coeff = c(");
		boolean hasNL = false;
		int lenPrev = 0;
		for(final Double dCoeff : vCoeffs) {
			hasNL = false;
			sb.append(dCoeff).append(", ");
			if(sb.length() > lenPrev + 60) {
				sb.delete(sb.length() - 1, sb.length());
				hasNL = true;
				sb.append("\n\t");
				lenPrev = sb.length();
			}
		}
		sb.delete(sb.length() - (hasNL ? 3 : 2), sb.length());
		sb.append(");\n");
		// Vars
		for(final Map.Entry<String, Integer> entryV : dictVars.entrySet()) {
			sb.append("# ").append(entryV.getKey()).append(" = Col ")
				.append(entryV.getValue()).append(";\n");
		}
		sb.append("m = matrix(c(\n\t");
		boolean addNL = false;
		for(final Vector<Integer> vPows : vPowsPoly) {
			for(final Integer pow : vPows) {
				sb.append(pow).append(", ");
			}
			if(addNL) {
				sb.delete(sb.length() - 1, sb.length()); // del space;
				sb.append("\n\t");
			}
			addNL = ! addNL;
		}
		sb.delete(sb.length() - (addNL ? 2 : 3), sb.length());
		sb.append("), ncol=").append(dictVars.size()).append(", byrow=TRUE)");
		
		return sb.toString();
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
	
	public String ToCoeffs(Polynom p) {
		final Vector<String> vCoeffs = new Vector<>();
		for(final Map.Entry<Integer, Polynom> entry : this.ToSeq(p, p.sRootName).entrySet()) {
			while(entry.getKey() > vCoeffs.size()) {
				vCoeffs.add("0");
			}
			vCoeffs.add(entry.getValue().toString());
		}
		final StringBuilder sb = new StringBuilder();
		sb.append("c(");
		for(int npos = vCoeffs.size() - 1; npos >= 0; npos--) {
			sb.append(vCoeffs.get(npos)).append(", ");
		}
		sb.delete(sb.length() - 2, sb.length());
		sb.append(")");
		final String sP = sb.toString();
		return sP;
	}
}
