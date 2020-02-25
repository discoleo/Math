package data;

public class Unity {
	
	public final static String sROOT_UNITY = "m";
	
	public static Pair<Polynom, Polynom> EvalSeq(final PowGrade powGrade) {
		// TODO: generic Concept
		final Polynom pUnity;
		if(powGrade.iPow == 9 && powGrade.dVal == 1) {
			pUnity = Unity.CreateUnity(new int [] {3, 6});
		} else if(powGrade.iPow == 15 && powGrade.dVal == 1) {
			pUnity = Unity.CreateUnity(new int [] {3, 6, 9, 12});
		} else {
			return null;
		}

		return new Pair<> (pUnity, new Polynom(-1, sROOT_UNITY));
	}
	
	public static Polynom CreateUnity(final int [] iPows) {
		final Polynom pUnity = new Polynom(sROOT_UNITY);
		for(final int iPow : iPows) {
			pUnity.Add(new Monom(sROOT_UNITY, iPow), 1);
		}
		return pUnity;
	}
}
