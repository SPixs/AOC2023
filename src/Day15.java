import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public class Day15 {
	
	public static class Box {

		int number = 0;
		public Map<String, Lens> lenses = new LinkedHashMap<String, Lens>();
		
		public Box(int number) {
			this.number = number;
		}

		public int getFocusingPower() {
			int result = 0;
			int index = 0;
			for (Lens lens : lenses.values()) {
				result += (number+1)*(++index)*lens.focalLength;			
			}
			return result;
		}

		public void putLens(Lens lens) {
			lenses.put(lens.label, lens);
		}

		public void removeLensWithLabel(String label) {
			lenses.remove(label);
		}
	}

	public static class Lens {

		private String label;
		public int focalLength;

		public Lens(String label, int focalLength) {
			this.label = label;
			this.focalLength = focalLength;
		}
	}

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day15.txt"));

		// Part 1
		long startTime = System.nanoTime();
		String[] split = lines.get(0).split(",");
		int result = Arrays.stream(split).mapToInt(Day15::computeHash).sum();
		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");
		
		// Part 2
		startTime = System.nanoTime();
		Box[] boxes = new Box[256];
		for (int i=0;i<256;i++) boxes[i] = new Box(i);
		
		for (String step : split) {
			int index = Math.max(step.indexOf("-"), step.indexOf("="));
			String label = step.substring(0, index);
			int boxIndex = computeHash(label);
			char operation = step.charAt(index);
			if (operation == '=') {
				int focalLength = Integer.parseInt(step.substring(index+1));
				boxes[boxIndex].putLens(new Lens(label, focalLength));
			}
			else {
				boxes[boxIndex].removeLensWithLabel(label);
			}
		}
		result = Arrays.stream(boxes).mapToInt(Box::getFocusingPower).sum();
		System.out.println("Result part 2 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");
	}

	private static int computeHash(String string) {
		
		int result = 0;
		for (char c : string.toCharArray()) {
			result += (int) c;
			result = (result * 17) % 256;
		}
		return result;
	}
}


