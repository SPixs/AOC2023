import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Day13 {
	
	public static class Pattern {

		List<String> lines = new ArrayList<String>();
		List<String> columns = new ArrayList<String>();
		
		public void appendLine(String line) {
			lines.add(line);
		}

		public int getVerticalRFeflectionCount() {
			List<Integer> verticalRFeflectionCollectiont = getVerticalReflectionCollection();
			if (verticalRFeflectionCollectiont.isEmpty()) return 0;
			return verticalRFeflectionCollectiont.get(0);
		}
			
		public int getHorizontalRFeflectionCount() {
			List<Integer> horizontalRFeflectionCollection = getHorizontalRFeflectionCollection();
			if (horizontalRFeflectionCollection.isEmpty()) return 0;
			return horizontalRFeflectionCollection.get(0);
		}

		public List<Integer> getVerticalReflectionCollection() {
			List<Integer> result = new ArrayList<Integer>();
			for (int i=1;i<columns.size();i++) {
				int width = Math.min(i-0, columns.size()-i);
				boolean found = true;
				for (int x=0;x<width&found;x++) {
					found &= columns.get(i-x-1).equals(columns.get(i+x));
				}
				if (found) result.add(i);
			}
			return result;
		}

		public List<Integer> getHorizontalRFeflectionCollection() {
			List<Integer> result = new ArrayList<Integer>();
			for (int i=1;i<lines.size();i++) {
				int height = Math.min(i-0, lines.size()-i);
				boolean found = true;
				for (int y=0;y<height&found;y++) {
					found &= lines.get(i-y-1).equals(lines.get(i+y));
				}
				if (found) result.add(i);
			}
			return result;
		}

		public void computeColumns() {
			columns.clear();
			for (int i=0;i<lines.get(0).length();i++) {
				StringBuffer buffer = new StringBuffer();
				for (int r=0;r<lines.size();r++) {
					buffer.append(lines.get(r).charAt(i));
				}
				columns.add(buffer.toString());
			}
		}

		public void dump() {
			lines.forEach(l -> System.out.println(l));
		}

		public void swap(int x, int y) {
			String string = lines.get(y);
			char c = lines.get(y).charAt(x);
			StringBuffer b = new StringBuffer(string);
			b.setCharAt(x, (c == '#') ? '.' : '#');
			lines.set(y, b.toString());
			computeColumns();
		}
	}

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day13.txt"));

		List<Pattern> patterns = new ArrayList<Pattern>();
		Pattern pattern = new Pattern();
		for (String line : lines) {
			if (line.isBlank()) {
				pattern.computeColumns();
				patterns.add(pattern);
				pattern = new Pattern();
			}
			else {
				pattern.appendLine(line);
			}
		}
		pattern.computeColumns();
		patterns.add(pattern);
		
		// Part 1
		long startTime = System.nanoTime();
		int result = patterns.stream().mapToInt(p -> p.getVerticalRFeflectionCount()).sum() + 100 * patterns.stream().mapToInt(p -> p.getHorizontalRFeflectionCount()).sum();
		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");
		
		// Part 2
		startTime = System.nanoTime();
		int verticalReflectionCount = 0;
		int horizontalReflectionCount = 0;
		boolean found = false;
		for (Pattern p : patterns) {
			List<Integer> originalVerticalRFeflectionCount = p.getVerticalReflectionCollection();
			List<Integer> originalHorizontalRFeflectionCount = p.getHorizontalRFeflectionCollection();
			found = false; 
			for (int x=0;x<p.columns.size()&&!found;x++) {
				for (int y=0;y<p.lines.size()&&!found;y++) {
					p.swap(x,y);
					List<Integer> newVerticalRFeflectionCount = p.getVerticalReflectionCollection();
					List<Integer> newHorizontalRFeflectionCount = p.getHorizontalRFeflectionCollection();
					newVerticalRFeflectionCount.removeAll(originalVerticalRFeflectionCount);
					newHorizontalRFeflectionCount.removeAll(originalHorizontalRFeflectionCount);
					
					if (!newVerticalRFeflectionCount.isEmpty()) {
						if (newVerticalRFeflectionCount.size() > 1) throw new IllegalStateException();
						verticalReflectionCount += newVerticalRFeflectionCount.get(0);
					}
					if (!newHorizontalRFeflectionCount.isEmpty()) {
						if (newHorizontalRFeflectionCount.size() > 1) throw new IllegalStateException();
						horizontalReflectionCount += newHorizontalRFeflectionCount.get(0);
					}
					
					found |= !(newVerticalRFeflectionCount.isEmpty() && newHorizontalRFeflectionCount.isEmpty());
					p.swap(x,y);
				}
			}
		}
		System.out.println("Result part 2 : " + (verticalReflectionCount + 100 * horizontalReflectionCount) + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");
	}
}


