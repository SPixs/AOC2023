import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day21 {

	static int width = 0;
	static int height = 0;
	static char[][] map;
	
	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day21.txt"));

		Point startPosition = null;
		
		// Build map
		width = lines.get(0).length();
		height = lines.size();
		
		int y = 0;
		map = new char[width][height];
		for (String line : lines) {
			for (int x=0;x<line.length();x++) {
				map[x][y] = line.charAt(x);
				if (map[x][y] == 'S') {
					startPosition = new Point(x,y);
					map[x][y] = '.';
				}
			}
			y++;
		}
		
		Set<Point> toExplore = new HashSet<Point>(Collections.singleton(startPosition));

		// Part 1
		long startTime = System.nanoTime();

		for (int i=0;i<64;i++) {
			toExplore = toExplore.stream().flatMap(p -> Stream.of(p.getDown(), p.getLeft(), p.getUp(), p.getRight())).filter(p -> isValid(map, p)).collect(Collectors.toSet());
        }
		long result = toExplore.size();
		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		startTime = System.nanoTime();
		
		long count = 26501365;
		long cycles = count / width;
		long reminder = count % width;
		
		toExplore = new HashSet<Point>(Collections.singleton(startPosition));

		final List<Point> regressionPoints = new ArrayList<Point>();
		
		int steps = 0;
		for (int i=0;i<3;i++) {
			while (steps < i*width+reminder) {
				toExplore = toExplore.parallelStream().flatMap(p -> Stream.of(p.getDown(), p.getLeft(), p.getUp(), p.getRight())).filter(p -> isValid2(map, p)).collect(Collectors.toSet());
				steps++;
			}
			regressionPoints.add(new Point((i), toExplore.size()));
		}
		
        // Function for the quadratic curve
        Function<Long, Long> g = new Function<Long, Long>() {
			public Long apply(Long x) {
				double x1 = regressionPoints.get(0).x;
				double y1 = regressionPoints.get(0).y;
				double x2 = regressionPoints.get(1).x;
				double y2 = regressionPoints.get(1).y;
				double x3 = regressionPoints.get(2).x;
				double y3 = regressionPoints.get(2).y;
				return (long) (((x-x2) * (x-x3)) / ((x1-x2) * (x1-x3)) * y1 +
					    ((x-x1) * (x-x3)) / ((x2-x1) * (x2-x3)) * y2 +
					    ((x-x1) * (x-x2)) / ((x3-x1) * (x3-x2)) * y3);
			}
        };

        result = g.apply(cycles);
        
		System.out.println("Result part 2 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
	private static boolean isValid2(char[][] map, Point p) {
		int x = ((p.x % width) + width) % width;
		int y = ((p.y % height) + height) % height;
		if (map[x][y] == '#') return false;
		return true;
	}

	private static boolean isValid(char[][] map, Point p) {
		if (p.x < 0 || p.x >= width) return false;
		if (p.y < 0 || p.y >= height) return false;
		if (map[p.x][p.y] == '#') return false;
		return true;
	}

	private static class Point {
		
		public int x,y;

		public Point(int x, int y) {
			this.x =x ;
			this.y=y;
		}

		public Point getUp() { return new Point(x,y-1); }
		public Point getLeft() {return new Point(x-1,y); }
		public Point getRight() {return new Point(x+1,y); }
		public Point getDown() {return new Point(x,y+1); }

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
 }
