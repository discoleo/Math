package io;

import math.MathTools;
import math.Monom;
import math.Polynom;

/*
 * The Polynomial Parser
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
public class Parser {
	
	protected boolean isVarNumeric = true;
	
	public enum STATE {
		START, ERR, EQ, END,
		SEMN_POZ, SEMN_NEG,
		PARENTH_START, PARENTH_END,
		NUM, NUM_SEP, NUM_FLOAT,
		VAR, VAR_NUM, VAR_END,
		MULT, POW, POW_NUM, POW_NEG;
	}

	public Polynom Parse(final String sInput, final String sVar) {
		final SIterator it = new SIterator(sInput, 0);
		return this.Parse(sVar, it, STATE.END);
	}
	public Polynom Parse(final String sVar, final SIterator it, final STATE stareEnd) {
		final PolyGenerator factoryPoly = new PolyGenerator(sVar);
		
		final boolean isVarNumeric = this.isVarNumeric;
		STATE stare = STATE.START;
		
		while(it.HasNext()) {
			if(stare == stareEnd) {
				factoryPoly.AddMonom();
				// System.out.println("SubExp = " + factoryPoly.GetPolynom());
				return factoryPoly.GetPolynom();
			}
			final char ch = it.Next();
			
			switch(stare) {
			case START :
			case EQ : {
				if(ch == ' ') {
					// Nothing: eat space character
				} else if(ch == '+') {
					stare = STATE.SEMN_POZ;
				} else if(ch == '-') {
					stare = STATE.SEMN_NEG;
					factoryPoly.SetMinus();
				} else if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
					stare = STATE.VAR;
					factoryPoly.SetVar(ch);
				} else if(ch >= '0' && ch <= '9') {
					stare = STATE.NUM;
					factoryPoly.SetCoefficient(ch);
				} else if(ch == '(') {
					// stare = STATE.PARENTH_START;
					this.SubExpr(factoryPoly, sVar, it);
					stare = STATE.VAR_END;
				} else if(ch == '*' || ch == '=' || ch == '^') {
					it.Error(stare);
					stare = STATE.ERR;
				} // else if(...) { etc. }
				break;
			}
			case SEMN_NEG:
			case SEMN_POZ: {
				if(ch == ' ') {
					// Nothing: eat space character
				} else if(ch == '+') {
					// Warning
				} else if(ch == '-') {
					// Warning
					// Sign is actually handled by factoryPoly
					if(stare == STATE.SEMN_NEG) {
						stare = STATE.SEMN_POZ;
						factoryPoly.SetMinus();
					} else {
						stare = STATE.SEMN_NEG;
						factoryPoly.SetMinus();
					}
				} else if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
					stare = STATE.VAR;
					factoryPoly.SetVar(ch);
				} else if(ch >= '0' && ch <= '9') {
					stare = STATE.NUM;
					factoryPoly.SetCoefficient(ch);
				} else if(ch == '(') {
					// stare = STATE.PARENTH_START;
					// TODO: sign "-"
					this.SubExpr(factoryPoly, sVar, it);
					stare = STATE.VAR_END;
				} else if(ch == '*' || ch == '=' || ch == '^') {
					it.Error(stare);
					stare = STATE.ERR;
				} // else if(...) { etc. }
				break;
			}
			case VAR_NUM :
			case VAR : {
				if(ch == ' ') {
					factoryPoly.EndVar();
				} else if(ch == '+') {
					stare = STATE.SEMN_POZ;
					factoryPoly.AddMonom();
				} else if(ch == '-') {
					stare = STATE.SEMN_NEG;
					factoryPoly.AddMonom();
					factoryPoly.SetMinus();
				} else if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
					if(stare == STATE.VAR_NUM) {
						factoryPoly.EndVar();
						stare = STATE.VAR;
					}
					// stare = STARE.VAR;
					factoryPoly.SetVar(ch);
				} else if(ch >= '0' && ch <= '9') {
					if(isVarNumeric) {
						stare = STATE.VAR_NUM;
						factoryPoly.SetVar(ch);
					} else {
						stare = STATE.POW;
						// factoryPoly.EndVar();
						factoryPoly.SetPower(ch);
					}
				} else if(ch == '^') {
					stare = STATE.POW;
					factoryPoly.EndVar();
				} else if(ch == '*') {
					stare = STATE.MULT;
					factoryPoly.EndVar();
				} else if(ch == '=') {
					stare = this.ParseEq(factoryPoly, it, stare);
				} else if(ch == ')') {
					stare = STATE.PARENTH_END;
				} // else if(...) { etc. }
				break;
			}
			case POW_NEG:
			case POW : {
				if(ch == ' ') {
					// NOTHING
				} else if(ch >= '0' && ch <= '9') {
					factoryPoly.SetPower(ch);
					if(stare == STATE.POW_NEG) {
						factoryPoly.SetPowerNeg(); // hack
					}
					stare = STATE.POW_NUM;
				} else if(ch == '.') {
					it.Error(stare);
					stare = STATE.ERR;
					// currently not supported
				} else if(ch == '=') {
					stare = this.ParseEq(factoryPoly, it, stare);
				} else if(ch == ')') {
					stare = STATE.PARENTH_END;
				} else if(ch == '-') {
					stare = STATE.POW_NEG;
					it.Error(stare); // ERR
				} else {
					it.Error(stare);
					stare = STATE.ERR;
				}
				break;
			}
			case POW_NUM : {
				if(ch == ' ') {
					stare = STATE.VAR_END;
					factoryPoly.EndVar();
				} else if(ch >= '0' && ch <= '9') {
					// stare = STARE.POW_NUM;
					factoryPoly.SetPower(ch);
				} else if(ch == '.') {
					it.Error(stare);
					stare = STATE.ERR;
					// currently not supported
				} else if(ch == '+') {
					stare = STATE.SEMN_POZ;
					factoryPoly.AddMonom();
				} else if(ch == '-') {
					stare = STATE.SEMN_NEG;
					factoryPoly.AddMonom();
					factoryPoly.SetMinus();
				} else if(ch == '*') {
					stare = STATE.MULT;
				} else if(ch == '=') {
					stare = this.ParseEq(factoryPoly, it, stare);
				} else if(ch == ')') {
					stare = STATE.PARENTH_END;
				} else {
					it.Error(stare);
					stare = STATE.ERR;
				}
				break;
			}
			case VAR_END : {
				if(ch == ' ') {
					// stare = STARE.VAR_END;
					// factoryPoly.EndVar();
				} else if(ch >= '0' && ch <= '9') {
					// stare = STARE.POW_NUM;
					factoryPoly.SetCoefficient(ch); // TODO: Warning
				} else if(ch == '+') {
					stare = STATE.SEMN_POZ;
					factoryPoly.AddMonom();
				} else if(ch == '-') {
					stare = STATE.SEMN_NEG;
					factoryPoly.AddMonom();
					factoryPoly.SetMinus();
				} else if(ch == '=') {
					stare = this.ParseEq(factoryPoly, it, stare);
				} else if(ch == '(') {
					// stare = STARE.PARENTH_START;
					this.SubExpr(factoryPoly, sVar, it);
					stare = STATE.VAR_END;
				} else if(ch == ')') {
					stare = STATE.PARENTH_END;
				} else {
					// TODO: "k^2 x" => "k^2*x"
					it.Error(stare);
					stare = STATE.ERR;
				}
				break;
			}
			case MULT : {
				if(ch >= '0' && ch <= '9') {
					stare = STATE.NUM;
					factoryPoly.SetCoefficient(ch);
				} else if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
					stare = STATE.VAR;
					factoryPoly.SetVar(ch);
				} else if(ch == '(') {
					// stare = STARE.PARENTH_START;
					// TODO: MULT
					this.SubExpr(factoryPoly, sVar, it);
					stare = STATE.VAR_END;
				} else if(ch == '*' || ch == '=' || ch == '^') {
					it.Error(stare);
					stare = STATE.ERR;
				} // else if(...) { etc. }
				break;
			}
			// Coeff
			case NUM : {
				if(ch >= '0' && ch <= '9') {
					// stare = STARE.NUM;
					factoryPoly.SetCoefficient(ch);
				} else if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
					stare = STATE.VAR;
					factoryPoly.SetVar(ch);
				} else if(ch == '.') {
					stare = STATE.NUM_SEP;
					factoryPoly.SetFloat();
				} else if(ch == '*') {
					stare = STATE.MULT;
				} else if(ch == '=') {
					stare = this.ParseEq(factoryPoly, it, stare);
				} else if(ch == '+') {
					stare = STATE.SEMN_POZ;
					factoryPoly.AddMonom();
				} else if(ch == '-') {
					stare = STATE.SEMN_NEG;
					factoryPoly.AddMonom();
					factoryPoly.SetMinus();
				} else if(ch == '(') {
					// stare = STARE.PARENTH_START;
					// TODO: mult
					this.SubExpr(factoryPoly, sVar, it);
					stare = STATE.VAR_END;
				} else if(ch == ')') {
					stare = STATE.PARENTH_END;
				} // else if(...) { etc. } // TODO: '^'
				break;
			}
			case NUM_SEP : {
				if(ch >= '0' && ch <= '9') {
					stare = STATE.NUM_FLOAT;
					factoryPoly.SetCoefficient(ch);
				} else {
					it.Error(stare);
					stare = STATE.ERR;
				}
			}
			case NUM_FLOAT : {
				if(ch >= '0' && ch <= '9') {
					// stare = STARE.NUM_FLOAT;
					factoryPoly.SetCoefficient(ch);
				} else if((ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z')) {
					stare = STATE.VAR;
					factoryPoly.SetVar(ch);
				} else if(ch == '.') {
					it.Error(stare);
					stare = STATE.ERR;
				} else if(ch == '*') {
					stare = STATE.MULT;
				} else if(ch == '=') {
					stare = this.ParseEq(factoryPoly, it, stare);
				} else if(ch == '+') {
					stare = STATE.SEMN_POZ;
					factoryPoly.AddMonom();
				} else if(ch == '-') {
					stare = STATE.SEMN_NEG;
					factoryPoly.AddMonom();
					factoryPoly.SetMinus();
				} else if(ch == '(') {
					// stare = STARE.PARENTH_START;
					// TODO: MULT
					this.SubExpr(factoryPoly, sVar, it);
					stare = STATE.VAR_END;
				} else if(ch == ')') {
					stare = STATE.PARENTH_END;
				} // else if(...) { etc. } // TODO: '^'
				break;
			}
			// ...
			}
			
			it.Inc();
		}
		if(factoryPoly.HasMonom()) {
			// System.out.println(pRez.toString());
			factoryPoly.AddMonom();
		}
		
		return factoryPoly.GetPolynom();
	}
	
	protected void SubExpr(final PolyGenerator factoryPoly, final String sVar, final SIterator it) {
		it.Inc();
		final Polynom pRezSubExpr = this.Parse(sVar, it, STATE.PARENTH_END);
		factoryPoly.Add(pRezSubExpr);
	}
	protected STATE ParseEq(final PolyGenerator factoryPoly, final SIterator it, final STATE stare) {
		factoryPoly.AddMonom();
		if(factoryPoly.SetAllNegativ()) {
			it.Error(stare);
			return STATE.ERR;
		} else {
			return STATE.EQ;
		}
	}
	
	// ++++++++ helper classes +++++++++++
	
	protected class SIterator {
		// pozitia in String
		protected int npos = 0;
		protected final String sInput;
		// +++++++++++++++++
		public SIterator(final String sInput, final int nposStart) {
			this.npos = nposStart;
			this.sInput = sInput;
		}
		public boolean HasNext() {
			return npos < sInput.length();
		}
		public char Next() {
			return sInput.charAt(npos);
		}
		public void Inc() {
			npos ++;
		}
		public void Error(final STATE state) {
			System.out.println("Error: pos = " + npos + "; " + state);
			System.out.println("Error: " + sInput.substring(npos > 1 ? (npos - 2) : 0));
		}
	}
	
	public class PolyGenerator {
		// aici se vor genera polinoamele
		private final MathTools math = new MathTools();

		boolean isAllNegativ = false;
		// semnul coeficientului
		boolean bSign = false;

		boolean isFloat = false;
		int iCoeff = 0;
		int iFloatPart = 0;
		int iFloatLen  = 0;
		//
		boolean isVarEnd = false;
		int iPow   = 0;
		
		// TODO: Vector<> sau TreeMap<> cu variabilele
		final Polynom pRez;
		Monom m = new Monom();
		String sVar = "";

		// TODO: "123 * 321", "123^4"
		
		public PolyGenerator(final String sVarName) {
			this.pRez = new Polynom(sVarName);
		}
		
		// ++++ Functii ++++
		
		public Polynom GetPolynom() {
			return pRez;
		}
		public Polynom Add(final Polynom pAdd) {
			// add in-place
			math.AddInPlace(pRez, pAdd);
			return pRez;
		}
		
		public void Init() {
			m = new Monom();
			sVar = "";
			isVarEnd = false;
			iPow = 0;
			
			bSign = false;
			isFloat = false;
			iCoeff = 0;
			iFloatPart = 0;
			iFloatLen  = 0;
		}
		
		public void AddMonom() {
			if(this.HasMonom()) {
				pRez.Add(this.GetMonom(), this.GetCoeff());
			}
		}
		public boolean HasMonom() {
			return ! sVar.isEmpty() || iCoeff != 0 || iFloatPart != 0 ;
		}
		public Monom GetMonom() {
			AddVar();
			return m;
		}
		public double GetCoeff() {
			if(isFloat && iFloatPart != 0) {
				// TODO: POWER
				return ((double) iFloatPart) / 10 * iFloatLen + iCoeff;
			} else if( ! m.isEmpty() && iCoeff == 0 && iFloatPart == 0) {
				iCoeff = 1;
			}
			if(isAllNegativ) {
				bSign = ! bSign;
			}
			final int iCoeff = (bSign) ? -this.iCoeff : this.iCoeff;
			this.Init();
			return iCoeff;
		}
		public boolean SetAllNegativ() {
			if(isAllNegativ) {
				return true; // ERROR
			}
			isAllNegativ = true;
			return false; // 1st time is OK
		}
		public void SetMinus() {
			bSign = ! bSign;
		}
		public void SetVar(final char ch) {
			if(isVarEnd) {
				AddVar();
			}
			sVar += ch;
			// TODO:
			// puteti folosi o optiune, daca sunt acceptate variabile cu >=2 caractere
		}
		public void EndVar() {
			isVarEnd = true;
		}
		protected void InitVar() {
			sVar = "";
			iPow = 0;
			isVarEnd = false;
		}
		protected void AddVar() {
			if( ! sVar.isEmpty()) {
				if(iPow == 0) {
					iPow = 1;
				}
				m.Add(sVar, iPow);
				InitVar();
			}
		}
		public void SetCoefficient(final char ch) {
			if(isFloat) {
				iFloatPart = iFloatPart * 10 + (ch - '0');
				iFloatLen ++ ;
			} else {
				iCoeff = iCoeff * 10 + (ch - '0');
			}
		}
		public void SetFloat() {
			isFloat = true;
		}
		public void SetPower(final char ch) {
			iPow = iPow * 10 + (ch - '0');
		}
		public void SetPowerNeg() {
			iPow = -iPow; // hack
		}
	}
}
