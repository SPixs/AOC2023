import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Day11 {
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day11.txt"));

		// BUild map
		int width = lines.get(0).length();
		int height = lines.size();

		int y = 0;
		char[][] map = new char[width][height];
		for (String line : lines) {
			for (int x=0;x<line.length();x++) {
				map[x][y] = line.charAt(x);
			}
			y++;
		}
		
		// Part 1
		long result = computeSumOfDistances(width, height, map, 1);
		System.out.println("Result part 1 : " + result);

		// Part 2
		result = computeSumOfDistances(width, height, map, 999999);
		System.out.println("Result part 2 : " + result);
	}

	private static long computeSumOfDistances(int width, int height, char[][] map, int increment) {

		long dx = 0;
		long dy = 0;

		long[] colDx = new long[width];
		long[] rowDy = new long[height];
		
		for (int x = 0;x<width;x++) {
			boolean empty = true;
			for (int y=0;y<height;y++) { if (map[x][y] != '.') empty = false; }
			if (empty) dx+= increment;
			colDx[x] = dx;
		}

		for (int y = 0;y<height;y++) {
			boolean empty = true;
			for (int x=0;x<width;x++) { if (map[x][y] != '.') empty = false; }
			if (empty) dy+= increment;
			rowDy[y] = dy;
		}
		
		List<Galaxy> galaxies = new ArrayList<Galaxy>();
		for (int y=0;y<height;y++) {
			for (int x=0;x<width;x++) {
				if (map[x][y] != '.') {
					galaxies.add(new Galaxy(x+colDx[x], y+rowDy[y]));
				}
			}			
		}
		
		return IntStream.range(0, galaxies.size())
				.mapToLong(src -> IntStream.range(src, galaxies.size())
						.mapToLong(dest -> galaxies.get(src).getDistance(galaxies.get(dest)))
						.sum())
				.sum();
	}

	public static class Galaxy {

		private long x;
		private long y;

		public Galaxy(long x, long y) {
			this.x = x;
			this.y = y;
		}

		public long getDistance(Galaxy other) {
			return Math.abs(other.x - x) + Math.abs(other.y - y);
		}
		
	}
}


