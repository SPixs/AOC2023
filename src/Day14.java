import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Day14 {
	
	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day14.txt"));
		int width = lines.get(0).length();
		int height = lines.size();
		
		// Part 1
		long startTime = System.nanoTime();
		char[][] map = createMap(lines);
		tiltNorth(width, height, map);
		long result = computeLoad(width, height, map);
		
		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");
		
		
		// Part 2
		startTime = System.nanoTime();
		map = createMap(lines);
		
		Map<String, Long> index = new HashMap<String, Long>();
		Map<String, Long> load = new HashMap<String, Long>();

		for (long i=0;i<1000000000;i++) {
			cycle(width, height, map);
			String str = toString(map);
			if (index.containsKey(str)) {
				long delta = i - index.get(str);
				i += delta * ((1000000000-i) / delta);
				index.put(str, i);
			}
			else {
				index.put(str, i);
			}
		}
		result = computeLoad(width, height, map); 
		
		System.out.println("Result part 2 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");
	}

	private static long computeLoad(int width, int height, char[][] map) {
		long result = 0;
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				if (map[x][y] == 'O') {
					result += height-y;
				}
			}
		}
		return result;
	}
	
	public static String toString(char[][] map) {
		StringBuffer b = new StringBuffer();
		for (char[] line : map) {
			b.append(new String(line));
		}
		return b.toString();
	}

	private static void cycle(int width, int height, char[][] map) {
		tiltNorth(width, height, map);
		tiltWest(width, height, map);
		tiltSouth(width, height, map);
		tiltEast(width, height, map);
	}

	private static char[][] createMap(List<String> lines) {
		int width = lines.get(0).length();
		int height = lines.size();
		char[][] map = new char[width][height];
		for (int y=0;y<lines.size();y++) {
			String line = lines.get(y);
			for (int x=0;x<line.length();x++) {
				map[x][y] = line.charAt(x);
			}
		}
		return map;
	}

	private static void tiltNorth(int width, int height, char[][] map) {
		for (int x=0;x<width;x++) {
			boolean move = true;
			while (move) {
				move = false;
				for (int y=1;y<height;y++) {
					if (map[x][y] == 'O' && map[x][y-1] == '.') {
						map[x][y] = '.';
						map[x][y-1] = 'O';
						move = true;
					}
				}
			}
		}
	}
	
	private static void tiltSouth(int width, int height, char[][] map) {
		for (int x=0;x<width;x++) {
			boolean move = true;
			while (move) {
				move = false;
				for (int y=height-2;y>=0;y--) {
					if (map[x][y] == 'O' && map[x][y+1] == '.') {
						map[x][y] = '.';
						map[x][y+1] = 'O';
						move = true;
					}
				}
			}
		}
	}

	private static void tiltWest(int width, int height, char[][] map) {
		for (int y=0;y<height;y++) {
			boolean move = true;
			while (move) {
				move = false;
				for (int x=1;x<width;x++) {
					if (map[x][y] == 'O' && map[x-1][y] == '.') {
						map[x][y] = '.';
						map[x-1][y] = 'O';
						move = true;
					}
				}
			}
		}
	}

	private static void tiltEast(int width, int height, char[][] map) {
		for (int y=0;y<height;y++) {
			boolean move = true;
			while (move) {
				move = false;
				for (int x=width-2;x>=0;x--) {
					if (map[x][y] == 'O' && map[x+1][y] == '.') {
						map[x][y] = '.';
						map[x+1][y] = 'O';
						move = true;
					}
				}
			}
		}
	}

	private static void dump(char[][] map) {
		for (int y=0;y<map[0].length;y++) {
			for (int x=0;x<map.length;x++) {
				System.out.print(map[x][y]);
			}
			System.out.println();
		}
	}
}


