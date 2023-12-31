import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Day17 {

	static int width = 0;
	static int height = 0;
	static int[][] grid;
	
	private enum Direction {
		UP(0,-1), RIGHT(1,0), DOWN(0,1), LEFT(-1,0);

		private int dx;
		private int dy;

		Direction(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}

		Node getNext(Node node) {
			return new Node(node.x+dx, node.y+dy,this, node.steps+1);
		}

		Direction getRight() {
			return Direction.values()[(ordinal()+1)%Direction.values().length];
		}

		Direction getLeft() {
			return Direction.values()[(Direction.values().length+(ordinal()-1))%Direction.values().length];
		}
	}
	
	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day17.txt"));

		width = lines.get(0).length();
		height = lines.size();
		
		grid = new int[width][height];
		int y=0;
		for(String line : lines) {
			for (int x=0;x<width;x++) {
				grid[x][y]=line.charAt(x) - '0';
			}
			y++;
		}
		
		Node start = new Node(0, 0, null, 0);

		// Part 1
		long startTime = System.nanoTime();
		int result = Integer.MAX_VALUE;
		Set<Node> allPathFromSource = calculateShortestPathFromSource(start, false);
		for (Node node : allPathFromSource) {
			if (node.x == width-1 && node.y == height-1) {
				result = Math.min(result, node.distance);
			}
		}
		
		System.out.println("Result part 1 : " + result + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		result = Integer.MAX_VALUE;
		allPathFromSource = calculateShortestPathFromSource(start, true);
		for (Node node : allPathFromSource) {
			if (node.x == width-1 && node.y == height-1 && node.steps > 3) {
				result = Math.min(result, node.distance);
			}
		}
		System.out.println("Result part 2 : " + result + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	static class Node {
		int x, y; 
		public int heatLoss;
		public int steps;
		public Direction direction;

		
		public Integer distance = Integer.MAX_VALUE;
//		public List<Node> shortestPath = new LinkedList<>();
		
		public Node(int x, int y, Direction direction, int step) {
			this.x = x;
			this.y = y;
			this.direction = direction;
			this.steps = step;
			heatLoss = isValid(x, y) ? grid[x][y] : 100000;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((direction == null) ? 0 : direction.hashCode());
			result = prime * result + steps;
			result = prime * result + x;
			result = prime * result + y;
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
			Node other = (Node) obj;
			if (direction != other.direction)
				return false;
			if (steps != other.steps)
				return false;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}

		public List<Node> computeAdjacent(boolean part2) {
			List<Node> neighbors = new ArrayList<>();
			
			if (direction == null) {
				 for (Direction direction : Direction.values()) {
					 neighbors.add(direction.getNext(this));
				 }
			}
			else {
				 if (steps < (part2 ? 10 : 3)) 
					 neighbors.add(direction.getNext(this));
				if (!part2 || steps > 3) { 
					Node rightNode = direction.getRight().getNext(this);
					rightNode.steps = 1;
					neighbors.add(rightNode);
	
					Node leftNode = direction.getLeft().getNext(this);
					leftNode.steps = 1;
					neighbors.add(leftNode);
				}
			}
			
			neighbors = neighbors.stream().filter(n -> isValid(n.x, n.y)).collect(Collectors.toList());
			return neighbors;
		}
		
		@Override
		public String toString() {
			return "["+x+","+y+"]";
		}
	}

	static boolean isValid(int x, int y) {
		return x >= 0 && x < width && y >= 0 && y < height;
	}
	
	// ========================================= Dijkstra ================================	

	public final static Set<Node> calculateShortestPathFromSource(Node source, boolean part2) {
	    
		source.distance = 0;
		
	    Set<Node> settledNodes = new HashSet<>();
	    Set<Node> unsettledNodes = new HashSet<>();
	    
	    unsettledNodes.add(source);
	    
	    while (unsettledNodes.size() != 0) {
	    	
	        Node currentNode = getLowestDistanceNode(unsettledNodes);
	        
	        unsettledNodes.remove(currentNode);
	        for (Node adjacentNode : currentNode.computeAdjacent(part2)) {
	        	
	        	boolean findLower = false;
	        	if (!part2) {
	        		for (int i=1;i<=adjacentNode.steps && !findLower;i++) {
	        			findLower |= settledNodes.contains(new Node(adjacentNode.x, adjacentNode.y, adjacentNode.direction, i));
	        		}
	        	}
	        	else {
	        		for (int i=Math.min(4, adjacentNode.steps);i<=adjacentNode.steps && !findLower;i++) {
	        			findLower |= settledNodes.contains(new Node(adjacentNode.x, adjacentNode.y, adjacentNode.direction, i));
	        		}
	        	}
	        	
	            if (!findLower) {
		            Integer edgeWeight = adjacentNode.heatLoss;
	                calculateMinimumDistance(adjacentNode, edgeWeight, currentNode);
                	unsettledNodes.add(adjacentNode);
	            }
	        }
	        settledNodes.add(currentNode);
	    }
	    
	    return settledNodes;
	}
	
	private final static Node getLowestDistanceNode(Set<Node> unsettledNodes) {
		Node lowestDistanceNode = null;
	    int lowestDistance = Integer.MAX_VALUE;
	    for (Node node: unsettledNodes) {
	        int nodeDistance = node.distance;
	        if (nodeDistance < lowestDistance) {
	            lowestDistance = nodeDistance;
	            lowestDistanceNode = node;
	        }
	    }
	    return lowestDistanceNode;
	}
	
	public final static void calculateMinimumDistance(Node evaluationNode,  Integer edgeWeight, Node sourceNode) {
	    Integer sourceDistance = sourceNode.distance;
	    if (sourceDistance + edgeWeight < evaluationNode.distance) {
	        evaluationNode.distance = sourceDistance + edgeWeight;
//	        LinkedList<Node> shortestPath = new LinkedList<>(sourceNode.shortestPath);
//	        shortestPath.add(sourceNode);
//	        evaluationNode.shortestPath = shortestPath;
	    }
	}
}
