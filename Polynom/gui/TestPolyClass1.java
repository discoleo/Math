package gui;

import data.PolyResult;
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
	}
	
	public String Rational(final String sRK) {
		return sRK.toUpperCase();
	}
	
	public Polynom Simple(final int nOrder) {
		return this.Build("s" + (nOrder - 1) + "*k^" + (nOrder - 1) + "+ s1*k", nOrder);
	}
	
	public Polynom Build(final String sRoot, final int nOrder) {
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
}
