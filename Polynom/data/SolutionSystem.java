package data;

import java.util.Vector;


public class SolutionSystem extends Vector<Pair<Polynom, Polynom>> {
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		
		for(final Pair<Polynom, Polynom> pair : this) {
			sb.append(pair.key.toString());
			sb.append(" = ");
			sb.append(pair.val.toString()).append("\n");
		}
		
		return sb.toString();
	}
}
