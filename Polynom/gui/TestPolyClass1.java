package gui;

import java.util.Map;
import java.util.Vector;

import data.Monom;
import data.Pair;
import data.PolyResult;
import data.PolySeq;
import data.Polynom;

public class TestPolyClass1 extends BaseGui {
	
	// Class 1 Polynomials:
	// Roots with radicals
	
	public final String sX = "x";
	public final String sKRoot = "k";
	public final String sK = "K"; // k^n = K;

	public TestPolyClass1(final BaseGui base) {
		super(base);
	}
	
	// ========= Class-1 Polynomials =========
	
	public void Test() {
		// this.Simple(5);
		this.ODETest();
	}
	
	public String Rational(final String sRK) {
		return sRK.toUpperCase();
	}
	
	public Polynom Simple(final int nOrder) {
		return this.Build("s" + (nOrder - 1) + "*k^" + (nOrder - 1) + "+ s1*k", nOrder);
	}
	
	public Polynom Build(final String sRoot, final int nOrder) {
		return this.Build(sRoot, nOrder, sX);
	}
	public Polynom Build(final String sRoot, final int nOrder, final String sX) {
		final Polynom pRoot = parser.Parse(sRoot, sKRoot);
		return this.Build(pRoot, nOrder, sX);
	}
	
	public Polynom Build(final Polynom pRoot, final int nOrder) {
		return this.Build(pRoot, nOrder, sX);
	}
	
	public Polynom Build(final Polynom pRoot, final int nOrder, final String sVar) {
		final PolyResult pRez = polyFact.Create(pRoot, nOrder, sVar);
		final Polynom p = math.Replace(pRez.GetPoly(),
				pRoot.sRootName, nOrder, this.Rational(pRoot.sRootName));
		this.Display("Root = " + pRoot.toString());
		this.Display(p);
		this.Display("Size = " + p.size());
		
		return p;
	}
	
	// ================
	
	public Polynom Replace(final String sRoot, final int nOrder, final Vector<Pair<String, String>> vReplace) {
		final Polynom pBase = this.Build(sRoot, nOrder, "y");
		
		Polynom pRez = pBase;
		for(final Pair<String, String> pair : vReplace) {
			pRez = math.Replace(pRez, pair.key, parser.Parse(pair.val, sX));
		}
		
		return pRez;
	}
	
	public Polynom ODETest() {
		final String sRoot = "s4*k^4 - s3*k^3 + s2*k^2 + s1*k";
		
		final Vector<Pair<String, String>> vReplace = new Vector<> ();
		vReplace.add(new Pair<> ("K", "x^2-x+1"));
		vReplace.add(new Pair<> ("s4", "x^2+x"));
		vReplace.add(new Pair<> ("s1", "x^2+x"));
		vReplace.add(new Pair<> ("s3", "x^2+2*x+1"));
		vReplace.add(new Pair<> ("s2", "x^2"));
		
		final Polynom pRez = this.Replace(sRoot, 5, vReplace);
		this.Display(polyFact.ToSeq(pRez, "y"));
		
		// ODE:
		final Derive derive = new Derive();
		final Polynom pDRez = derive.ODE(pRez, "x", "y");
		this.PrintDy(pDRez, "y");
		
		return pRez;
	}
	
	public void PrintDy(final Polynom p, final String sVar) {
		final String dY = "d" + sVar;
		final PolySeq seq = polyFact.ToSeq(p, dY);
		
		for(final Map.Entry<Integer, Polynom> entryP : seq.entrySet()) {
			if(entryP.getKey() == 0) {
				this.Display(entryP.getValue());
				continue;
			}
			for(final Map.Entry<Integer, Polynom> entryP2 : polyFact.ToSeq(entryP.getValue(), sVar).entrySet()) {
				this.Display(" +\n(");
				this.Display(entryP2.getValue());
				// Pow
				final int iPow = entryP2.getKey();
				final String sMY = (iPow == 0) ? "" : "*" + sVar + "^" + iPow;
				this.Display(") " + sMY + " * " + dY + "\n");
			}
		}
	}
	
	
	public class Derive {

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
}
