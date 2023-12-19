import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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

		public void visit(Solutions candidates, Map<String, Workflow> worflows, BigInteger[] bigResult) {
			for (Rule rule : rules) {
				Solutions newCandidates = new Solutions(candidates);
				rule.retain(candidates);
				rule.visit(candidates, worflows, bigResult);
				rule.substract(newCandidates);
				candidates = newCandidates;
			}
		}
	}
	
	public static abstract class Rule {
	
		public String nextWorkflow;
		abstract boolean accept(Part p);

		protected void visit(Solutions candidates, Map<String, Workflow> worflows, BigInteger[] bigResult) {
			if (nextWorkflow.equals("R")) {
			}
			else if (nextWorkflow.equals("A")) {
				bigResult[0] = bigResult[0].add(candidates.getSize());
			}
			else {
				worflows.get(nextWorkflow).visit(candidates, worflows, bigResult);
			}
		}

		protected abstract void substract(Solutions solutions);

		protected abstract Solutions retain(Solutions solutions);
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

		@Override
		protected Solutions retain(Solutions solutions) {
			solutions.retainGT(attributeName, value);
			return solutions;
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
		
		@Override
		protected Solutions retain(Day19.Solutions solutions) {
			solutions.retainLT(attributeName, value);
			return solutions;
		}
	}

	public static class AcceptRule extends Rule {
		
		@Override
		boolean accept(Part p) {
			return true;
		}
		
		@Override
		public String toString() {
			return "A";
		}
		
		protected void visit(Solutions candidates, Map<String, Workflow> worflows, BigInteger[] bigResult) {
			bigResult[0] = bigResult[0].add(candidates.getSize());
		}

		@Override
		protected void substract(Solutions solutions) {
		}

		@Override
		protected Solutions retain(Solutions solutions) {
			return solutions;
		}
	}

	public static class RejectRule extends Rule {
	
		@Override
		boolean accept(Part p) {
			return true;
		}
		
		@Override
		public String toString() {
			return "R";
		}
		
		protected void visit(Solutions candidates, Map<String, Workflow> worflows, BigInteger[] bigResult) {
		}

		@Override
		protected void substract(Solutions solutions) {
		}

		@Override
		protected Solutions retain(Solutions solutions) {
			return solutions;
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
		
		@Override
		protected Solutions retain(Solutions solutions) {
			return solutions;
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
					return rule instanceof AcceptRule;
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
					if (rule.contains("<")) {
						Rule r = new LTRule(rule.split("<")[0], Integer.parseInt(rule.split("<")[1].split(":")[0]), rule.split("<")[1].split(":")[1]);
						rules.add(r);
					}
					else if (rule.contains(">")) {
						Rule r = new GTRule(rule.split(">")[0], Integer.parseInt(rule.split(">")[1].split(":")[0]), rule.split(">")[1].split(":")[1]);
						rules.add(r);
					}
					else if (rule.contains("A")) {
						rules.add(new AcceptRule());
					}
					else if (rule.contains("R")) {
						rules.add(new RejectRule());
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
		
		long startTime = System.nanoTime();
		int result = 0;
		for (Part part : parts) {
			if (accept(part, worflows)) {
				result += part.getSum();
			}
		}

		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMicros((System.nanoTime() - startTime)) / 1000.d + "ms");

		// Part 2
		startTime = System.nanoTime();
		BigInteger[] bigResult = new BigInteger[] { BigInteger.ZERO };
		Solutions candidates = new Solutions();
		worflows.get("in").visit(candidates, worflows, bigResult);
		
		System.out.println("Result part 2 : " + bigResult[0] + " in " + TimeUnit.NANOSECONDS.toMicros((System.nanoTime() - startTime))  / 1000.d + "ms");
	}
	
	public static final class Range {
		
		private int min;
		private int max;
		
		public Range() {
			min = 1;
			max = 4000;
		}

		public Range(int min, int max) {
			this.min = min;
			this.max = max;
		}

		public void substractLT(int value) {
			min = Math.max(min, value);
		}

		public void substractGT(int value) {
			max = Math.min(max, value);
		}

		public void retainGT(int value) {
			min = Math.max(min, value + 1);
		}

		public void retainLT(int value) {
			max = Math.min(max, value - 1);
		}

		public static Range empty() {
			Range range = new Range();
			range.min = 0;
			range.max = -1;
			return range;
		}
		
		@Override
		public String toString() {
			return "["+min+","+max+"]";
		}

		public Range copy() {
			return new Range(min, max);
		}

		public int size() {
			return Math.max(0, max-min+1);
		}
	}
	
	public static final class Solutions {
		
		private final Map<String, Range> ranges = new HashMap<String, Range>();

		public Solutions() {
			ranges.put("x", new Range());
			ranges.put("m", new Range());
			ranges.put("a", new Range());
			ranges.put("s", new Range());
		}
		
		public Solutions(Solutions solutions) {
			ranges.put("x", solutions.ranges.get("x").copy());
			ranges.put("m", solutions.ranges.get("m").copy());
			ranges.put("a", solutions.ranges.get("a").copy());
			ranges.put("s", solutions.ranges.get("s").copy());
		}
		
		public BigInteger getSize() {
			BigInteger result = BigInteger.ONE;
			for (Range r : ranges.values()) {
				result = result.multiply(BigInteger.valueOf(r.size()));
			}
			return result;
 		}

		public void retainGT(String attributeName, int value) {
			ranges.get(attributeName).retainGT(value);
		}


		public void retainLT(String attributeName, int value) {
			ranges.get(attributeName).retainLT(value);
		}


		public void substractLT(String attributeName, int value) {
			ranges.get(attributeName).substractLT(value);
		}

		public void substractGT(String attributeName, int value) {
			ranges.get(attributeName).substractGT(value);
		}
		
		@Override
		public String toString() {
			return "x="+ranges.get("x")+","+"m="+ranges.get("m")+","+"a="+ranges.get("a")+","+"s="+ranges.get("s");
		}
	}
}
