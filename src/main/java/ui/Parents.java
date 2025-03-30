package ui;

public class Parents {
	private Clause firstParent;
	private Clause secondParent;

	public Parents(Clause firstParent, Clause secondParent) {

		this.firstParent = firstParent;
		this.secondParent = secondParent;
	}

	@Override
	public String toString() {
		return "(" + firstParent + ", " + secondParent + ")";
	}

	public Clause getFirstParent() {
		return firstParent;
	}

	public Clause getSecondParent() {
		return secondParent;
	}

	@Override
	public boolean equals(Object o) {
		if (!o.getClass().equals(getClass()))
			return false;
		Parents p = (Parents) o;
		return (firstParent.equals(p.firstParent) && secondParent.equals(p.secondParent))
				|| (firstParent.equals(p.secondParent) && secondParent.equals(p.firstParent));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((firstParent == null) ? 0
				: firstParent.hashCode() + ((secondParent == null) ? 0 : secondParent.hashCode()));

		return result;
	}

}
