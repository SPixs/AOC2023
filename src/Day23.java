import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day23 {

	static int width = 0;
	static int height = 0;
	static char[][] map;
	static Point[][] points;

	static Point startPosition = null;
	static Point endPosition = null;

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day23.txt"));

		
		// Build map
		width = lines.get(0).length();
		height = lines.size();

		points = new Point[width][height];
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				points[x][y] = new Point(x, y);
			}			
		}

		int y = 0;
		map = new char[width][height];
		for (String line : lines) {
			for (int x=0;x<line.length();x++) {
				map[x][y] = line.charAt(x);
				if (y == 0 && map[x][y] == '.') { startPosition = points[x][y]; }
				if (y == height-1 && map[x][y] == '.') { endPosition = points[x][y]; }
			}
			y++;
		}

		
		List<List<Point>> allPath = new ArrayList<List<Point>>();
//		Set<Point> visited = new HashSet<Point>();
		List<Point> firstPath = new ArrayList<Point>();
		firstPath.add(startPosition);
		allPath.add(firstPath);
//		visited.add(startPosition);
		final Point end = endPosition;

//		Point location = startPosition;
		boolean stop = false;
		while (!stop) {
			stop = true;
			for (List<Point> path : new ArrayList<List<Point>>(allPath)) {
				Point head = path.get(path.size()-1);
				List<Point> next = head.createNext().stream().filter(p -> !path.contains(p)).collect(Collectors.toList());
				if (!next.isEmpty()) {
					stop = false;
					path.add(next.get(0));
				}
				else {
					if (!head.equals(end)) {
						allPath.remove(path);
					}
					Thread.yield();
				}
				for (int i=1;i<next.size();i++) {
					List<Point> otherPath = new ArrayList<Point>(path);
					otherPath.remove(otherPath.size()-1);
					otherPath.add(next.get(i));
					allPath.add(otherPath);
				}
			}
		}	
		
		
		// Part 1
		long startTime = System.nanoTime();
		int result = allPath.stream().filter(p -> p.get(p.size()-1).equals(end)).mapToInt(p -> p.size()-1).max().orElseThrow();

		System.out.println("Result part 1 : " + result + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		
		// Part 2
		startTime = System.nanoTime();

		AtomicInteger counter = new AtomicInteger();
		Set<Point> visited = new HashSet<Point>();
		iterate(visited, counter);
		result = counter.get()-1;
		
//		Set<XPath> allPaths = new HashSet<XPath>();
//		XPath path = new XPath(startPosition);
//		allPaths.add(path);
//		
//		stop = false;
//		while (!stop) {
//			stop = true;
//			for (XPath tmpPath : new HashSet<XPath>(allPaths)) {
////				System.out.println(tmpPath.visited.size());
//				Point head = tmpPath.head;
//				List<Point> next = head.createNextPart2().stream().filter(p -> !tmpPath.visited.contains(p)).collect(Collectors.toList());
//				if (!next.isEmpty()) {
//					stop = false;
//					tmpPath.add(next.get(0));
//				}
//				else {
//					if (!head.equals(end)) {
//						allPaths.remove(tmpPath);
//						Thread.yield();
//					}
//				}
//				for (int i=1;i<next.size();i++) {
//					XPath p = new XPath(tmpPath);
//					p.head = next.get(i);
//					allPaths.add(p);
//				}
//			}
//			System.out.println(allPaths.size());
//		}	
//		
//		result = allPaths.stream().filter(p -> p.head.equals(end)).mapToInt(p -> p.visited.size()).max().orElseThrow();
		
		System.out.println("Result part 2 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
	private static void recurse(Point location, Set<Point> visited, AtomicInteger counter) {
		if (location.equals(endPosition)) {
			counter.set(Math.max(counter.get(), visited.size()));
			return;
		}
		List<Point> next = location.createNextPart2().stream().filter(p -> !visited.contains(p)).collect(Collectors.toList());
		for (Point point : next) {
			visited.add(point);
			recurse(point, visited, counter);
			visited.remove(point);
		}
	}
	
	private static void iterate(final Set<Point> visited, AtomicInteger counter) {
//	    Stack<Point> stack = new Stack<>();
//	    Stack<Set<Point>> visitedStack = new Stack<Set<Point>>();
//	    stack.push(startPosition);
//	    visitedStack.push(new HashSet<Point>());
		
		final Point[] current = new Point[1];
		Stack<Action> stack = new Stack<Action>();
		stack.add(
				new Action(null) {
					void start() { 
						current[0] = startPosition;
						visited.add(startPosition); 
					}
				});

	    while (!stack.isEmpty()) {
		    
 		    Action r = stack.pop();
		    r.start();
	        
	        if (current[0].equals(endPosition)) {
	            if (visited.size() > counter.get()) {
		            System.out.println(counter);
	            }
	            counter.set(Math.max(counter.get(), visited.size()));
	            r.end();
	            continue;
	        }

	        List<Point> next = current[0].createNextPart2().stream().filter(p -> !visited.contains(p)).collect(Collectors.toList());
	        Collections.shuffle(next);
	        
	        if (next.isEmpty()) r.end();
	        
        	for (int i=0;i<next.size();i++) {
        		final Point p = next.get(i);
        		if (i==0) {
        			stack.add(new Action(r) {
        				void start() { current[0] = p; visited.add(p); }
        				void end() {
        					visited.remove(p);
        					previous.end(); 
        				}
        			});
        		}
        		else {
        			stack.add(new Action(r) {
        				void start() { current[0] = p; visited.add(p); }
        				void end() {
        					visited.remove(p);
        				}
        			});
        		}
        	}
	    }
	    
	    Thread.yield();
	}
	
	private static class Action {
		public Action previous;

		void start() {}
		void end() {}
		
		public Action(Action previous) {
			this.previous = previous;
		}
	}

	private static class XPath {
		
		public Set<Point> visited = new HashSet<Point>();
		public Point head = null;
		
		public XPath(Point head) {
			this.head = head;
		}
		
		public void add(Point point) {
			visited.add(head);
			head = point;
		}

		public XPath(XPath other) {
			this.visited = new HashSet<Point>(other.visited);
			this.head = other.head;
		}
	}
	
	private static class Point {
		
		public int x,y;

		public Point(int x, int y) {
			this.x =x ;
			this.y=y;
		}

		public List<Point> createNext() {
			
			if (map[x][y] == 'v' && isValid(getDown())) return Collections.singletonList(getDown());
			if (map[x][y] == '>' && isValid(getRight())) return Collections.singletonList(getRight());
			if (map[x][y] == '<' && isValid(getLeft())) return Collections.singletonList(getLeft());
			if (map[x][y] == '^' && isValid(getUp())) return Collections.singletonList(getUp());

			return Stream.of(getDown(), getLeft(),getUp(), getRight()).filter(p -> isValid(p)).collect(Collectors.toList());
		}
		
		public List<Point> createNextPart2() {
			
			return Stream.of(getDown(), getLeft(),getUp(), getRight()).filter(p -> isValid(p)).collect(Collectors.toList());
		}

		public Point getUp() { return y > 0 ? points[x][y-1] : null; }
		public Point getLeft() {return x > 0 ? points[x-1][y] : null; }
		public Point getRight() {return x < width-1 ? points[x+1][y] : null; }
		public Point getDown() {return y < height-1 ? points[x][y+1] : null; }

		@Override
		public String toString() {
			return "["+x+","+y+"]";
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			return result;
		}

		@Override
		public boolean equals(Object other) {
			return (x == ((Point)other).x) && (y == ((Point)other).y);
		}
	}

	public static boolean isValid(Point p) {
		return p != null && map[p.x][p.y] != '#';
	}
}
