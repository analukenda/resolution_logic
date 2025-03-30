package ui;

public class Literal {
	private String name;
	private boolean sign;

	public Literal(String name, boolean sign) {

		this.name = name;
		this.sign = sign;
	}

	public String getName() {
		return name;
	}

	public boolean getSign() {
		return sign;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (sign ? 1231 : 1237);
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
		Literal other = (Literal) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sign != other.sign)
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (!sign)
			return "~" + name;
		return name;
	}

}
