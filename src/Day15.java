import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.UnaryOperator;

public class Day15 {
	
	public static class Box {

		int number = 0;
		public List<Lens> lenses = new ArrayList<Lens>();
		
		public Box(int number) {
			this.number = number;
		}

		public int getFocusingPower() {
			int result = 0;
			for (int i=0;i<lenses.size();i++) {
				result += (number+1)*(i+1)*lenses.get(i).focalLength;			
			}
			return result;
		}

		public void putLens(Lens lens) {
			int index = lenses.indexOf(lens);
			if (index >= 0) {
				lenses.set(index, lens);
			}
			else {
				lenses.add(lens);
			}
		}

		public void removeLensWithLabel(String label) {
			int index = lenses.indexOf(new Lens(label, 0));
			if (index >= 0) {
				lenses.remove(index);
			}
		}
	}

	public static class Lens {

		private String label;
		public int focalLength;

		public Lens(String label, int focalLength) {
			this.label = label;
			this.focalLength = focalLength;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null || !(obj instanceof Lens)) return false;
			return Objects.equals(label, ((Lens)obj).label);
		}
	}

	public static void main(String[] args) throws IOException {

		List<String> lines = Files.readAllLines(Path.of("input_day15.txt"));

		// Part 1
		long startTime = System.nanoTime();
		String[] split = lines.get(0).split(",");
		int result = 0;
		for (String string : split) {
			result += computeHash(string);
		}
		
		System.out.println("Result part 1 : " + result + " in " + TimeUnit.NANOSECONDS.toMillis((System.nanoTime()-startTime))+"ms");
		
		// Part 2
		startTime = System.nanoTime();
		Box[] boxes = new Box[256];
		for (int i=0;i<256;i++) boxes[i] = new Box(i);
		
		for (String string : split) {
			int index = Math.max(string.indexOf("-"), string.indexOf("="));
			String label = string.substring(0, index);
			int boxIndex = computeHash(label);
			char operation = string.charAt(index);
			if (operation == '=') {
				int focalLength = Integer.parseInt(string.substring(index+1));
				Lens lens = new Lens(label, focalLength);
				boxes[boxIndex].putLens(lens);
			}
			else {
				boxes[boxIndex].removeLensWithLabel(label);
			}
		}
		result = 0;
		for (Box box : boxes) {
			result += box.getFocusingPower();
		}
		System.out.println(result);
	}

	private static int computeHash(String string) {
		int result = 0;
		for (char c : string.toCharArray()) {
			result += (int) c;
			result *= 17;
			result = result % 256;
		}
		return result;
	}
}


