package ui;

public class Command {
	private Clause clause = new Clause(null);
	private String operation;

	public Command(Clause clause, String operation) {

		this.clause = clause;
		this.operation = operation;
	}

	public Clause getClause() {
		return clause;
	}

	public String getOperation() {
		return operation;
	}

	@Override
	public String toString() {
		return clause + " " + operation;
	}

}
