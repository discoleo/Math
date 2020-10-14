package gui;

import java.util.Map;
import java.util.Vector;

import data.Pair;
import data.PolyResult;
import data.PolySeq;
import data.Polynom;
import math.Derive;


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

	// +++ various ODEs +++
	
	public Polynom ODETest0() {
		final String sK = "x-1";
		return this.ODETest(sK);
	}
	public Polynom ODETest() {
		final String sK = "x-a";
		return this.ODETest(sK);
	}
	public Polynom ODETest2() {
		final String sK = "x^2-x+1";
		return this.ODETest(sK);
	}
	public Vector<Pair<String, String>> TestCoeff(final String sKFunct) {
		final Vector<Pair<String, String>> vReplace = new Vector<> ();
		vReplace.add(new Pair<> ("K", sKFunct));
		vReplace.add(new Pair<> ("s4", "1"));
		vReplace.add(new Pair<> ("s1", "1"));
		vReplace.add(new Pair<> ("s3", "-1"));
		vReplace.add(new Pair<> ("s2", "1"));
		return vReplace;
	}
	public Vector<Pair<String, String>> TestCoeff2(final String sKFunct) {
		final Vector<Pair<String, String>> vReplace = new Vector<> ();
		vReplace.add(new Pair<> ("K", sKFunct));
		vReplace.add(new Pair<> ("s4", "x^2+x"));
		vReplace.add(new Pair<> ("s1", "x^2+x"));
		vReplace.add(new Pair<> ("s3", "-x^2-2*x-1"));
		vReplace.add(new Pair<> ("s2", "x^2"));
		return vReplace;
	}
	public Polynom ODETest(final String sKFunct) {
		final int nOrder = 5;
		final String sRoot = "s4*k^4 + s3*k^3 + s2*k^2 + s1*k";
		final Vector<Pair<String, String>> vReplace = this.TestCoeff(sKFunct);
		
		final Polynom pRez = this.Replace(sRoot, nOrder, vReplace);
		this.Display(polyFact.ToSeq(pRez, "y"));
		
		// ODE:
		final Derive derive = new Derive(this.math, this.polyFact);
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
}
