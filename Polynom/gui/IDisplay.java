package gui;

import data.PolySeq;
import data.Polynom;

public interface IDisplay {

	void Display(String sTitle);

	void Display(Polynom p);
	void Display(PolySeq p);

}