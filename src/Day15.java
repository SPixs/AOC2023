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
		public Map<String, Integer> lenses = new LinkedHashMap<String, Integer>();
		
		public Box(int number) {
			this.number = number;
		}

		public int getFocusingPower() {
			List<Integer> lensesList = new ArrayList<Integer>(lenses.values());
			return IntStream.range(0, lensesList.size()).map(i -> lensesList.get(i)*(i+1)).sum() * (number+1);
		}

		public void putLens(String label, int focalLength) { lenses.put(label, focalLength); }
		public void removeLensWithLabel(String label) { lenses.remove(label); }
	}

	private static int computeHash(String string) {
		return string.chars().reduce(0, (a,b) -> ((a + b) * 17) % 256);
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
			Box box = boxes[computeHash(label)];
			char operation = step.charAt(index);
			if (operation == '=') {
				int focalLength = Integer.parseInt(step.substring(index+1));
				box.putLens(label, focalLength);
			}
			else {
				box.removeLensWithLabel(label);
			}
		}
		result = Arrays.stream(boxes).mapToInt(Box::getFocusingPower).sum();
		System.out.println("Result part 2 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");
	}
}


