import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day09 {
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day09.txt"));

		List<List<Long>> input = lines.stream()
				  .map(line -> Arrays.stream(line.split(" "))
				  .map(Long::parseLong).collect(Collectors.toList()))
	              .collect(Collectors.toList());
		
		// Part 1
		System.out.println("Result part 1 : " + input.stream().mapToLong(l -> extrapolate(l, false)).sum());
		
		// Part 2
		System.out.println("Result part 2 : " + input.stream().mapToLong(l -> extrapolate(l, true)).sum());
	}

	private static long extrapolate(List<Long> line, boolean part2) {
		if (isAllZeros(line)) return 0;
		List<Long> newLine = IntStream.range(0, line.size()-1).mapToObj(i -> line.get(i+1)-line.get(i)).collect(Collectors.toList());
		return part2 ? line.get(0) - extrapolate(newLine, part2) : line.get(line.size()-1) + extrapolate(newLine, part2);
	}

	private static boolean isAllZeros(List<Long> list) {
		return list.stream().allMatch(l -> l == 0L);
    }
}


