import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Day16 {
	
	public static class BeamDirection {
		public BeamDirection(int dx, int dy) {
			this.dx = dx;
			this.dy = dy;
		}
		int dx;
		int dy;
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + dx;
			result = prime * result + dy;
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
			BeamDirection other = (BeamDirection) obj;
			if (dx != other.dx)
				return false;
			if (dy != other.dy)
				return false;
			return true;
		}
		
		
	}

	public static class Energized {
		public Set<BeamDirection> directions = new HashSet<BeamDirection>();

		public void add(BeamDirection direction) {
			directions.add(direction);
		}

		public boolean contains(BeamDirection direction) {
			// TODO Auto-generated method stub
			return directions.contains(direction);
		}
	}

	static int width = 0;
	static int height = 0;

	private static class LightSource {
		
		int x;
		int y;
		int dx;
		int dy;
		
		public LightSource(int x, int y, int dx, int dy) {
			this.x = x;
			this.y = y;
			this.dx = dx;
			this.dy = dy;
		}

		public List<LightSource> getReflected(char c) {
			List<LightSource> result = new ArrayList<LightSource>();
			if (dx == 0 && dy != 0) {
				if (c == '-') {
					result.add(new LightSource(x,y,-1,0));
					result.add(new LightSource(x,y,1,0));
				}
				if (c == '/') {
					result.add(new LightSource(x,y,-dy,0));
				}
				if (c == '\\') {
					result.add(new LightSource(x,y,dy,0));
				}
				if (c == '|') {
					result.add(new LightSource(x,y,dx, dy));
				}
			}
			else if (dx != 0 && dy == 0) {
				if (c == '|') {
					result.add(new LightSource(x,y,0,-1));
					result.add(new LightSource(x,y,0,1));
				}
				if (c == '/') {
					result.add(new LightSource(x,y,0,-dx));
				}
				if (c == '\\') {
					result.add(new LightSource(x,y,0,dx));
				}
				if (c == '-') {
					result.add(new LightSource(x,y,dx, dy));
				}
			}
			else 
				throw new IllegalStateException();
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day16.txt"));

		// Part 1
		long startTime = System.nanoTime();
		width = lines.get(0).length();
		height = lines.size();
		
		char[][] map = createMap(lines);
		Energized[][] energizedMap = new Energized[width][height];
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				energizedMap[x][y] = new Energized();
			}
		}
		
		int result = 0;
		LightSource source = new LightSource(-1, 0, 1, 0);
		processLight(source, map, energizedMap);
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				if (!energizedMap[x][y].directions.isEmpty()) {
					result++;
				}
			}
		}
		
		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");
		
		// Part 2
		startTime = System.nanoTime();
		
		result = 0;
		for (int x=0;x<width;x++) {
			source = new LightSource(x, -1, 0, 1);
			result = Math.max(result, countEnergized(source, map, energizedMap));
			source = new LightSource(x, height, 0, -1);
			result = Math.max(result, countEnergized(source, map, energizedMap));
		}
		for (int y=0;y<height;y++) {
			source = new LightSource(-1, y, 1, 0);
			result = Math.max(result, countEnergized(source, map, energizedMap));
			source = new LightSource(width, y, -1, 0);
			result = Math.max(result, countEnergized(source, map, energizedMap));
		}
			
		System.out.println("Result part 2 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");
	}
	
	private static int countEnergized(LightSource source, char[][] map, Energized[][] energizedMap) {
		int result = 0;
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				energizedMap[x][y] = new Energized();
			}
		}
		processLight(source, map, energizedMap);
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {
				if (!energizedMap[x][y].directions.isEmpty()) {
					result++;
				}
			}
		}
		return result;
	}

	private static void processLight(LightSource source, char[][] map, Energized[][] energizedMap) {
		int x = source.x+source.dx;
		int y = source.y+source.dy;
		source.x = x;
		source.y = y;
		if (x >= width || x < 0 || y >= height || y < 0) return; 
		BeamDirection direction = new BeamDirection(source.dx, source.dy);
		if (!energizedMap[x][y].contains(direction)) {
			energizedMap[x][y].add(direction);
			char c = map[x][y];
			if (c == '.') {
				LightSource newSource = new LightSource(x, y, source.dx, source.dy);
				processLight(newSource, map, energizedMap);
			}
			else {
				List<LightSource> newSources = source.getReflected(c);
				for (LightSource lightSource : newSources) {
					processLight(lightSource, map, energizedMap);
				}
			}
		}
		Thread.yield();
	}

	private static char[][] createMap(List<String> lines) {
		char[][] map = new char[width][height];
		for (int y=0;y<lines.size();y++) {
			String line = lines.get(y);
			for (int x=0;x<line.length();x++) {
				map[x][y] = line.charAt(x);
			}
		}
		return map;
	}
}


