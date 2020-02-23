/*
 * Various MathTools for Polynomials
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
package math;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import data.Monom;
import data.Pair;
import data.Polynom;
import data.PowGrade;


public class MathTools {
	
	// ++++ Polynom helper Functions ++++
	
	// +++ Multiply Polynomial with a Scalar
	public Polynom Mult(final Polynom p1, final int iMult) {
		return this.Mult(p1, iMult, 1);
	}
	public Polynom MultInPlace(final Polynom p1, final int dM, final double dD) {
		for(final Map.Entry<Monom, Double> entry : p1.entrySet()) {
			final double dRez = entry.getValue() * dM / dD;
			p1.put(entry.getKey(), dRez);
		}
		return p1;
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
		final Iterator<Map.Entry<Monom, Double>> itP2 = p2Const.entrySet().iterator();
		
		while(itP2.hasNext()) {
			final Map.Entry<Monom, Double> entryP2 = itP2.next();
			final Double dCoeff = p1.get(entryP2.getKey());
			if(dCoeff == null) {
				p1.put(entryP2.getKey(), entryP2.getValue());
			} else {
				final double dCoeffRez = entryP2.getValue() + dCoeff;
				if(dCoeffRez != 0) {
					p1.put(entryP2.getKey(), dCoeffRez);
				} else {
					p1.remove(entryP2.getKey());
				}
			}
		}
		return p1;
	}
	public Polynom AddInPlaceOld(final Polynom p1, final Polynom p2Const) {
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

	public Polynom RemoveInPlace(final Polynom p1, final Polynom p2Const) {
		// remove p2Const from p1: assumes equal Coefficients
		final Iterator<Monom> itP2 = p2Const.keySet().iterator();
		while(itP2.hasNext()) {
			final Monom mP2 = itP2.next();
			p1.remove(mP2);
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
	public int SamePower(final Polynom p, final String sVar) {
		// assumes NO monomes with coeff = 0;
		final int iPow = p.firstKey().getOrDefault(sVar, 0);
		
		for(final Monom m : p.keySet()) {
			if(iPow != m.getOrDefault(sVar, 0)) {
				return - 1;
			}
		}
		return iPow;
	}
	
	// +++ Select Subsequence +++
	public Polynom SubSequence(final Polynom p, final String sVarName, final int iPow) {
		final String sRootName = p.sRootName;
		final Polynom polyRez =  new Polynom(sRootName);
		
		for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
			final Integer nPow = entryM.getKey().get(sVarName);
			if(iPow == 0 && nPow == null) {
				this.Add(polyRez, entryM.getKey(), entryM.getValue());
			} else if(nPow != null && nPow == iPow) {
				// in place addition
				this.Add(polyRez, entryM.getKey(), entryM.getValue());
			}
		}
		return polyRez;
	}
	public int [] SubSequencePow(final Polynom p1, final Polynom p2, final int nOrder,
			final String sVarName, final String sUnity) {
		// assumes same powers for the main Var;
		final int [] iPowSeq = new int [nOrder];
		
		for(int nPow = nOrder - 1; nPow >= 0; nPow--) {
			final Polynom p1Sub = Replace(SubSequence(p1, sVarName, nPow), sVarName, 1, 1);
			final Polynom p2Sub = Replace(SubSequence(p2, sVarName, nPow), sVarName, 1, 1);
			// System.out.println("x Power: " + nPow);
			final int iUPow = SamePower(p2Sub, sUnity);
			if(iUPow < 0) {
				iPowSeq[nPow] = -999;
			} else {
				final Polynom p2SubSub = Replace(SubSequence(p2Sub, sUnity, iUPow), sUnity, iUPow, 1);
				final Polynom pDiff = Diff(p1Sub, p2SubSub);
				// System.out.println("Size: " + pDiff.size());
				if(pDiff.size() == 0) {
					// System.out.println("Size: " + p2SubSub.size());
					iPowSeq[nPow] = iUPow;
				} else {
					iPowSeq[nPow] = (iUPow == 0) ? -990 : -iUPow;
				}
			}
		}
		return iPowSeq;
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
		// System.out.println(polyRez.toString());
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
	public Polynom Replace(final Polynom p, final String sVarName, final Monom m) {
		return this.Replace(p, sVarName, new Polynom(m, 1, p.sRootName)); // TODO: Root name
	}
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
	public Polynom ReplaceSeq(final Polynom pOriginal, final String sVarName, final PowGrade powGrade) {
		// processes only seq of type: 1 + m + m^2 + ...
		Polynom polyRez =  new Polynom(pOriginal.sRootName);
		final Polynom pTemp = new Polynom(pOriginal);
		final int iMaxPow = powGrade.MaxPow();
		final double dVal = - powGrade.dVal;
		
		Iterator<Map.Entry<Monom, Double>> itM = pTemp.entrySet().iterator();
		
		// TODO: !! BUG !!
		while(itM.hasNext()) {
			final Map.Entry<Monom, Double> entryM = itM.next();
			final int nPow = entryM.getKey().getOrDefault(sVarName, 0);
			final Monom mWBase;
			if(nPow == 0) {
				mWBase = entryM.getKey();
				// polyRez = this.Add(polyRez, entryM.getKey(), entryM.getValue());
				// itM.remove();
				// continue;
			} else {
				mWBase = this.Div(entryM.getKey(), sVarName, nPow);
			}
			// TODO: nPow > iMaxPow
			final Monom mW = new Monom(mWBase);
			boolean isPresent = true;
			int iMissingPow = -1;
			int countPos = 0;
			int countNeg = 0;
			double dMinCoeff = Double.POSITIVE_INFINITY;
			double dMaxCoeff = Double.NEGATIVE_INFINITY;
			// 1 + m + m^2 + ...
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
				if(dCoeff > 0) {
					dMaxCoeff = Math.min(dMinCoeff, dCoeff);
					countPos ++ ;
				} else if(dCoeff < 0) {
					dMaxCoeff = Math.max(dMaxCoeff, dCoeff);
					countNeg ++;
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
			if(isPresent && countPos < iMaxPow && countNeg < iMaxPow) {
				isPresent = false;
			}
			if( ! isPresent) {
				polyRez = this.Add(polyRez, entryM.getKey(), entryM.getValue());
				itM.remove();
				// TODO: optimize and add all Terms?
				continue;
			}
			// isPresent
			for(int iPow=0; iPow <= iMaxPow; iPow++) {
				if(iPow > 0) {
					mWBase.Add(sVarName, 1);
				}
				final Double dCoeff = pTemp.get(mWBase);
				if(dCoeff == null) {
					polyRez = this.Add(polyRez, new Monom(mWBase), - dMaxCoeff);
				} else if(dCoeff != dMaxCoeff) {
					polyRez = this.Add(polyRez, new Monom(mWBase), dCoeff - dMaxCoeff);
				}
				pTemp.remove(mWBase);
			}
			itM = pTemp.entrySet().iterator();
		}
		return polyRez;
	}
	
	public Polynom ReplaceSeqMult(final Polynom pOriginal, final String sVarName,
			final PowGrade powGrade, final int iMaxMissing) {
		// replace Seq with multiple terms missing
		Polynom polyRez =  new Polynom(pOriginal.sRootName);
		final Polynom pTemp = new Polynom(pOriginal);
		// final int iMaxPow = powGrade.MaxPow();
		// final double dVal = - powGrade.dVal;
		
		Iterator<Map.Entry<Monom, Double>> itM = pTemp.entrySet().iterator();
		
		while(itM.hasNext()) {
			final Map.Entry<Monom, Double> entryM = itM.next();
			final Integer nPow = entryM.getKey().get(sVarName);
			
			final Monom mWBase;
			// TODO: nPow > iMaxPow
			if(nPow == null) {
				mWBase = entryM.getKey();
			} else {
				mWBase = this.Div(entryM.getKey(), sVarName, nPow);
			}
			final Monom mBase = new Monom(mWBase);
			
			final Pair<Vector<Pair<Monom, Double>>, Double> vMonoms =
					this.HasSeq(pTemp, mBase, sVarName, powGrade, iMaxMissing);
			// ! isPresent
			if(vMonoms.val == 0) {
				for(final Pair<Monom, Double> pair : vMonoms.key) {
					if(pair.val != 0) {
						polyRez = this.Add(polyRez, pair.key, pair.val);
					}
					pTemp.remove(pair.key);
				}
			}
			// isPresent
			else {
				// process original Terms
				// processes automatically the Complement as well
				for(final Pair<Monom, Double> pair : vMonoms.key) {
					final double dRemaining = pair.val - vMonoms.val;
					if(dRemaining != 0) {
						polyRez = this.Add(polyRez, pair.key, dRemaining);
					}
					pTemp.remove(pair.key);
				}
			}
			// update Iterator
			itM = pTemp.entrySet().iterator();
		}
		return polyRez;
	}
	
	public Polynom ReplaceSeqByVar(final Polynom pOriginal,
			final Polynom pSubSeq, final String sSubSeqName, final PowGrade powGrade) {
		// assumes "x" = variable (sRootName)
		final String sVarPrimary = pOriginal.sRootName;
		final Monom m = new Monom();
		final Polynom pRez = new Polynom(pOriginal);
		final Monom mSubSeqNew = new Monom(sSubSeqName, 1);
		
		for(int iPow=0; iPow < powGrade.iPow; iPow++) {
			if(iPow > 0) {
				m.Add(sVarPrimary, 1);
			}
			boolean hasSubSeq = true;
			double dCoeffBase = 0;
			for(final Monom mSub : pSubSeq.keySet()) {
				final Monom mSubX = new Monom(mSub).Add(sVarPrimary, iPow);
				final Double dCoeff = pOriginal.get(mSubX);
				if(dCoeff == null || dCoeff == 0) {
					hasSubSeq = false;
					break;
				}
				if(dCoeffBase == 0) {
					dCoeffBase = dCoeff;
				} else if(dCoeffBase != dCoeff) {
					hasSubSeq = false;
					break;
				}
			}
			if( ! hasSubSeq) {
				continue;
			}
			// remove SubSeq
			final Polynom pSubSeqRemove;
			if(iPow > 0) {
				final Monom mX = new Monom(sVarPrimary, iPow);
				pSubSeqRemove = this.Mult(pSubSeq, mX);
			} else {
				pSubSeqRemove = pSubSeq;
			}
			this.RemoveInPlace(pRez, pSubSeqRemove);
			// replace SubSeq
			final Monom mSubSeqRepl = new Monom(mSubSeqNew).Add(sVarPrimary, iPow);
			pRez.Add(mSubSeqRepl, dCoeffBase);
		}
		return pRez;
	}
	
	public Pair<Vector<Pair<Monom, Double>>, Double> HasSeq(final Polynom p, final Monom mBase,
			final String sVarName,
			final PowGrade powGrade, final int iMaxMissing) {
		final int iMaxPow = powGrade.MaxPow();
		final int lenPow = iMaxPow + 1;
		
		final Vector<Pair<Monom, Double>> vMonoms = new Vector<>();

		int iMissing = 0;
		for(int iPow=0; iPow <= iMaxPow; iPow++) {
			if(iPow > 0) {
				mBase.Add(sVarName, 1);
			}
			final Double dCoeff = p.getOrDefault(mBase, 0d);
			if(dCoeff == 0) {
				iMissing ++;
			}
			final Pair<Monom, Double> pair = new Pair<>(new Monom(mBase), dCoeff);
			vMonoms.add(pair);
		}
		// only existence of Sub-seq;
		if(iMissing > iMaxMissing) {
			return new Pair<> (vMonoms, 0d);
		} else {
			final double dMaxCoeff = this.HasSeq(vMonoms, lenPow - iMaxMissing);
			// System.out.println(mBase.toString() + ": " + dMaxCoeff + ", " + (lenPow - iMaxMissing));
			return new Pair<> (vMonoms, dMaxCoeff);
		}
	}
	public double HasSeq(final Vector<Pair<Monom, Double>> vMonoms, final int iMinCount) {
		// Note: some terms may be already missing from the Vector;
		// TODO: version for roots of minus-Unity;
		int countPos = 0;
		int countNeg = 0;
		double dMin = Double.POSITIVE_INFINITY;
		double dMax = Double.NEGATIVE_INFINITY;
		for(final Pair<Monom, Double> pair : vMonoms) {
			if(pair.val > 0) {
				countPos ++;
				if(pair.val < dMin) {
					dMin = pair.val;
				}
			} else if(pair.val < 0) {
				countNeg ++;
				if(pair.val > dMax) {
					dMax = pair.val;
				}
			}
		}
		if(countPos >= iMinCount) {
			return dMin;
		} else if(countNeg >= iMinCount) {
			return dMax;
		}
		return 0;
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
