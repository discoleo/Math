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
import data.Unity;


public class MathTools {
	
	// ++++ Polynom helper Functions ++++
	
	public int MaxPowSimple(final Polynom p, final String sVar) {
		if(p.sRootName.equals(sVar) && p.IsSortedByPrimary()) {
			// TODO: verify that it is the last key
			final Integer iPow = p.lastKey().get(p.sRootName);
			if(iPow != null) {
				return iPow;
			}
		}
		return -1;
	}
	public int MaxPow(final Polynom p, final String sVar) {
		int maxPow = 0; // works only with positive Powers
		
		for(final Monom m : p.keySet()) {
			final Integer iPow = m.get(sVar);
			if(iPow == null) continue;
			if(maxPow < iPow) maxPow = iPow;
		}
		
		return maxPow;
	}
	public int MinPow(final Polynom p, final String sVar) {
		int minPow = -1; // works only with positive Powers
		
		for(final Monom m : p.keySet()) {
			final Integer iPow = m.get(sVar);
			if(iPow == null) return 0;
			if(minPow < 0) minPow = iPow;
			else minPow = Math.min(minPow, iPow);
		}
		
		return minPow;
	}
	public Polynom Leading(final Polynom p, final String sVar) {
		if(p.sRootName.equals(sVar) && p.IsSortedByPrimary()) {
			// TODO: verify that it is the last key
			final Map.Entry<Monom, Double> mLast = p.lastEntry();
			if(mLast != null) {
				return new Polynom(sVar).Add(mLast.getKey(), mLast.getValue());
			}
		}
		return null;
	}
	
	// +++ Multiply Polynomial with a Scalar
	public Polynom Mult(final Polynom p1, final int iMult) {
		return this.Mult(p1, iMult, 1);
	}
	public Polynom MultInPlace(final Polynom p1, final double dMult) {
		return this.MultInPlace(p1, dMult, 1d);
	}
	public Polynom MultInPlace(final Polynom p1, final double dM, final double dD) {
		for(final Map.Entry<Monom, Double> entry : p1.entrySet()) {
			final double dRez = entry.getValue() * dM / dD;
			p1.put(entry.getKey(), dRez);
		}
		return p1;
	}
	public Polynom Mult(final Polynom p1, final int iM, final int iD) {
		// polynomial multiplication and division with a scalar;
		// use MultInPlace() for in-place multiplication;
		final Polynom pM = new Polynom(p1.sRootName);
		if(iM == 0) {
			return pM;
		}
		for(final Map.Entry<Monom, Double> entry : p1.entrySet()) {
			final double dRez = entry.getValue() * iM / iD;
			pM.put(entry.getKey(), dRez);
		}
		return pM;
	}
	// +++ Multiply Polynomial with a Monome
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
	
