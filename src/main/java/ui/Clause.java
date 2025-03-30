package ui;

import java.util.HashSet;
import java.util.LinkedHashSet;

public class Clause {
	private HashSet<Literal> literals;
	private Parents parents;

	public Clause(Parents parents) {

		literals = new HashSet<Literal>();
		this.parents = parents;

	}

	public HashSet<Literal> getLiterals() {
		return literals;
	}

	public void setLiterals(HashSet<Literal> literals) {
		this.literals = literals;
	}

	public Parents getParents() {
		return parents;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + literals.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Clause other = (Clause) obj;

		if (!literals.equals(other.literals))
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (literals.isEmpty())
			return "NIL";
		
		String s = "";
		for (Literal l : literals) 
			s += l + " v ";
		s = s.substring(0, s.length() - 3);
		return s;

	}

	public boolean subset(Clause other) {
		HashSet<Literal> o = other.getLiterals();
		if (literals.containsAll(o))
			return true;
		return false;
	}

}
