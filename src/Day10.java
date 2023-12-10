import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Day10 {
	
	public static class Graph {
		
	    private Set<Node> nodes = new HashSet<>();
	    
	    public Graph(List<Node> allNodes) {
	    	nodes.addAll(allNodes);
		}

		public void addNode(Node nodeA) {
	        nodes.add(nodeA);
	    }
	}
	
	public static class Node {

		public char c;
		private int x;
		private int y;
		
		public Integer distance = Integer.MAX_VALUE;
		public List<Node> shortestPath = new LinkedList<>();
		public Map<Node, Integer> adjacentNodes = new HashMap<>();
		
		public Node(char c, int x, int y) {
			this.c = c;
			this.x = x;
			this.y = y;
		}

		public void process(Node[][] graph, int width, int height) {
			switch (c) {
				case '|':
					putIfExist(getUp(graph));
					putIfExist(getDown(graph));
					break;
				case '-':
					putIfExist(getLeft(graph));
					putIfExist(getRight(graph));
					break;
				case 'J':
					putIfExist(getLeft(graph));
					putIfExist(getUp(graph));
					break;
				case 'L':
					putIfExist(getRight(graph));
					putIfExist(getUp(graph));
					break;
				case 'F':
					putIfExist(getDown(graph));
					putIfExist(getRight(graph));
					break;
				case '7':
					putIfExist(getLeft(graph));
					putIfExist(getDown(graph));
					break;
				case '.':
					break;
				case 'S':
					break;
				default:
					throw new IllegalStateException();
			}
		}
		
		private void putIfExist(Node node) { if (node != null) adjacentNodes.put(node, 1); }

		public Node getUp(Node[][] graph) { return y > 0 ? graph[x][y-1] : null; }
		public Node getDown(Node[][] graph) { return y < graph[0].length -1 ? graph[x][y+1] : null; }
		public Node getLeft(Node[][] graph) { return x > 0 ? graph[x-1][y] : null; }
		public Node getRight(Node[][] graph) { return x < graph.length - 1 ? graph[x+1][y] : null;  }
	}

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day10.txt"));

		int height = lines.size();
		int width = lines.get(0).length();
		
		Node[][] graphArray = new Node[width][height];
		List<Node> allNodes = new ArrayList<Node>();
		
		int y = 0;
		Node startNode = null;
		for (String line : lines) {
			for (int x = 0;x<line.length();x++) {
				allNodes.add(graphArray[x][y] = new Node(line.charAt(x), x, y));
				if (line.charAt(x) == 'S') startNode = graphArray[x][y];
			}
			y++;
		}
		
		allNodes.forEach(n -> n.process(graphArray, width, height));
		allNodes.forEach(n -> {
			for (Node other : new ArrayList<Node>(n.adjacentNodes.keySet())) {
				if (other.c == 'S') other.adjacentNodes.put(n, 1);
			}
		});
		
		Graph graph = new Graph(allNodes);
		graph = calculateShortestPathFromSource(graph, startNode);
		
		int distance = allNodes.stream().mapToInt(n -> n.distance).filter(d -> d!= Integer.MAX_VALUE).max().orElseThrow();
		System.out.println("Result part 1 : " + distance);

		// Fix S type
		if (startNode.adjacentNodes.containsKey(startNode.getUp(graphArray))) {
			if (startNode.adjacentNodes.containsKey(startNode.getDown(graphArray))) { startNode.c = '|'; }
			if (startNode.adjacentNodes.containsKey(startNode.getLeft(graphArray))) { startNode.c = 'J'; }
			if (startNode.adjacentNodes.containsKey(startNode.getRight(graphArray))) { startNode.c = 'L'; }
			
		}
		else if (startNode.adjacentNodes.containsKey(startNode.getDown(graphArray))) {
			if (startNode.adjacentNodes.containsKey(startNode.getLeft(graphArray))) { startNode.c = '7'; }
			if (startNode.adjacentNodes.containsKey(startNode.getRight(graphArray))) { startNode.c = 'F'; }
		}
		else {
			startNode.c = '-';
		}
		
		HashSet<Node> inside = new HashSet<Day10.Node>();
		for (y=0;y<height;y++) {
			HashSet<Node> lineInside = new HashSet<Day10.Node>();
			boolean isIn = false;
			char enterChar = ' ';
			for (int x=0;x<width;x++) {
				Node node = graphArray[x][y];
				if (node.distance != Integer.MAX_VALUE) {
					if (enterChar == ' ') enterChar = node.c;
					if (node.c == '|') { isIn = !isIn; enterChar = ' ';  }
					if (node.c == 'J' && enterChar == 'F') { isIn = !isIn; enterChar = ' '; }
					if (node.c == 'J' && enterChar == 'L') { enterChar = ' '; }
					if (node.c == '7' && enterChar == 'L') { isIn = !isIn; enterChar = ' '; }
					if (node.c == '7' && enterChar == 'F') { enterChar = ' '; }

					if (!lineInside.isEmpty()) {
						inside.addAll(lineInside);
						lineInside.clear();
					}
				}
				if (node.distance == Integer.MAX_VALUE && isIn) {
					lineInside.add(node);
					enterChar = ' ';
				}
			}
		}
		
		System.out.println("Result part 2 : " + inside.size());
	}
	
	// ========================================= Dijkstra ================================	

	public static Graph calculateShortestPathFromSource(Graph graph, Node source) {
	    
		source.distance = 0;
	
	    Set<Node> settledNodes = new HashSet<>();
	    Set<Node> unsettledNodes = new HashSet<>();
	
	    unsettledNodes.add(source);
	
	    while (unsettledNodes.size() != 0) {
	        Node currentNode = getLowestDistanceNode(unsettledNodes);
	        unsettledNodes.remove(currentNode);
	        for (Entry<Node, Integer> adjacencyPair : currentNode.adjacentNodes.entrySet()) {
	            Node adjacentNode = adjacencyPair.getKey();
	            Integer edgeWeight = adjacencyPair.getValue();
	            if (!settledNodes.contains(adjacentNode)) {
	                calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
	                unsettledNodes.add(adjacentNode);
	            }
	        }
	        settledNodes.add(currentNode);
	    }
	    return graph;
	}
	
	private static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
		return unsettledNodes.stream().min((n1, n2) -> {
			return Integer.compare(n1.distance, n2.distance);
		}).orElse(null);
	}
	
	public static void calculateMinimumDistance(Node evaluationNode,  Integer edgeWeigh, Node sourceNode) {
	    Integer sourceDistance = sourceNode.distance;
	    if (sourceDistance + edgeWeigh < evaluationNode.distance) {
	        evaluationNode.distance = sourceDistance + edgeWeigh;
	        LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.shortestPath);
	        shortestPath.add(sourceNode);
	        evaluationNode.shortestPath = shortestPath;
	    }
	}
}


