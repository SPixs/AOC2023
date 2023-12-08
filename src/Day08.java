import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Day08 {
	
	private static class Node {
		
		static Map<String, Node> nameToNode = new HashMap<String, Node>();

		private String name;
		private Node left, right;

		public Node(String nodeName) { this.name = nodeName; }
		
		private final static Node getNode(String name) {
			return nameToNode.computeIfAbsent(name, key -> new Node(name));
		}
	}

	public static int getCount(String instructions, Node node, boolean part2) {
		int count = 0;
		boolean found = false;
		
		while (!found) {
			for (char direction : instructions.toCharArray()) {
				if (part2 ? node.name.endsWith("Z") : node.name.equals("ZZZ")) { found = true; break; }
				node = (direction == 'R') ? node.right : node.left;
				count++;
			}
		}
		return count;
	}
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day08.txt"));

		String instructions = lines.get(0);
		
		for (String line : lines.subList(2, lines.size())) {
			Node node = Node.getNode(line.split("=")[0].trim());
			node.left = Node.getNode(line.split("=")[1].split(",")[0].trim().substring(1));
			node.right = Node.getNode(line.split("=")[1].split(",")[1].split("\\)")[0].trim());
		}
		
		// Part 1
		long startTime = System.nanoTime();
		int count = getCount(instructions, Node.getNode("AAA"), false);
		System.out.println("Result part 1 : " + count + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");

		// Part 2
		// All concurrent paths are periodic. Use LCM (PPCM in French) to find the minimal number of steps.
		startTime = System.nanoTime();
		long countPart2 = computePPCM(Node.nameToNode.keySet().stream().filter(n -> n.endsWith("A")).map(Node::getNode).mapToLong(n -> getCount(instructions, n, true)).toArray());
		System.out.println("Result part 2 : " + countPart2 + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");
	}

    private static long computePPCM(long[] numbers) {
        return Arrays.stream(numbers)
                .reduce(1L, (a, b) -> (a * b) / computePGCD(a, b));
    }

    // Recursive method to calculate the Greatest Common Divisor (GCD)
    private static long computePGCD(long a, long b) {
        return b == 0 ? a : computePGCD(b, a % b);
    }
}
