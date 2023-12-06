import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day06 {
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day06.txt"));

		List<Integer> timeList = Arrays.stream(lines.get(0).split(":")[1].trim().split(" ")).filter(s -> !s.isBlank()).map(Integer::parseInt).collect(Collectors.toList()); 
		List<Integer> distanceList = Arrays.stream(lines.get(1).split(":")[1].trim().split(" ")).filter(s -> !s.isBlank()).map(Integer::parseInt).collect(Collectors.toList()); 
		
		// Part 1
		int result = 1;
		for (int i=0;i<timeList.size();i++) {
			int time = timeList.get(i);
			int distance = distanceList.get(i);
			
			int waysToBeat = getWaysToBeat(time, distance);
			result *= waysToBeat;
		}
		
		System.out.println("Result part 1 : " + result);
		
		// Part 2
		long time = Long.parseLong(lines.get(0).split(":")[1].trim().replaceAll(" ", ""));
		long distance = Long.parseLong(lines.get(1).split(":")[1].trim().replaceAll(" ", ""));
		System.out.println("Result part 2 : " + getWaysToBeat(time, distance));
	}

	private static int getWaysToBeat(long time, long distance) {
		double delta = time * time - 4 * distance;
		double r1 = (time - Math.sqrt(delta)) / 2;
		double r2 = (time + Math.sqrt(delta)) / 2;
		
		int min = (int) (Math.ceil(r1) == r1 ? r1 + 1 : Math.ceil(r1));
		int max = (int) (Math.floor(r2) == r2 ? r2 - 1 : Math.floor(r2));

		int waysToBeat = max - min + 1;
		return waysToBeat;
	}
}
