package math;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

/*
 * Various MathTools for Polynomials
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
public class MathTools {
	
	// ++++ Polynom helper Functions ++++
	
	// +++ Multiply Polynomial with a Scalar
	public Polynom Mult(final Polynom p1, final int iMult) {
		return this.Mult(p1, iMult, 1);
	}
	public Polynom Mult(final Polynom p1, final int iM, final int iD) {
		// multiplicare si diviziune polinom cu o valoare numerica;
		// de fapt vom procesa doar coeficientii;
		// TODO:
		// final Polynom pM = new Polynom();
		// TODO: iM == 0
		for(final Map.Entry<Monom, Double> entry : p1.entrySet()) {
			final double dRez = entry.getValue() * iM / iD;
			p1.put(entry.getKey(), dRez);
		}
		return p1;
	}
	public Polynom MultInPlace(final Polynom p1, final int iM) {
		for(final Map.Entry<Monom, Double> entry : p1.entrySet()) {
			final double dRez = entry.getValue() * iM;
			p1.put(entry.getKey(), dRez);
		}
		return p1;
	}
	
	// +++ Multiply 2 Polynomials
	public Polynom Mult(final Polynom p1, final Monom m2) {
		return this.Mult(p1, new Polynom(m2, 1, p1.sRootName));
	}
	public Polynom Mult(final Polynom p1, final Monom m2, final double dCoeff) {
		return this.Mult(p1, new Polynom(m2, dCoeff, p1.sRootName));
	}
	public Polynom Mult(final Polynom p1, final Polynom p2) {
		// multiplicarea celor 2 polinoame
		final Polynom pM = new Polynom(p1.sRootName);

		for(final Map.Entry<Monom, Double> entryP1 : p1.entrySet()) {
			for(final Map.Entry<Monom, Double> entryP2 : p2.entrySet()) {
				final Monom mRez = this.Mult(entryP1.getKey(), entryP2.getKey());
				final double dCoeff = entryP1.getValue() * entryP2.getValue();
				// trebuie sa verificati daca exista deja monomul: ex. x*x^3 == x^2*x^2
				final Double dCoeffPrev = pM.get(mRez);
				if(dCoeffPrev == null) {
					pM.put(mRez, dCoeff);
				}  else {
					final double dCoeffRez = dCoeffPrev + dCoeff;
					if(dCoeffRez != 0) {
						pM.put(mRez, dCoeffRez);
					} else {
						pM.remove(mRez);
					}
				}
			}
		}
		return pM;
	}
	public Monom Mult(final Monom m1, final Monom m2) {
		final Monom mRez = new Monom();
		for(final Map.Entry<String, Integer> entryM1 : m1.entrySet()) {
			final Integer iPow2 = m2.get(entryM1.getKey());
			if(iPow2 == null) {
				mRez.put(entryM1.getKey(), entryM1.getValue());
			} else {
				final int iPowRez = entryM1.getValue() + iPow2;
				if(iPowRez != 0) {
					mRez.put(entryM1.getKey(), iPowRez);
				} else {
					mRez.remove(entryM1.getKey());
				}
			}
		}
		// Variables only in m2
		for(final Map.Entry<String, Integer> entryM2 : m2.entrySet()) {
			if( ! m1.containsKey(entryM2.getKey())) {
				mRez.put(entryM2.getKey(), entryM2.getValue());
			}
		}

		return mRez;
	}
	
	public Monom Pow(final Monom m1, final int iPow) {
		// assumes iPow != 0
		final Monom mRez = new Monom();
		for(final Map.Entry<String, Integer> entryM1 : m1.entrySet()) {
			mRez.Add(entryM1.getKey(), iPow * entryM1.getValue());
		}
		return mRez;
	}
	
	public Monom Div(final Monom m1, final Monom m2) {
		final Monom mRez = new Monom(m1);
		if(m2.size() == 0) {
			return mRez;
		}
		for(final Map.Entry<String, Integer> entryM2 : m2.entrySet()) {
			final Integer iPow1 = m1.get(entryM2.getKey());
			if(iPow1 == null) {
				// negative Power
				mRez.Add(entryM2.getKey(), -entryM2.getValue());
			} else {
				final int iPowRez = iPow1 - entryM2.getValue();
				if(iPowRez == 0) {
					mRez.remove(entryM2.getKey());
				} else {
					mRez.Put(entryM2.getKey(), iPowRez);
				}
			}
		}
		return mRez;
	}
	
	public Monom Div(final Monom m1, final String sVarName, final int iPow) {
		final Monom mRez = new Monom(m1);
		if(iPow == 0) {
			return mRez;
		}
		final Integer iPow1 = m1.get(sVarName);
		if(iPow1 == null) {
			// negative Power
			mRez.Add(sVarName, -iPow);
		} else {
			final int iPowRez = iPow1 - iPow;
			if(iPowRez == 0) {
				mRez.remove(sVarName);
			} else {
				mRez.Put(sVarName, iPowRez);
			}
		}
		return mRez;
	}
	
	public Polynom Add(final Polynom p, final Monom m, final double dCoeff) {
		// "in place" addition
		final Double dCoeffOld = p.get(m);
		if(dCoeffOld == null) {
			p.put(m, dCoeff);
		} else {
			final double dCoeffNew = dCoeffOld + dCoeff;
			if(dCoeffNew != 0) {
				p.put(m, dCoeffNew);
			} else {
				p.remove(m);
			}
		}
		return p;
	}
	public Polynom Add(final Polynom p1, final Polynom p2) {
		// Suma dintre 2 polinoame
		final Polynom pRez = new Polynom(p1.sRootName);
		// daca vreti: puteti salva suma tot in p1
		// dar atunci trebuie sa fiti atenti daca un termen se anuleaza
		// pentru ca va trebui sa il eliminati din p1!
		for(final Map.Entry<Monom, Double> entryP1 : p1.entrySet()) {
			final Double dCoeff2 = p2.get(entryP1.getKey());
			if(dCoeff2 == null) {
				pRez.put(entryP1.getKey(), entryP1.getValue());
			} else {
				final double dCoeffRez = entryP1.getValue() + dCoeff2;
				if(dCoeffRez != 0) {
					pRez.put(entryP1.getKey(), dCoeffRez);
				} else {
					pRez.remove(entryP1.getKey());
				}
			}
		}
		// Terms only in p2
		for(final Map.Entry<Monom, Double> entryP2 : p2.entrySet()) {
			if( ! p1.containsKey(entryP2.getKey())) {
				pRez.put(entryP2.getKey(), entryP2.getValue());
			}
		}
		return pRez;
	}
	public Polynom AddInPlace(final Polynom p1, final Polynom p2Const) {
		final Polynom p2Temp = new Polynom(p2Const);
		// Suma dintre 2 polinoame: in-place
		final Iterator<Map.Entry<Monom, Double>> itP1 = p1.entrySet().iterator();
		while(itP1.hasNext()) {
			final Map.Entry<Monom, Double> entryP1 = itP1.next();
			final Double dCoeff2 = p2Temp.get(entryP1.getKey());
			if(dCoeff2 != null) {
				final double dCoeffRez = entryP1.getValue() + dCoeff2;
				p2Temp.remove(entryP1.getKey());
				if(dCoeffRez != 0) {
					p1.put(entryP1.getKey(), dCoeffRez);
				} else {
					p1.remove(entryP1.getKey());
				}
			}
		}
		// Terms only in p2
		for(final Map.Entry<Monom, Double> entryP2 : p2Temp.entrySet()) {
			// if( ! p1.containsKey(entryP2.getKey())) {
				p1.put(entryP2.getKey(), entryP2.getValue());
			// }
		}
		return p1;
	}
	public Polynom Diff(final Polynom p1, final Polynom p2) {
		// Diferenta dintre 2 polinoame
		final Polynom pRez = new Polynom(p1.sRootName);
		// daca vreti: puteti salva diferenta tot in p1
		// dar atunci trebuie sa fiti atenti daca un termen se anuleaza
		// pentru ca va trebui sa il eliminati din p1!
		for(final Map.Entry<Monom, Double> entryP1 : p1.entrySet()) {
			final Double dCoeff2 = p2.get(entryP1.getKey());
			if(dCoeff2 == null) {
				pRez.put(entryP1.getKey(), entryP1.getValue());
			} else {
				final double dCoeffRez = entryP1.getValue() - dCoeff2;
				if(dCoeffRez != 0) {
					pRez.put(entryP1.getKey(), dCoeffRez);
				} else {
					pRez.remove(entryP1.getKey());
				}
			}
		}
		// Terms only in p2
		for(final Map.Entry<Monom, Double> entryP2 : p2.entrySet()) {
			if( ! p1.containsKey(entryP2.getKey())) {
				pRez.put(entryP2.getKey(), - entryP2.getValue());
			}
		}
		return pRez;
	}
	
	// +++ Max Power of Monom2 in Monom1
	public int Contains(final Monom m1, final Monom m2) {
		if(m2.size() == 0) {
			return 0;
		}
		int iMaxPow = Integer.MAX_VALUE;
		for(final Map.Entry<String, Integer> entryM2 : m2.entrySet()) {
			final Integer iPow1 = m1.get(entryM2.getKey());
			if(iPow1 == null) {
				return 0;
			}
			if(iPow1 < entryM2.getValue()) {
				return 0;
			}
			final int iMaxPow1 = iPow1 / entryM2.getValue();
			iMaxPow = Math.min(iMaxPow, iMaxPow1);
		}
		return iMaxPow;
	}
	
	// +++ Variable Replacements +++
	
	// +++ Reduce: Var^p to Num
	public Polynom Replace(final Polynom p, final String sVarName, final int iPow, final double dVal) {
		final String sRootName = p.sRootName;
		final Polynom polyRez =  new Polynom(sRootName);
		
		for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
			final Integer nPow = entryM.getKey().get(sVarName);
			if(nPow == null || nPow < iPow) {
				// in place addition
				this.Add(polyRez, entryM.getKey(), entryM.getValue());
				continue;
			} else {
				final Monom mRemaining = new Monom(entryM.getKey());
				final int iPowAfter = nPow % iPow;
				final int iPowPow = nPow / iPow;
				if(iPowAfter == 0) {
					mRemaining.remove(sVarName);
				} else {
					mRemaining.Put(sVarName, iPowAfter);
				}
				// Multiply with Coeff
				final double dCoeff = entryM.getValue() * Pow(dVal, iPowPow);
				// System.out.println(mRemaining + ": " + dCoeff);
				this.Add(polyRez, mRemaining, dCoeff);
			}
		}
		return polyRez;
	}
	
	// +++ Reduce: Var^p to NewVar
	public Polynom Replace(final Polynom p, final String sVarName, final int iPow, final String sNewVar) {
		final String sRootName = p.sRootName;
		final Polynom polyRez =  new Polynom(sRootName);
		
		for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
			final Integer nPow = entryM.getKey().get(sVarName);
			if(nPow == null || nPow < iPow) {
				// in place addition
				this.Add(polyRez, entryM.getKey(), entryM.getValue());
				continue;
			} else {
				final Monom mRemaining = new Monom(entryM.getKey());
				final int iPowAfter = nPow % iPow;
				final int iPowPow = nPow / iPow;
				if(iPowAfter == 0) {
					mRemaining.remove(sVarName);
				} else {
					mRemaining.Put(sVarName, iPowAfter);
				}
				mRemaining.Add(sNewVar, iPowPow);
				// Multiply with Coeff
				final double dCoeff = entryM.getValue();
				// System.out.println(mRemaining + ": " + dCoeff);
				this.Add(polyRez, mRemaining, dCoeff);
			}
		}
		return polyRez;
	}
	
	// +++ Reduce: Monom m to NewVar
	public Polynom Replace(final Polynom p, final Monom m, final String sNewVar) {
		final String sRootName = p.sRootName;
		final Polynom polyRez =  new Polynom(sRootName);

		for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
			final int iMaxPow = this.Contains(entryM.getKey(), m);
			if(iMaxPow == 0) {
				polyRez.Add(entryM.getKey(), entryM.getValue());
			} else {
				final Monom mReduced = this.Div(entryM.getKey(), this.Pow(m, iMaxPow));
				mReduced.Add(sNewVar, iMaxPow);
				polyRez.Add(mReduced, entryM.getValue());
			}
		}
		return polyRez;
	}
	

	// +++ Convolve: Replace 1 variable with a polynomial
	public Polynom Replace(final Polynom p, final String sVarName, final Polynom pW) {
		final boolean isSameVar = p.sRootName.equals(sVarName);
		final String sRootName = isSameVar ? pW.sRootName : p.sRootName;
		final Polynom polyRez = new Polynom(sRootName);
		return this.Replace(polyRez, p, sVarName, pW);
	}
	public Polynom Replace(Polynom polyRez, final Polynom p, final String sVarName, final Polynom pW) {
		// TODO: "transfer" results to original polyRez
		final Vector<Polynom> vPolyPow = new Vector<> (); // powers
		vPolyPow.add(pW); // Powers of pW

		// final boolean isSameVar = p.sRootName.equals(sVarName);
		final String sRootName = polyRez.sRootName;
		// Polynom polyRez =  new Polynom(sRootName);
		
		for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
			final Integer nPow = entryM.getKey().get(sVarName);
			if(nPow == null) {
				polyRez = this.Add(polyRez, entryM.getKey(), entryM.getValue());
				continue;
			}
			while(vPolyPow.size() < nPow) {
				final int iLast = vPolyPow.size() - 1;
				vPolyPow.add(this.Mult(vPolyPow.get(iLast), pW));
			}
			final Monom mRemaining = new Monom(entryM.getKey());
			mRemaining.remove(sVarName);
			// Multiply with Coeff
			final Polynom polyTerm = this.Mult(vPolyPow.get(nPow - 1),
					new Polynom(mRemaining, entryM.getValue(), sRootName));
			polyRez = this.Add(polyRez, polyTerm);
		}
		
		return polyRez;
	}
	
	// +++ Replace: sum(var^i) with dVal
	public Polynom ReplaceSeq(final Polynom pOriginal, final String sVarName, final int iMaxPow, final double dVal) {
		Polynom polyRez =  new Polynom(pOriginal.sRootName);
		final Polynom pTemp = new Polynom(pOriginal);
		
		Iterator<Map.Entry<Monom, Double>> itM = pTemp.entrySet().iterator();
		
		while(itM.hasNext()) {
			final Map.Entry<Monom, Double> entryM = itM.next();
			final Integer nPow = entryM.getKey().get(sVarName);
			if(nPow == null) {
				// processes only seq of type: 1 + m + m^2 + ...
				polyRez = this.Add(polyRez, entryM.getKey(), entryM.getValue());
				itM.remove();
				continue;
			}
			// TODO: nPow > iMaxPow
			final Monom mWBase = this.Div(entryM.getKey(), sVarName, nPow);
			final Monom mW = new Monom(mWBase);
			boolean isPresent = true;
			int iMissingPow = -1;
			double dMaxCoeff = entryM.getValue(); // Double.MAX_VALUE;
			// m + m^2 + ...
			for(int iPow=0; iPow <= iMaxPow; iPow++) {
				if(iPow > 0) {
					mW.Add(sVarName, 1);
				}
				final Double dCoeff = pTemp.get(mW);
				if(dCoeff == null) {
					if(iMissingPow < 0) {
						iMissingPow = iPow;
						continue;
					} else {
						isPresent = false;
						break;
					}
				}
				// Cases: Coeff > 0 vs Coeff < 0
				if(dMaxCoeff > 0 && dCoeff > 0) {
					dMaxCoeff = Math.min(dMaxCoeff, dCoeff);
				} else if(dMaxCoeff < 0 && dCoeff < 0) {
					dMaxCoeff = Math.max(dMaxCoeff, dCoeff);
				} else {
					if(iMissingPow < 0) {
						// TODO: which Power is correct?
						iMissingPow = iPow;
						continue;
					}
					isPresent = false;
					break;
				}
			}
			// ! isPresent
			if( ! isPresent) {
				polyRez = this.Add(polyRez, entryM.getKey(), entryM.getValue());
				itM.remove();
				// TODO: optimize and add all Terms?
				continue;
			}
			// isPresent
			if(iMissingPow >= 0) {
				final Monom mReplacement = new Monom(mWBase);
				if(iMissingPow > 0) {
					mReplacement.Add(sVarName, iMissingPow);
				}
				polyRez = this.Add(polyRez, mReplacement, dMaxCoeff * dVal);
			} // ELSE: terms Cancel out!
			for(int iPow=0; iPow <= iMaxPow; iPow++) {
				if(iPow > 0) {
					mWBase.Add(sVarName, 1);
				}
				final Double dCoeff = pTemp.get(mWBase);
				if(dCoeff != null && dCoeff != dMaxCoeff) {
					polyRez = this.Add(polyRez, new Monom(mWBase), dCoeff - dMaxCoeff);
				}
				pTemp.remove(mWBase);
			}
			itM = pTemp.entrySet().iterator();
		}
		return polyRez;
	}
	
	// +++ helper Functions +++

	// helper Function: add a variable to a Polynom
	public Polynom AddVar(final Polynom p, final String sVarName, final int nPow) {
		final Polynom polyRez = new Polynom(sVarName);
		for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
			final Monom mAdded = this.AddVar(entryM.getKey(), sVarName, nPow);
			polyRez.put(mAdded, entryM.getValue());
		}
		return polyRez;
	}
	// helper Function: add a variable to a Monom
	public Monom AddVar(final Monom m, final String sVarName, final int nPow) {
		// Attention: does NOT add "in place"
		// Original Coeffs needed!
		final Monom mAdd = new Monom(m);
		return mAdd.Add(sVarName, nPow);
	}
	
	public int Pow(final int iVal, final int iPow) {
		int iAcc = 1;
		int iValPow = iVal;
		for(int iPowA = iPow; iPowA > 0; ) {
			if(iPowA == 1) {
				iPowA --;
			} else if(iPowA % 2 == 1) {
				iAcc *= iValPow;
				iPowA --;
			} else {
				iValPow *= iValPow;
				iPowA /= 2;
			}
		}
		return iAcc * iValPow;
	}
	public double Pow(final double dVal, final int iPow) {
		if(iPow == 1) {
			return dVal;
		}
		double dAcc = 1;
		double dValPow = dVal;
		for(int iPowA = iPow; iPowA > 0; ) {
			if(iPowA == 1) {
				iPowA --;
			} else if(iPowA % 2 == 1) {
				dAcc *= dValPow;
				iPowA --;
			} else {
				dValPow *= dValPow;
				iPowA /= 2;
			}
		}
		// System.out.println("" + iPow + ": " + dAcc * dValPow);
		return dAcc * dValPow;
	}
}
