import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Day19 {

	public static class Part {
		
		Map<String, Integer> attributes = new HashMap<String, Integer>();

		public int getSum() {
			return attributes.values().stream().mapToInt(v -> v).sum();
		}
		
	}
	
	public static class Workflow {
		
		public String name;
		public List<Rule> rules = new ArrayList<Day19.Rule>();
		
		public Workflow(String name) {
			this.name= name;
		}
		
		@Override
		public String toString() {
			return name + "{" + rules + "}";
		}

		public void visit(Solutions solutions, Map<String, Workflow> worflows, List<Rule> rulesStack) {
			for (Rule rule : rules) {
				rule.visit(solutions, worflows, rulesStack);
			}
		}
		
	}
	
	public static abstract class Rule {
	
		public String nextWorkflow;
		abstract boolean accept(Part p);

		protected void visit(Solutions solutions, Map<String, Workflow> worflows, List<Rule> rulesStack) {
			rulesStack.add(this);
			if (nextWorkflow.equals("R")) {
				solutions.substract(rulesStack);
			}
			else if (nextWorkflow.equals("A")) {}
			else {
				worflows.get(nextWorkflow).visit(solutions, worflows, rulesStack);
			}
			rulesStack.remove(this);
		}

		protected abstract void substract(Solutions solutions);
	}
	
	public static class GTRule extends Rule {
	
		public String attributeName;
		public int value;
		
		public GTRule(String attributeName, int value, String nextWorkflow) {
			this.attributeName = attributeName;
			this.value = value;
			this.nextWorkflow = nextWorkflow;
		}
		
		@Override
		boolean accept(Part p) {
			if (!p.attributes.containsKey(attributeName)) return false;
			return p.attributes.get(attributeName) > value;
		}
		
		@Override
		public String toString() {
			return attributeName + ">" + value + ":" + nextWorkflow;
		}

		@Override
		protected void substract(Solutions solutions) {
			solutions.substractGT(attributeName, value);
		}
	}
	
	public static class EqualRule extends Rule {
		
		public String attributeName;
		public int value;
		
		public EqualRule(String attributeName, int value, String nextWorkflow) {
				this.attributeName = attributeName;
				this.value = value;
				this.nextWorkflow = nextWorkflow;
		}
		
		@Override
		boolean accept(Part p) {
			if (!p.attributes.containsKey(attributeName)) return false;
			return p.attributes.get(attributeName) == value;
		}
		
		@Override
		public String toString() {
			return attributeName + "=" + value + ":" + nextWorkflow;
		}

		@Override
		protected void substract(Solutions solutions) {
			solutions.substractEQ(attributeName, value);
		}
	}

	public static class LTRule extends Rule {
		
		public String attributeName;
		public int value;
		
		public LTRule(String attributeName, int value, String nextWorkflow) {
				this.attributeName = attributeName;
				this.value = value;
				this.nextWorkflow = nextWorkflow;
		}
		
		@Override
		boolean accept(Part p) {
			if (!p.attributes.containsKey(attributeName)) return false;
			return p.attributes.get(attributeName) < value;
		}
		
		@Override
		public String toString() {
			return attributeName + "<" + value + ":" + nextWorkflow;
		}

		@Override
		protected void substract(Solutions solutions) {
			solutions.substractLT(attributeName, value);
		}
	}

	public static class AcceptTRule extends Rule {
		
		@Override
		boolean accept(Part p) {
			return true;
		}
		
		@Override
		public String toString() {
			return "A";
		}
		
		protected void visit(Solutions solutions, Map<String, Workflow> worflows, List<Rule> rulesStack) {
		}

		@Override
		protected void substract(Solutions solutions) {
		}
	}

	public static class RejectTRule extends Rule {
	
		@Override
		boolean accept(Part p) {
			return true;
		}
		
		@Override
		public String toString() {
			return "R";
		}
		
		protected void visit(Solutions solutions, Map<String, Workflow> worflows, List<Rule> rulesStack) {
			rulesStack.add(this);
			solutions.substract(rulesStack);
			rulesStack.remove(this);
		}

		@Override
		protected void substract(Solutions solutions) {
		}
	}
	
	public static class GotoRule extends Rule {
		
		public GotoRule(String nextWorkflow) {
			this.nextWorkflow = nextWorkflow;
		}
		
		@Override
		boolean accept(Part p) {
			return true;
		}
		
		@Override
		public String toString() {
			return "GOTO " + nextWorkflow;
		}

		@Override
		protected void substract(Solutions solutions) {
		}
	}
	
	public static boolean accept(Part part, Map<String, Workflow> worflows) {
		
		return acceptWorkflow(part, worflows, worflows.get("in"));
	}
	
	

	private static boolean acceptWorkflow(Part part, Map<String, Workflow> worflows, Workflow workflow) {

		boolean accept = false;
		for(Rule rule : workflow.rules) {
			if (rule.accept(part)) {
				if (rule.nextWorkflow != null) {
					if (rule.nextWorkflow.equals("A")) {
						return true;
					}
					else if (rule.nextWorkflow.equals("R")) {
						return false;
					}
					else {
						return acceptWorkflow(part, worflows, worflows.get(rule.nextWorkflow));
					}
				}
				else {
					return rule instanceof AcceptTRule;
				}
			}
		}
		return accept;
	}



	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day19.txt"));

		Map<String, Workflow> worflows = new LinkedHashMap<String, Day19.Workflow>();
		List<Part> parts = new ArrayList<Day19.Part>();
		
		for (String line : lines) {
			if (line.isBlank()) continue;
			if (!line.startsWith("{")) {
				Workflow workflow = new Workflow(line.split("\\{")[0]);
				worflows.put(line.split("\\{")[0], workflow);
				String rulesPart = line.substring(line.indexOf('{')+1, line.length()-1);
				String[] splitRules = rulesPart.split(",");
				List<Rule> rules = new ArrayList<Day19.Rule>();
				for (String rule : splitRules) {
					if (rule.contains("=")) {
						Rule r = new EqualRule(rule.split("=")[0], Integer.parseInt(rule.split("=")[1].split(":")[0]), rule.split("=")[1].split(":")[1]);
						rules.add(r);
					}
					else if (rule.contains("<")) {
						Rule r = new LTRule(rule.split("<")[0], Integer.parseInt(rule.split("<")[1].split(":")[0]), rule.split("<")[1].split(":")[1]);
						rules.add(r);
					}
					else if (rule.contains(">")) {
						Rule r = new GTRule(rule.split(">")[0], Integer.parseInt(rule.split(">")[1].split(":")[0]), rule.split(">")[1].split(":")[1]);
						rules.add(r);
					}
					else if (rule.contains("A")) {
						rules.add(new AcceptTRule());
					}
					else if (rule.contains("R")) {
						rules.add(new RejectTRule());
					}
					else {
						rules.add(new GotoRule(rule));
					}
				}
				workflow.rules.addAll(rules);
			}
			else {
				Part p = new Part();
				line = line.substring(1, line.length()-1);
				String[] split = line.split(",");
				Part part = new Part();
				for (String string : split) {
					String[] split2 = string.split("=");
					part.attributes.put(split2[0], Integer.parseInt(split2[1]));
				}
				parts.add(part);
			}
		}
		
		int result = 0;
		for (Part part : parts) {
			if (accept(part, worflows)) {
				result += part.getSum();
			}
		}
		
		long startTime = System.nanoTime();

		System.out.println("Result part 1 : " + result + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		
		Solutions solutions = new Solutions();
		worflows.get("in").visit(solutions, worflows, new ArrayList<Rule>());
		
		for (Range r : solutions.ranges.values()) {
			System.out.println(r.values.size());
		}
		
		result = 0;
		startTime = System.nanoTime();
		System.out.println("Result part 2 : " + result + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
	public static class Range {
		
		public List<Integer> values = new ArrayList<Integer>();
		
		public Range() {
			for (int i=1;i<=4000;i++) {
				values.add(i);
			}
		}

//		public Range(Range range) {
//			this.start = range.start;
//			this.end = range.end;
//		}

		public void substractLT(int value) {
			values = values.stream().filter(v -> v >= value).collect(Collectors.toList());
			Thread.yield();
		}

		public void substractEQ(int value) {
			values = values.stream().filter(v -> v != value).collect(Collectors.toList());
			Thread.yield();
		}

		public void substractGT(int value) {
			values = values.stream().filter(v -> v <= value).collect(Collectors.toList());
			Thread.yield();
		}
		
	}
	
	public static class Solutions {
		
		private Map<String, Range> ranges = new HashMap<String, Range>();
		
		public Solutions() {
			ranges.put("x", new Range());
			ranges.put("m", new Range());
			ranges.put("a", new Range());
			ranges.put("s", new Range());
		}
		
		public void substractLT(String attributeName, int value) {
			ranges.get(attributeName).substractLT(value);
		}

		public void substractEQ(String attributeName, int value) {
			ranges.get(attributeName).substractEQ(value);
		}

		public void substractGT(String attributeName, int value) {
			ranges.get(attributeName).substractGT(value);
		}

		public void substract(List<Rule> rulesStack) {
			for (Rule rule : rulesStack) {
				rule.substract(this);
			}
		}

//		public Solutions(Solutions other) {
//			for (String attribute : ranges.keySet()) {
//				ranges.put(attribute, new Range(ranges.get(attribute)));
//			}
//		}
		
//		public void substract(Solutions s) {
//			for (String attribute : ranges.keySet()) {
//				ranges.put(attribute, ranges.get(attribute).substract(s.ranges.get(attribute)));
//			}
//		}
		
	}
}