	public Polynom DotProd(final Polynom [] pp1, final Polynom [] pp2) {
		final Polynom pR = new Polynom(pp1[0].sRootName);
		
		for(int i=0; i < pp1.length; i++) {
			this.AddInPlace(pR, this.Mult(pp1[i], pp2[i]));
		}
		
		return pR;
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

	public Polynom DivAbs(final Polynom p1, final Monom mDiv) {
		// mDiv *MUST* divide all monomials;
		Polynom pR = new Polynom(p1.sRootName);
		for(final Map.Entry<Monom, Double> entryP : p1.entrySet()) {
			final Monom mRDiv = DivAbs(entryP.getKey(), mDiv);
			if(mRDiv == null) {
				System.out.println("Error: NOT divisible");
				return null;
			}
			pR.Add(mRDiv, entryP.getValue());
		}
		return pR;
	}
	public Monom DivAbs(final Monom m1, final Monom m2) {
		// m2 *MUST* divide m1;
		final Monom mRez = new Monom(m1);
		if(m2.size() == 0) {
			return mRez;
		}
		for(final Map.Entry<String, Integer> entryM2 : m2.entrySet()) {
			final Integer iPow1 = m1.get(entryM2.getKey());
			if(iPow1 == null) {
				// Error
				return null;
			} else {
				final int iPowRez = iPow1 - entryM2.getValue();
				if(iPowRez == 0) {
					mRez.remove(entryM2.getKey());
				} else if(iPowRez > 0) {
					mRez.Put(entryM2.getKey(), iPowRez);
				} else {
					return null;
				}
			}
		}
		return mRez;
	}
	public Pair<Polynom, Polynom> Div(final Polynom p1, final Polynom pDiv) {
		// "agile" implementation of a very simple division
		Polynom pRemain = new Polynom(p1, pDiv.sRootName);
		Polynom pRez = new Polynom(p1.sRootName);
		
		final Map.Entry<Monom, Double> entryDiv = pDiv.lastEntry();
		final Monom mDiv = entryDiv.getKey(); // top Monom
		final double dDiv = entryDiv.getValue();
		
		while(pRemain.size() > 0) {
			final Map.Entry<Monom, Double> entryP = pRemain.lastEntry();
			Monom mTop = entryP.getKey();
			mTop = this.DivAbs(mTop, mDiv);
			if(mTop == null) {
				System.out.println("Error: NOT divisible!");
				return new Pair<> (pRez, pRemain); // TODO: more advanced
			}
			final double dVal = entryP.getValue() / dDiv;
			pRemain = this.Add(pRemain, this.MultInPlace(this.Mult(pDiv, mTop), -dVal, 1.0d));
			pRez.Add(mTop, dVal);
		}
		return new Pair<> (pRez, pRemain);
	}

	public Polynom DivExact(final Polynom p1, final Polynom pDiv, final String sR) {
		// used for elementary Polynomials
		// TODO: implement robust method;
		Polynom pR = null;
		for(final Map.Entry<Monom, Double> entryP : pDiv.entrySet()) {
			final Polynom pRDiv = DivExact(p1, entryP.getKey());
			if(pRDiv.size() == 0) { return null; }
			if(pR == null) {
				pR = pRDiv;
			} else {
				if( ! pR.equals(pRDiv)) {
					System.out.println("Div Exact: different; NOT yet implemented!"); 
					return null; // TODO: NOT yet supported
				}
			}
		}
		if(pR != null) {
			final Monom mR = new Monom(sR, 1);
			final Polynom pReplace = this.Mult(pR, mR);
			return this.Add(
					this.Add(p1, this.Mult(this.MultInPlace(pR, -1), pDiv)), pReplace);
		}
		return pR;
	}
	public Polynom DivExact(final Polynom p1, final Monom mDiv) {
		// used for elementary Polynomials + other uses;
		Polynom pR = new Polynom(p1.sRootName);
		for(final Map.Entry<Monom, Double> entryP : p1.entrySet()) {
			final Monom mRDiv = DivExact(entryP.getKey(), mDiv);
			if(mRDiv == null) { continue; }
			pR.Add(mRDiv, entryP.getValue());
		}
		return pR;
	}
	public Monom DivExact(final Monom m1, final Monom m2) {
		// exact Division: same Power!
		// used for elementary Polynomials
		if(m2.size() == 0) {
			return null;
		}
		final Monom mRez = new Monom(m1);
		for(final Map.Entry<String, Integer> entryM2 : m2.entrySet()) {
			final Integer iPow1 = m1.get(entryM2.getKey());
			if(iPow1 == null) {
				return null;
			} else {
				final int iPowRez = iPow1 - entryM2.getValue();
				if(iPowRez == 0) {
					mRez.remove(entryM2.getKey());
				} else {
					return null;
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
	
	public Polynom Gcd(final Polynom p1, final Polynom p2) {
		// TODO:
		// only simple polynomials: 1 Variable
		final String sVarName = p2.sRootName;
		final Polynom pLeading = this.Leading(p2, sVarName);
		// TODO: evaluate if MaxPow() is fine?
		final int iMaxPow = this.MaxPowSimple(p2, sVarName);
		System.out.println("Max Pow = " + iMaxPow);
		Polynom p = new Polynom(p1);
		final Polynom pDiv = new Polynom(sVarName);
		
		// Iterator<Map.Entry<Monom, Double>> it = p.descendingMap().entrySet().iterator();
		Map.Entry<Monom, Double> entry = p.descendingMap().firstEntry();
		while(entry != null) {
			System.out.println(entry.getKey().toString());
			
			final Integer iPow = entry.getKey().get(sVarName);
			if(iPow == null || iPow < iMaxPow) {
				break; // all Monoms are lower order
			}
			// TODO: verify (divide by pLeading!)
			final Monom mDiv = this.Div(entry.getKey(), pLeading.firstKey());
			final double dDivCoeff = entry.getValue() / pLeading.firstEntry().getValue();
			if(dDivCoeff == 0) { continue; }
			pDiv.Add(mDiv, dDivCoeff);
			p = this.Add(p,
					this.Mult(p2, mDiv, - dDivCoeff));
			entry = p.descendingMap().firstEntry();
		}
		if(p.size() != 0) {
			System.out.println("Error in GCD:\n" + p.toString());
		}
		
		return pDiv;
	}
	
	public Polynom GcdExtract(final Polynom p1, final Polynom p2, final String sVar) {
		Polynom pSm = p1;
		Polynom pGr = p2;
		int iPow1 = this.MaxPow(pSm, sVar);
		int iPow2 = this.MaxPow(pGr, sVar);
		if(iPow1 > iPow2) {
			final Polynom pTmp = pSm; pSm = pGr; pGr = pTmp;
			final int iTmp = iPow1; iPow1 = iPow2; iPow2 = iTmp;
		}
		
		while(true) {
			if(iPow1 <= 1) return pSm;
			if(iPow2 <= 1) return pGr;
			
			final Polynom pM1 = this.ExtractMonoms(pSm, sVar, iPow1);
			final Polynom pM2 = this.ExtractMonoms(pGr, sVar, iPow2);
			final Polynom pM1Adj = this.Mult(pSm, pM2);
			final Polynom pR = this.Diff(
					(iPow1 < iPow2) ? this.Mult(pM1Adj, new Monom(sVar, iPow2 - iPow1)) : pM1Adj,
					this.Mult(pGr, pM1));
			final int iPowR = this.MaxPow(pR, sVar);
			if(iPowR <= 1) return pR;
			if(iPowR <= iPow1) {
				pGr = pSm; pSm = pR;
				iPow2 = iPow1; iPow1 = iPowR;
				System.out.println("Smaller: " + iPowR);
			} else if(iPowR <= iPow2) {
				pGr = pR;
				iPow2 = iPow1;
				System.out.println("Intermediate: " + iPowR);
			} else {
				System.out.println("GCD Error: should NOT happen!");
			}
		}
	}
	
	// ++++ Add ++++
	
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
		// extract all Monomes containing sVarName at power iPow;
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
			// extract Monomes containing sVarName
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
	
	// +++ Replace: Var^p with pReplace
	public Polynom Replace(final Polynom p, final String sVarName, final int iPow, final Polynom pRepl) {
		final Polynom pTemp = this.Replace(p, sVarName, iPow, "_tmp_" + sVarName);
		return this.Replace(pTemp, "_tmp_" + sVarName, pRepl);
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
	public Polynom ReplaceOrMult(final Polynom p, final String sVarName,
			final Polynom pW, final Polynom pMult, final int maxPow) {
		// Replaces with pW vs multiplies with pMult
		// pW^k1 * pMult^k2, with k1 + k2 = maxPow;
		final Vector<Polynom> vPolyPow = this.PowAll(pW, maxPow); // powers
		final Vector<Polynom> vMultPow = this.PowAll(pMult, maxPow); // powers

		final String sRootName = p.sRootName;
		Polynom polyRez =  new Polynom(sRootName);
		
		for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
			final Integer nPow = entryM.getKey().get(sVarName);
			if(nPow == null) {
				final Polynom pAdd =
						this.MultInPlace(
						this.Mult(vMultPow.lastElement(), entryM.getKey()), entryM.getValue(), 1.0d);
				polyRez = this.Add(polyRez, pAdd);
				continue;
			}
			final Monom mRemaining = new Monom(entryM.getKey());
			mRemaining.remove(sVarName);
			// Multiply with Coeff
			final Polynom polyTerm =
					this.Mult(vMultPow.get(maxPow - nPow), // pos(0) == 1
					this.Mult(vPolyPow.get(nPow),
							new Polynom(mRemaining, entryM.getValue(), sRootName)));
			polyRez = this.Add(polyRez, polyTerm);
		}
		
		return polyRez;
	}
	
	public Vector<Polynom> Replace(final Vector<Polynom> vP1, final String [] sVars, final Vector<Polynom> vReplace) {
		final Vector<Polynom> vRez = new Vector<> ();
		
		for(final Polynom p : vP1) {
			Polynom pR = p;
			for(int iV=0; iV < sVars.length; iV++) {
				pR = this.Replace(pR, sVars[iV], vReplace.get(iV));
			}
			vRez.add(pR);
		}
		
		return vRez;
	}

	public Polynom Expand(final Polynom p, final String [] sVar, final Polynom [] pR) {
		Polynom pRez = new Polynom(p);
		
		for(int id=0; id < sVar.length; id++) {
			pRez = this.Replace(pRez, sVar[id], pR[id]);
		}
		return pRez;
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
	public Polynom Simplify(final Polynom pOriginal, final PowGrade powGrade) {
		final Pair<Polynom, Polynom> pairUnity = Unity.EvalSeq(powGrade);
		if(pairUnity == null) {
			return pOriginal;
		}
		// TODO: reuse code
		while(true) {
			Monom mFirst = pairUnity.key.firstKey();
			final Monom mBase = this.Match(pOriginal, mFirst);
			// TODO: break, if the entire Seq is NOT found;
			if(mBase == null) { break; }
			final Polynom pModify = this.Mult(pairUnity.key, mBase);
			final double dCoeff = - this.HasSeq(pOriginal, pModify);
			System.out.println(pModify.toString() + ": " + dCoeff);
			
			if(dCoeff == 0) { break; }
			
			final Polynom pModifyCoeff =
					this.MultInPlace(pModify, dCoeff);
			final Polynom pAddCoeff =
					this.MultInPlace(
					this.Mult(pairUnity.val, mBase), -dCoeff);
			this.AddInPlace(pOriginal, pModifyCoeff);
			this.AddInPlace(pOriginal, pAddCoeff);
			System.out.println(pModifyCoeff);
			System.out.println(pAddCoeff);
		}
		
		return pOriginal;
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
	
	public Pair<Polynom, Polynom> Match(final Polynom pMonoms, final Polynom pSubSeq,
			final String sSubSeqName, final PowGrade powGrade) {
		// matches the SubSeq and computes the minimal matching Coeffs
		// the Coeffs are inverted;
		final int MAX_MISSING = (powGrade.iPow == 15 || powGrade.iPow == 25) ? 4 : 2; // TODO
		final int lenMin = pSubSeq.size() - MAX_MISSING;
		final Polynom pRez = new Polynom(pMonoms.sRootName);
		final Polynom pNew = new Polynom(pMonoms.sRootName);
		
		int iRepeats = powGrade.iPow; // /2 ???
		
		REPEATS:
		while(iRepeats > 0) {
			iRepeats --;
			final Iterator<Monom> itSeq = pSubSeq.keySet().iterator();
			
			int npos = 0;
			while(itSeq.hasNext()) {
				final Monom m = itSeq.next();
				final Monom mVar = this.Match(pMonoms, m, pMonoms.sRootName);
				if(mVar == null) { npos ++; continue; }
				// found 1st Term
				final Monom mCurrent = new Monom(mVar).Add(m);
				// System.out.println(mVar);
				// System.out.println(mCurrent);
				
				final double [] dCoeffs = new double [pSubSeq.size()];
				dCoeffs[npos++] = pMonoms.getOrDefault(mCurrent, 0d);
				// find all terms
				while(itSeq.hasNext()) {
					final Monom mNext = new Monom(itSeq.next()).Add(mVar);
					dCoeffs[npos++] = pMonoms.getOrDefault(mNext, 0d);
				}
				// Check what is missing
				final double dCoeffCommon = - this.CommonCoefficient(dCoeffs, lenMin);
				if(dCoeffCommon == 0) {
					continue REPEATS;
				}
				for(final Monom mKeys : pSubSeq.keySet()) {
					final Monom mNewSeq = new Monom(mKeys).Add(mVar);
					pRez.Add(mNewSeq, dCoeffCommon);
					// need to Remove to process higher Powers
					pMonoms.remove(mNewSeq);
				}
				pNew.Add(new Monom(mVar), -dCoeffCommon);
			}
		}
		
		
		return new Pair<>(pRez, pNew);
	}
	
	public Monom Match(final Polynom pMonoms, final Monom mToFind, final String sOtherVar) {
		// must match "exactly", except for sOtherVar;
		// [currently ignores powers]
		EXTERNAL:
		for(final Monom m : pMonoms.keySet()) {
			if(m.size() < mToFind.size()) { continue; }
			int countMatchedVars = 0;
			Monom mVar = null;
			for(final Map.Entry<String, Integer> entryM : m.entrySet()) {
				if(entryM.getKey().equals(sOtherVar)) {
					mVar = new Monom(entryM.getKey(), entryM.getValue());
					continue;
				}
				final Integer iPow = mToFind.get(entryM.getKey());
				if(iPow == null) {
					continue EXTERNAL;
				}
				countMatchedVars ++;
			}
			if(countMatchedVars < mToFind.size()) { continue; }
			if(mVar == null) {
				mVar = new Monom();
			}
			return mVar;
		}
		return null;
	}
	public Monom Match(final Polynom pMonoms, final Monom mToFind) {
		// common vars must match exactly;
		// any other variables can be present in pMonoms;
		EXTERNAL:
		for(final Monom mP : pMonoms.keySet()) {
			if(mP.size() < mToFind.size()) { continue; }
			
			for(final Map.Entry<String, Integer> entryVars : mToFind.entrySet()) {
				final Integer iPow = mP.get(entryVars.getKey());
				if(iPow == null || iPow != entryVars.getValue()) {
					continue EXTERNAL;
				}
			}
			return this.Div(mP, mToFind);
		}
		return null;
	}
	
	public Monom FindFirst(final Polynom p, final String sVar) {
		for(final Monom m : p.keySet()) {
			if(m.containsKey(sVar)) {
				return m;
			}
		}
		return null;
	}
	public Polynom FindAll(final Polynom p, final String sVar) {
		final Polynom pMonoms = new Polynom (p.sRootName);
		for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
			if(entryM.getKey().containsKey(sVar)) {
				pMonoms.put(entryM.getKey(), entryM.getValue());
			}
		}
		return pMonoms;
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
	public double HasSeq(final Polynom p1, final Polynom pSeq) {
		final int len = pSeq.size();
		final double [] dCoeffs = new double[len];
		
		int npos = 0;
		for(final Monom m : pSeq.keySet()) {
			dCoeffs[npos++] = p1.getOrDefault(m, 0d);
		}
		return this.CommonCoefficient(dCoeffs, len);
	}
	
	// 
	public Polynom ExpandSqrt(final Polynom pSq, final Polynom pMult, final Polynom pSqrt, final int iPow) {
		// pSq & pMult are raised to Power iPow
		// pSq^iPow - pSqrt * pMult^iPow;
		return
				this.Diff(this.Pow(pSq, iPow),
				this.Mult(this.Pow(pMult, iPow), pSqrt));
		
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
	public Polynom Pow(final Polynom p, final int iPow) {
		if(iPow == 1) {
			return new Polynom(p); // copy of original!
		}
		Polynom pAcc = new Polynom(1, p.sRootName);
		Polynom pValPow = new Polynom(p);
		for(int iPowA = iPow; iPowA > 0; ) {
			if(iPowA == 1) {
				iPowA --;
			} else if(iPowA % 2 == 1) {
				pAcc = this.Mult(pAcc, pValPow);
				iPowA --;
			} else {
				pValPow = this.Mult(pValPow, pValPow);
				iPowA /= 2;
			}
		}
		// System.out.println("" + iPow + ": " + dAcc * dValPow);
		return this.Mult(pValPow, pAcc);
	}
	public Vector<Polynom> PowAll(final Polynom p, final int iPow) {
		// ALL powers: pos(0) == 1; (simplifies code in various functions)
		final Vector<Polynom> vP = new Vector<> (iPow);
		vP.add(new Polynom(1, p.sRootName));
		vP.add(new Polynom(p));
		if(iPow == 1) {
			return vP; // copy of original!
		}
		for(int i = 2; i <= iPow; i++) {
			vP.add(this.Mult(vP.lastElement(), p));
		}
		return vP;
	}
	
	public double CommonCoefficient(final double [] dCoeffs, final int lenMin) {
		// Check what is missing
		int countPos = 0;
		double dCoeffPos = Double.POSITIVE_INFINITY;
		int countNeg = 0;
		double dCoeffNeg = Double.NEGATIVE_INFINITY;
		for(int npos=0; npos < dCoeffs.length; npos++) {
			if(dCoeffs[npos] > 0) {
				countPos ++;
				if(dCoeffPos > dCoeffs[npos]) { dCoeffPos = dCoeffs[npos]; }
			} else if(dCoeffs[npos] < 0) {
				countNeg ++;
				if(dCoeffNeg < dCoeffs[npos]) { dCoeffNeg = dCoeffs[npos]; }
			}
		}
		//
		final double dCoeffCommon;
		if(countPos > countNeg && countPos >= lenMin) {
			dCoeffCommon = dCoeffPos;
		} else if(countNeg >= lenMin) {
			dCoeffCommon = dCoeffNeg;
		} else {
			dCoeffCommon = 0;
		}
		return dCoeffCommon;
	}
	
	public Polynom ExtractMonoms(final Polynom p, final String sVar, final int iPow) {
		// extracts only Monoms that have sVar^iPow;
		// removes sVar;
		final Polynom pR = new Polynom(p.sRootName);
		
		for(final Map.Entry<Monom, Double> entryM : p.entrySet()) {
			final Integer powM = entryM.getKey().get(sVar);
			if(powM == null) continue;
			if(powM != iPow) continue;
			final Monom m = new Monom(entryM.getKey());
			m.remove(sVar);
			pR.Add(m, entryM.getValue());
		}
		
		return pR;
	}
}
