import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

public class Day01 {

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day01.txt"));

		// Part 1
		int result = lines.stream().map(line -> line.replaceAll("[^0-9]",""))
				.mapToInt(calibration -> 10*(calibration.charAt(0)-'0') + (calibration.charAt(calibration.length()-1)-'0')).sum();
		System.out.println("Result part 1 : " + result);
		
		// Part 2
		final List<String> literalTokens = Arrays.asList("one", "two", "three", "four", "five", "six", "seven", "eight", "nine");
		final List<String> tokens = new ArrayList<String>(literalTokens);
		IntStream.range(0, 10).forEach(digit -> tokens.add(String.valueOf(digit)));
		
		ToIntFunction<String> lineToCalibration = new ToIntFunction<String>() {
			public int applyAsInt(String s) {
				String first = tokens.stream().filter(token -> s.contains(token)).min(((t1, t2) -> Integer.compare(s.indexOf(t1),  s.indexOf(t2)))).orElseThrow();
				String second = tokens.stream().filter(token -> s.contains(token)).max(((t1, t2) ->  Integer.compare(s.lastIndexOf(t1), s.lastIndexOf(t2)))).orElseThrow();
				
				int tens = literalTokens.contains(first) ? tokens.indexOf(first) + 1 : Integer.parseInt(first);
				int units = literalTokens.contains(second) ? tokens.indexOf(second) + 1 : Integer.parseInt(second);
				
				return 10*tens+units;
			}
		};
		result = lines.stream().mapToInt(lineToCalibration).sum();
		System.out.println("Result part 2 : " + result);
	}

}
