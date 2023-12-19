import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Day18 {

	static long width = 0;
	static long height = 0;

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day18.txt"));

		int x = 0;
		int y = 0;

		List<Point> points = new ArrayList<>();
		points.add(new Point(0, 0));

		List<Point> pointsPart2 = new ArrayList<>();
		pointsPart2.add(new Point(0, 0));

		
		int perimeter = 0;
		for (String line : lines) {
			String[] split = line.split(" ");
			char direction = split[0].charAt(0);
			int count = Integer.parseInt(split[1]);
			String color = split[2].substring(2, 8); // not used

			switch (direction) {
				case 'U': y -= count; break;
				case 'D': y += count; break;
				case 'R': x += count; break;
				case 'L': x -= count; break;
			}
	
			perimeter += count;
			points.add(new Point(x, y));
		 }
		 
		 
		// Part 1
		long startTime = System.nanoTime();
		
		// Use Pick's Theorem : Area = the number of integer coordinates inside the polygon + (number of integer coordinates on the boundary / 2) - 1
		// Number of internal points = Area (from Shoelace) + perimeter - (Nb points on perimeter / 2) + 1
		long result = calculatePolygonArea(points) + (perimeter / 2) + 1;
		
		System.out.println("Result part 1 : " + result + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");

		// Part 2
		x = 0;
		y = 0;
		points = new ArrayList<>();
		points.add(new Point(0, 0));
		BigInteger bigPerimeter = BigInteger.ZERO;
		int p = 0;
		for (String line : lines) {
			String[] split = line.split(" ");
			String hexa = split[2].substring(2, 8);
			char direction = new char[] { 'R', 'D', 'L', 'U' }[hexa.charAt(5)-'0'];
			int count = Integer.parseInt(hexa.substring(0, 5), 16);
			
			switch (direction) {
				case 'U': y -= count; break;
				case 'D': y += count; break;
				case 'R': x += count; break;
				case 'L': x -= count; break;
			}

			p += count;
			bigPerimeter = bigPerimeter.add(BigInteger.valueOf(count));
			points.add(new Point(x, y));
		 }
		
		startTime = System.nanoTime();
		// Use Pick's Theorem again
		BigInteger bigResult = calculatePolygonAreaBig(points); 
		bigPerimeter = bigPerimeter.divide(BigInteger.TWO);
		bigResult = bigResult.add(bigPerimeter).add(BigInteger.ONE);
		System.out.println("Result part 2 : " + bigResult + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
	/**
	 * Shoelace algorithm with integer coordinates
	 */
	static long calculatePolygonArea(List<Point> polygon) {
		int n = polygon.size();
		long area = 0;

		for (int i = 0; i < n; i++) {
			Point current = polygon.get(i);
			Point next = polygon.get((i + 1) % n);

			area += (current.x * next.y - next.x * current.y);
		}

		return Math.abs(area) / 2;
	}
	
	/**
	 * Shoelace algorithm with large integer coordinates
	 */
	static BigInteger calculatePolygonAreaBig(List<Point> polygon) {
		int n = polygon.size();
		BigInteger area = BigInteger.ZERO;

		for (int i = 0; i < n; i++) {
			Point current = polygon.get(i);
			Point next = polygon.get((i + 1) % n);

			area = area.add(BigInteger.valueOf(current.x).multiply(BigInteger.valueOf(next.y)).subtract(BigInteger.valueOf(next.x).multiply(BigInteger.valueOf(current.y))));
		}

		return area.abs().divide(BigInteger.valueOf(2));
	}

	private static class Point {

		private int x;
		private int y;

		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}
}
