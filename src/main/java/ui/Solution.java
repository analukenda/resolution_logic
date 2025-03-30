package ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Solution {

	private static LinkedHashSet<Clause> clauses = new LinkedHashSet<Clause>();
	private static HashSet<Clause> sos = new HashSet<Clause>();
	private static ArrayList<String> ispis = new ArrayList<String>();
	private static ArrayList<Clause> data = new ArrayList<Clause>();
	private static Clause resolvent;
	private static HashSet<Parents> alreadyResolved = new HashSet<Parents>();
	private static ArrayList<Command> commands = new ArrayList<Command>();

	public static void main(String... args) throws FileNotFoundException {

		String task = args[0];
		File f = new File(args[1]);
		Scanner sc = new Scanner(f);

		String line = sc.nextLine();
		while (!line.equals("") && line != null) {
			line = line.trim().toLowerCase();
			if (line.startsWith("#")) {
				try {
					line = sc.nextLine();
					continue;
				} catch (NoSuchElementException ex) {
					break;
				}
			}

			Clause clause = new Clause(null);
			String[] literals = line.split(" v ");
			
			for (String s : literals) {
                if (s.startsWith("~"))
					clause.getLiterals().add(new Literal(s.substring(1), false));
				else
					clause.getLiterals().add(new Literal(s, true));
			}
			data.add(clause);

			try {
				line = sc.nextLine();
			} catch (NoSuchElementException ex) {
				break;
			}
		}

		if (task.equals("resolution")) {
            
			
            if (!resolution())
				System.out.println("[CONCLUSION]: " + resolvent + " is unknown");
			else {
				ispis.add("[CONCLUSION]: " + resolvent + " is true");
				ispis();
			}
		}

		else if (task.equals("cooking")) {
			f = new File(args[2]);
			sc = new Scanner(f);
			line = sc.nextLine();
			while (!line.equals("") && line != null) {
				line = line.trim().toLowerCase();
				if (line.startsWith("#")) {
					try {
						line = sc.nextLine();
						continue;
					} catch (NoSuchElementException ex) {
						break;
					}
				}
				int length = line.length();
				Clause clause = new Clause(null);
				String[] literals = line.substring(0, length - 2).split(" v ");
				for (String s : literals) {
					s = s.trim();
					if (s.startsWith("~"))
						clause.getLiterals().add(new Literal(s.substring(1), false));
					else
						clause.getLiterals().add(new Literal(s, true));
				}
				commands.add(new Command(clause, line.substring(line.length() - 1)));
				try {
					line = sc.nextLine();

				} catch (NoSuchElementException ex) {
					break;
				}
			}
			cooking();
		}

	}

	public static boolean resolution() {
		int index = data.size() - 1;
		resolvent = data.get(index);
        data.remove(index);
		
        HashSet<Clause> resClauses = new HashSet<Clause>();

		for (Literal l : resolvent.getLiterals()) {
            Clause neg = new Clause(null);
			neg.getLiterals().add(new Literal(l.getName(), !l.getSign()));
            data.add(neg);
			resClauses.add(neg);
		}

		for (Clause clause : data) {
			if (!redundant(clause) && !unimportant(clause)) {
				clauses.add(clause);
				if (resClauses.contains(clause)) {
					sos.add(clause);
				}
				ispis.add(clause.toString());
			}
		}

		ispis.add("----------------------------------------");
		
		HashSet<Clause> newClauses = new HashSet<Clause>();

		while (true) {
			Iterator<Clause> it = clauses.iterator();
			HashSet<Clause> remained = new HashSet<Clause>(sos);

			while (it.hasNext()) {
				Clause parent_1 = it.next();
                remained.remove(parent_1);

				for (Clause parent_2 : remained) {
					Parents parents = new Parents(parent_1, parent_2);
					if (!alreadyResolved.contains(parents)) {
						HashSet<Clause> resolvents = plResolve(parent_1, parent_2);

						if (!resolvents.isEmpty()) {
							for (Clause cl : resolvents) {
								if (cl.getLiterals().isEmpty()) {
									finish(cl);
									return true;
								}
							}
							newClauses.addAll(resolvents);
						}
					}
				}
			}
			
			if (clauses.containsAll(newClauses))
				return false;
			HashSet<Clause> pom = new HashSet<Clause>();
			
			for (Clause c : newClauses) {
				if (!redundant(c) && !unimportant(c))
					pom.add(c);
			}
			
			clauses.addAll(pom);
			sos.addAll(pom);

		}

	}

	public static boolean unimportant(Clause clause) {
		Literal[] arr = Arrays.copyOf(clause.getLiterals().toArray(), 
				                      clause.getLiterals().toArray().length, Literal[].class);
		for (int i = 0; i < arr.length; ++i) {
			for (int j = i + 1; j < arr.length; ++j) {
				if (arr[i].getName().equals(arr[j].getName())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean redundant(Clause clause) {
		for (Clause c : clauses) {
			if (c.equals(clause))
				continue;
			if (clause.subset(c))
				return true;
		}
		return false;
	}

	public static HashSet<Clause> plResolve(Clause parent_1, Clause parent_2) {
		HashSet<Clause> children = new HashSet<Clause>();
		HashSet<Literal> literals_1 = parent_1.getLiterals();
		HashSet<Literal> literals_2 = parent_2.getLiterals();
		Parents parents = new Parents(parent_1, parent_2);
		for (Literal l_1 : literals_1) {
			for (Literal l_2 : literals_2) {
				if (l_1.getName().equals(l_2.getName()) && l_1.getSign() != l_2.getSign()) {
					HashSet<Literal> resolved = new HashSet<Literal>();
					Clause newClause = new Clause(parents);

					if (literals_1.size() != 1 || literals_2.size() != 1) {
						resolved.addAll(literals_1);
						resolved.addAll(literals_2);
						resolved.remove(l_1);
						resolved.remove(l_2);
						newClause.setLiterals(resolved);
						children.add(newClause);
					} else {
						newClause.setLiterals(resolved);
						children.add(newClause);
						return children;
					}
				}
			}
		}
		
		alreadyResolved.add(parents);
		return children;
	}

	public static void finish(Clause clause) {
		ArrayList<Clause> findParents = new ArrayList<Clause>();
		LinkedHashMap<Clause, Parents> used = new LinkedHashMap<Clause, Parents>();
		findParents.add(clause);
		used.put(clause, clause.getParents());
		while (!findParents.isEmpty()) {
			clause = findParents.get(0);

			Parents parents = clause.getParents();
			if (parents != null) {
				Clause parent_1 = parents.getFirstParent();
				Clause parent_2 = parents.getSecondParent();

				if (parent_1.getParents() != null) {
					findParents.add(parent_1);
					used.put(parent_1, parent_1.getParents());
				}
				if (parent_2.getParents() != null) {
					findParents.add(parent_2);
					used.put(parent_2, parent_2.getParents());
				}
			}
			findParents.remove(0);

		}
		
		ArrayList<String> pom = new ArrayList<String>();
		for (Map.Entry<Clause, Parents> entry : used.entrySet()) {
			pom.add(entry.getKey() + " " + entry.getValue());

		}
		
		Collections.reverse(pom);
		ispis.addAll(pom);
		ispis.add("----------------------------------------");
	}

	public static void cooking() {
		System.out.println("Constructed with knowledge:");
		for (Clause c : data) {
			System.out.println(c);
			if (!redundant(c) && !unimportant(c))
				clauses.add(c);
		}

		for (Command c : commands) {
			System.out.println();
			System.out.println("User's command: " + c);
			Clause clause = c.getClause();
			String operation = c.getOperation();
			if (operation.equals("?")) {
				cookingResolution(clause);
				
				for (Clause cl : sos) {
					clauses.remove(cl);
				}
				sos.clear();
				alreadyResolved.clear();
				ispis.clear();
			} else if (operation.equals("+")) {
				data.add(clause);
				if (!redundant(clause) && !unimportant(clause))
					clauses.add(clause);

				System.out.println("Added " + clause);
			} else if (operation.equals("-")) {
				data.remove(clause);
				clauses.remove(clause);

				System.out.println("removed " + clause);
			}
		}
	}

	public static void cookingResolution(Clause clause) {
		resolvent = clause;
		HashSet<Clause> resClauses = new HashSet<Clause>();

		for (Literal l : resolvent.getLiterals()) {
			Clause neg = new Clause(null);
			neg.getLiterals().add(new Literal(l.getName(), !l.getSign()));
			if (!redundant(neg) && !unimportant(neg)) {
				clauses.add(neg);
				sos.add(neg);
			}
		}
		
		HashSet<Clause> newClauses = new HashSet<Clause>();

		while (true) {
			Iterator<Clause> it = clauses.iterator();
			HashSet<Clause> remained = new HashSet<Clause>(sos);

			while (it.hasNext()) {
				Clause parent_1 = it.next();
				remained.remove(parent_1);

				for (Clause parent_2 : remained) {
					Parents parents = new Parents(parent_1, parent_2);
					if (!alreadyResolved.contains(parents)) {
						HashSet<Clause> resolvents = plResolve(parent_1, parent_2);

						if (!resolvents.isEmpty()) {
							for (Clause cl : resolvents) {
								if (cl.getLiterals().isEmpty()) {
									finishCookingResolution(cl);
									return;
								}
							}
							newClauses.addAll(resolvents);
						}
					}

				}
			}
			
			if (clauses.containsAll(newClauses)) {
				System.out.println("[CONCLUSION]: " + resolvent + " is unknown");
				return;
			}

			HashSet<Clause> pom = new HashSet<Clause>();
			for (Clause c : newClauses) {
				if (!redundant(c) && !unimportant(c))
					pom.add(c);
			}
			clauses.addAll(pom);
			sos.addAll(pom);

		}

	}

	public static void finishCookingResolution(Clause c) {
		ArrayList<Clause> oldClauses = new ArrayList<Clause>();
		ArrayList<Clause> newClauses = new ArrayList<Clause>();
		LinkedHashSet<Clause> oldUsed = new LinkedHashSet<Clause>();
		LinkedHashMap<Clause, Parents> newUsed = new LinkedHashMap<Clause, Parents>();
		
		newClauses.add(c);
		newUsed.put(c, c.getParents());
		
		while (!oldClauses.isEmpty() || !newClauses.isEmpty()) {
			if (!oldClauses.isEmpty())
				c = oldClauses.remove(0);
			else
				c = newClauses.remove(0);

			Parents parents = c.getParents();
			if (parents != null) {
				Clause parent_1 = parents.getFirstParent();
				Clause parent_2 = parents.getSecondParent();
				
				if (parent_1.getParents() != null) {
					newClauses.add(parent_1);
					newUsed.put(parent_1, parent_1.getParents());
				} else {
					oldClauses.add(parent_1);
					oldUsed.add(parent_1);
				}
				if (parent_2.getParents() != null) {
					newClauses.add(parent_2);
					newUsed.put(parent_2, parent_2.getParents());

				} else {
					oldClauses.add(parent_2);
					oldUsed.add(parent_2);
				}
			}

		}
		
		for (Clause cl : oldUsed) {
			ispis.add(cl.toString());

		}
		ispis.add("----------------------------------------");
		
		ArrayList<String> pom = new ArrayList<String>();
		boolean line = false;
		for (Map.Entry<Clause, Parents> entry : newUsed.entrySet()) 
			pom.add(entry.getKey() + " " + entry.getValue());
		
		Collections.reverse(pom);
		ispis.addAll(pom);
		ispis.add("----------------------------------------");
		ispis.add("[CONCLUSION]: " + resolvent + " is true");
		ispis();
	}

	public static void ispis() {
		for (String s : ispis)
			System.out.println(s);
	}

}
