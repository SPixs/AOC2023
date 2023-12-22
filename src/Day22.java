import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Day22 {
	
	public static class Grid {

		private Set<Point> filledBlocks = new HashSet<Point>();
		
		public static Grid from(List<Brick> bricks) {
			Grid grid = new Grid();
			for (Brick brick : bricks) {
				for (Point p : brick.getBlocks()) {
//					if (grid.filledBlocks.contains(p)) {
//						System.out.println(p);
//						throw new IllegalStateException();
//					}
				}
				grid.filledBlocks.addAll(brick.getBlocks());
			}
			return grid;
		}

		public void remove(List<Point> blocks) {
			for (Point b : blocks) {
//				if (!filledBlocks.contains(b)) {
//					throw new IllegalStateException();
//				}
			}
			filledBlocks.removeAll(blocks);
		}

		public void add(List<Point> blocks) {
			for (Point b : blocks) {
				if (filledBlocks.contains(b)) {
					throw new IllegalStateException();
				}
			}
			filledBlocks.addAll(blocks);
		}
		
		public Brick getFallCandidate(List<Brick> bricks) {
			for (Brick brick : bricks) {
				if (brick.canFall(this)) { return brick; }
			}
			return null;
		}
		
		public List<Brick> getAllFallCandidate(List<Brick> bricks) {
			return bricks.stream().filter(b -> b.canFall(this)).collect(Collectors.toList());
		}
		
		public void doFall(Brick brick) {
			brick.doFall(this);
		}
		
		public void check(List<Brick> bricks) {
			for (Brick brick : bricks) {
				for (Point p : brick.getBlocks()) {
					if (!filledBlocks.contains(p))
						throw new IllegalStateException();
				}
			}
		}
		
		public static Map<Point, Brick> getBrickMap(Collection<Brick> bricks) {
			Map<Point, Brick> map = new HashMap<Point, Brick>();
			for (Brick b : bricks) {
				List<Day22.Point> blocks = b.getBlocks();
				for (Point p : blocks) {
					map.put(p, b);
				}
			}
			return map;
		}
	}

	private static class Brick {
		
		public Set<Brick> savedLowerBricks = new HashSet<Brick>();
		public Set<Brick> savedUpperBricks = new HashSet<Brick>();
		public Set<Brick> lowerBricks = new HashSet<Brick>();
		public Set<Brick> upperBricks = new HashSet<Brick>();
		
		public Brick(Point start, Point end) {
			this.start = start;
			this.end = end;
		}

		public Point start, end;
		private String name;

		public void buildDependencies(Map<Point, Brick> map) {
			for (Point p : getBlocks()) {
				Point key = new Point(p.x, p.y, p.z-1);
				if (map.containsKey(key) && !(map.get(key) == this)) lowerBricks.add(map.get(key));
				key = new Point(p.x, p.y, p.z+1);
				if (map.containsKey(key) && !(map.get(key) == this)) upperBricks.add(map.get(key));
			}
			savedLowerBricks.addAll(lowerBricks);
			savedUpperBricks.addAll(upperBricks);
		}
		
		public void resetDependencies() {
			lowerBricks.addAll(savedLowerBricks);
			upperBricks.addAll(savedUpperBricks);
		}
		
		public List<Point> getBlocks() {
			List<Point> blocks = new ArrayList<Point>();

			int dx = end.x-start.x;
			int dy = end.y-start.y;
			int dz = end.z-start.z;
			
			blocks.add(new Point(start.x,start.y,start.z));
			
			if (dx != 0) {
				for (int x=start.x ; x<=end.x ; x+=dx/Math.abs(dx==0?1:dx)) { blocks.add(new Point(x,start.y,start.z)); }
			}
			if (dy != 0) {
				for (int y=start.y ; y<=end.y ; y+=dy/Math.abs(dy==0?1:dy)) { blocks.add(new Point(start.x,y,start.z)); }
			}
			if (dz != 0) {
				for (int z=start.z ; z<=end.z ; z+=dz/Math.abs(dz==0?1:dz)) { blocks.add(new Point(start.x,start.y,z)); }
			}

			return blocks;
		}

		@Override
		public String toString() {
			return name+"{"+start+","+end+"}";
		}
		
		public boolean canFall(Grid grid) {
			if (isOnGround()) return false;
			List<Point> blocks = getBlocks();
			grid.remove(blocks);
			for (Point p : blocks) {
				if (grid.filledBlocks.contains(new Point(p.x, p.y, p.z - 1))) {
					grid.add(blocks);
					return false;
				}
			}
			grid.add(blocks);
			return true;
		}

		public void doFall(Grid grid) {
			List<Point> blocks = getBlocks();
			grid.remove(blocks);
			start.z--;
			end.z--;
			grid.add(getBlocks());
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void recurseRemove(Set<Brick> removed, Set<Brick> processed) {
			for (Brick parent : upperBricks) {
				parent.recurseRemove(this, removed, processed);
			}
		}

		private void recurseRemove(Brick brick, Set<Brick> removed, Set<Brick> processed) {
			lowerBricks.remove(brick);
			processed.add(this);
			if (lowerBricks.isEmpty()) {
				removed.add(this);
				recurseRemove(removed, processed);
			}
		}

		public boolean isOnGround() {
			int minZ = Math.min(start.z, end.z);
			return (minZ == 1);
		}

		public void recurseRestore() {
			for (Brick parent : upperBricks) {
				if (parent.lowerBricks.isEmpty()) {
					parent.lowerBricks.add(this);
					parent.recurseRestore();
				}
				else {
					parent.lowerBricks.add(this);
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day22.txt"));
		
		List<Brick> bricks = lines.stream().map(l -> {
			String[] parts = l.split("\\~");
			String[] startCoordinates = parts[0].split(",");
			String[] endCoordinates = parts[1].split(",");
			Point start = new Point(Integer.parseInt(startCoordinates[0]), Integer.parseInt(startCoordinates[1]), Integer.parseInt(startCoordinates[2]));
			Point end = new Point(Integer.parseInt(endCoordinates[0]), Integer.parseInt(endCoordinates[1]), Integer.parseInt(endCoordinates[2]));
			return new Brick(start, end);
		}).collect(Collectors.toList());
		
		int index = 0;
		for (Brick brick : bricks) {
			brick.setName(String.valueOf(index++));
		}

		Grid grid = Grid.from(bricks);
		
		// Part 1
		long startTime = System.nanoTime();
		
		// Compute initial fall so that all bricks have settled
		List<Brick> candidates = grid.getAllFallCandidate(bricks);
		while (!candidates.isEmpty()) {
			candidates.forEach(c -> grid.doFall(c));
			candidates = grid.getAllFallCandidate(bricks);
		}
		System.out.println("Part 1a in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
		
		// Build dependencies between grid
		Map<Point,Brick> brickMap = Grid.getBrickMap(bricks);
		for (Brick brick : bricks) { brick.buildDependencies(brickMap); }

		int result = 0;

		for (Brick brick : new ArrayList<Brick>(bricks)) {
			
			Set<Brick> upper = brick.upperBricks;
			upper.forEach(parent -> parent.lowerBricks.remove(brick)); 
			if (upper.stream().allMatch(p -> !p.lowerBricks.isEmpty())) {
				result++;
			}
			upper.forEach(parent -> parent.resetDependencies());
		}
		
		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		result = 0;
		
		Set<Brick> removed = new HashSet<Brick>();
		Set<Brick> processed = new HashSet<Brick>();
		for (Brick b : bricks) {
			removed.clear();
			processed.clear();
			b.recurseRemove(removed, processed);
			b.recurseRestore();
//			for (Brick brick : processed) { brick.resetDependencies(); }
			result += removed.size();
		}
		
		System.out.println("Result part 2 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}

	private static class Point {

		public int x, y, z;
		
		public Point(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
			return result;
		}
		
		@Override
		public String toString() {
			return "["+x+","+y+","+z+"]";
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Point other = (Point) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			if (z != other.z)
				return false;
			return true;
		}
	}
}
