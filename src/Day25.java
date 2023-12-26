import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Day25 {

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day25.txt"));

		// Part 1
		long startTime = System.nanoTime();
		Graph g = createGraph(lines);
		
		int result = 0;
		int maxCut = Integer.MAX_VALUE;
		while (maxCut > 3) {
			g = createGraph(lines);
			int cuts = g.kargerMinCut();
			if (cuts < maxCut) {
				result = g.allNodes.get(0).mergeCount * g.allNodes.get(1).mergeCount;
				maxCut = cuts;
			}
		}
		
		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

	}

	private static Graph createGraph(List<String> lines) {
		Graph g = new Graph();
		for (String line : lines) {
			String[] split = line.split(":");
			Node fromNode = g.getNode(split[0].trim());
			String[] connected = split[1].trim().split(" ");
			for (String connectedName : connected) {
				fromNode.addNode(g.getNode(connectedName.trim()));
			}
		}
		return g;
	}
	
	private static class Graph {
		
		public List<Node> allNodes = new ArrayList<Node>();
		public Map<String, Node> nodeMap = new HashMap<String, Node>();
		
		public Node getNode(String name) {
			Node node = nodeMap.get(name);
			if (node == null) {
				node = nodeMap.computeIfAbsent(name, (n) -> new Node(n, nodeMap.size()));
				allNodes.add(node);
			}
			return node;
		}
		
		public int kargerMinCut() {
	        while (allNodes.size() > 2) {
	        	 // Select random node
	             int randomIndex = new Random().nextInt(allNodes.size());
	             Node randomNode = allNodes.get(randomIndex);
	        	
	             // Select random link
	             randomIndex = new Random().nextInt(randomNode.neighbours.size());
	             Node otherNode = randomNode.neighbours.get(randomIndex);
	             
	             mergeNodes(randomNode, otherNode);
	             
	             // removeSelfLoops
	             randomNode.neighbours.removeIf(n -> n == randomNode);
	        }

	        return allNodes.get(0).neighbours.size();
	    }

		private void mergeNodes(Node node1, Node node2) {
			
			List<Node> neighbours1 = node1.neighbours;
			List<Node> neighbours2 = node2.neighbours;
			
			neighbours1.addAll(neighbours2);
			for (Node n : neighbours2) {
				n.neighbours.remove(node2);
				n.neighbours.add(node1);
			}

			node1.mergeCount += node2.mergeCount;
			
			nodeMap.remove(node2.name);
			allNodes.remove(node2);
		}
	}

	public static class Node {

		private String name;
		private List<Node> neighbours = new ArrayList<Node>();
		public int index = 0;
		public int mergeCount = 1;

		public Node(String name, int index) {
			this.name = name;
			this.index = index;
		}

		public void addNode(Node other) {
			if (!neighbours.contains(other)) {
				neighbours.add(other);
				other.addNode(this);
			}
		}

		@Override
		public String toString() {
			return name;
		}
	}
}
