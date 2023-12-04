import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day03 {
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day03.txt"));

		// First, map all location to a number, if present
		int lineIndex = 0;
		Map<Point, Number> numberMap = new HashMap<Point, Number>();
		for (String line : lines) {
			parseNumbers(lineIndex++, line, numberMap);
		}

		// Then, find all numbers adjacent to a symbol
		lineIndex = 0;
		Set<Number> partNumbers = new HashSet<Number>();
		for (String line : lines) {
			for (int i=0;i<line.length();i++) {
				char charAt = line.charAt(i);
				if (!Character.isDigit(charAt) && charAt != '.') {
					partNumbers.addAll(findPartNumbers(i, lineIndex, numberMap));
				}
			}
			lineIndex++;
		}
		
		// Result part 1
		int result = partNumbers.stream().mapToInt(n -> n.value).sum();
		System.out.println("Result part 1 : " + result);


		// Part 2. Find all pairs of numbers adjacent to a '*' symbol
		lineIndex = 0;
		result = 0;
		for (String line : lines) {
			for (int i=0;i<line.length();i++) {
				char charAt = line.charAt(i);
				if (charAt == '*') {
					Set<Number> gearPartNumbers = findPartNumbers(i, lineIndex, numberMap);
					if (gearPartNumbers.size() == 2) {
						result += gearPartNumbers.stream().mapToInt(n -> n.value).reduce(1, (a, b) -> a * b);
					}
				}
			}
			lineIndex++;
		}

		// Result part 2
		System.out.println("Result part 2 : " + result);
	}
	
	/**
	 * @param x x location in map
	 * @param y y location in map
	 * @param numberMap the map of all know numbers in map
	 * @return the numbers adjacent to the given location in map
	 */
	private static Set<Number> findPartNumbers(int x, int y, Map<Point, Number> numberMap) {
		Set<Number> result = new HashSet<Number>();
		for (int px = x-1; px < x+2;px++) {
			for (int py = y-1; py < y+2;py++) {
				Point location = new Point(px, py);
				Number number = numberMap.get(location);
				if (number != null) result.add(number);
			}
		}
		return result;
	}

	/**
	 * @param lineIndex current Y location in map
	 * @param line the current line of text in map
	 * @param numberMap
	 * @return
	 */
	private static void parseNumbers(int lineIndex, String line, Map<Point, Number> numberMap) {
		Number number = null;
		for (int i=0;i<line.length();i++) {
			char charAt = line.charAt(i);
			if (Character.isDigit(charAt)) {
				if (number == null) number = new Number();
				number.append(charAt - '0');
				numberMap.put(new Point(i, lineIndex), number);
			}
			else {
				number = null;
			}
		}
	}
	
	private static class Number {

		private int value = 0;

		public void append(int i) {
			value = 10 * value + i;
		}

		@Override
		public String toString() {
			return String.valueOf(value);
		}
	}
}
