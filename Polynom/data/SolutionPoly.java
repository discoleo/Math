package data;

import java.util.Vector;

public class SolutionPoly extends Vector<Pair<String, Polynom>> {

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		for(final Pair<String, Polynom> pair : this) {
			sb.append(pair.key).append(" = ");
			sb.append(pair.val.toString());
			sb.append("\n");
		}
		
		return sb.toString();
	}
}
