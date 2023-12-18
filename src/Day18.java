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

		int perimeter = 0;
		for (String line : lines) {
			String[] split = line.split(" ");
			char direction = split[0].charAt(0);
			int count = Integer.parseInt(split[1]);
			String color = split[2].substring(2, 8);

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
			String color = hexa;
			char direction = new char[] { 'R', 'D', 'L', 'U' }[color.charAt(5)-'0'];
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
		BigInteger bigResult = calculatePolygonAreaBig(points); 
		bigPerimeter = bigPerimeter.divide(BigInteger.TWO);
		bigResult = bigResult.add(bigPerimeter).add(BigInteger.ONE);
		System.out.println("Result part 2 : " + bigResult + " in "
				+ TimeUnit.NANOSECONDS.toMillis((System.nanoTime() - startTime)) + "ms");
	}
	
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

		public void setY(int y) {
			this.y = y;
		}

		public void setX(int x) {
			this.x = x;
		}

		int getX() {
			return x;
		}

		int getY() {
			return y;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (x ^ (x >>> 32));
			result = prime * result + (int) (y ^ (y >>> 32));
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
			Point other = (Point) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			return true;
		}
	}
}
