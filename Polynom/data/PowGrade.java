/*
 * Roots of Unity Data-Classes
 * 
 * (C) Leonard Mada
 * 
 * License: GPL v.3
 * 
 * */
package data;

public class PowGrade {
	
	public final int iPow;
	public final double dVal;
	
	public PowGrade(final int iPow, final double dVal) {
		this.iPow = iPow;
		this.dVal = dVal;
	}
	
	public int MaxPow() {
		return iPow - 1;
	}
}
