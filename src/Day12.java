import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day12 {
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day12.txt"));

		// Part 1
		long result = 0;
		for (String line : lines) {
			char[] springs = line.split(" ")[0].toCharArray();
			int[] damagedGroups = Arrays.stream(line.split(" ")[1].split(",")).mapToInt(Integer::parseInt).toArray();
			result += countArrangement(springs, damagedGroups); 
		}
		
		System.out.println("Result part 1 : " + result);
		
		// Part 2
		result = lines.stream().mapToLong(line -> countArrangements(line)).sum();
//		result = lines.parallelStream().mapToLong(line -> countArrangements(line)).sum();
		System.out.println("Result part 2 : " + result);
	}
	
	static AtomicInteger processed = new AtomicInteger();

	private static int countArrangements(String line) {
		char[] springs = line.split(" ")[0].toCharArray();
		// 5 copies
		char[] extendedSprings = new char[4+springs.length*5];
		for (int i=0;i<5;i++) {
			if (i>0) extendedSprings[i*(springs.length+1)-1] = '?';
			System.arraycopy(springs, 0, extendedSprings, i*(springs.length+1), springs.length);
		}
		
		int[] damagedGroups = Arrays.stream(line.split(" ")[1].split(",")).mapToInt(Integer::parseInt).toArray();
		int[] extendeddDamagedGroups = new int[damagedGroups.length*5];
		for (int i=0;i<5;i++) {
			System.arraycopy(damagedGroups, 0, extendeddDamagedGroups, i*(damagedGroups.length), damagedGroups.length);
		}
		
		int arrangements = countArrangement(extendedSprings, extendeddDamagedGroups);
		System.out.println(processed.incrementAndGet() + " " + arrangements);
		return arrangements;
	}
	
	
	
	private static int countArrangement(char[] springs, int[] damagedGroups) {
		AtomicInteger counter = new AtomicInteger(0);
		countArrangement(0, springs, damagedGroups, new ArrayList<Integer>(), new AtomicInteger(), counter);
		return counter.intValue();
	}
	
	private static boolean countArrangement(int index, char[] springs, int[] damagedGroups, List<Integer> computedDamagedGroups, AtomicInteger damageCounter, AtomicInteger counter) {

//		if (!check(computedDamagedGroups, damagedGroups)) return false;
		
		if (index >= springs.length) {
			if (damageCounter.get() > 0) {
				computedDamagedGroups.add(damageCounter.get()); 
			}
//			boolean valid = Arrays.equals(damagedGroups, computedDamagedGroups.stream().mapToInt(Integer::intValue).toArray());
			boolean valid = checkEqual(computedDamagedGroups, damagedGroups);
			if (damageCounter.get() > 0) {
				computedDamagedGroups.remove(computedDamagedGroups.size()-1);
			}
			if (valid) counter.incrementAndGet();
			return valid;
		}
		
		char state = springs[index];

		if (state == '#') { 
			damageCounter.incrementAndGet();  
			boolean valid = countArrangement(index + 1, springs, damagedGroups, computedDamagedGroups, damageCounter, counter);
			damageCounter.decrementAndGet();
			return valid;
		}
		if (state == '.') { 
			int damageValue = damageCounter.get();
			boolean valid = true;
			if (damageValue > 0) {
				computedDamagedGroups.add(damageValue); 
				valid = check(computedDamagedGroups, damagedGroups);
				damageCounter.set(0); 
			}
			valid = valid && countArrangement(index + 1, springs, damagedGroups, computedDamagedGroups, damageCounter, counter);
			if (damageValue > 0) {
				computedDamagedGroups.remove(computedDamagedGroups.size()-1);
			}
			damageCounter.set(damageValue);
			return valid;
		}
		else {
			// simulate '#'
			damageCounter.incrementAndGet();
			boolean valid = countArrangement(index + 1, springs, damagedGroups, computedDamagedGroups, damageCounter, counter);
			damageCounter.decrementAndGet();
			
			// simulate '.'
			boolean check = true;
			int damageValue = damageCounter.get();
			if (damageValue > 0) {
				computedDamagedGroups.add(damageValue); 
				check = check(computedDamagedGroups, damagedGroups);
				damageCounter.set(0); 
			}
			valid |= check && countArrangement(index + 1, springs, damagedGroups, computedDamagedGroups, damageCounter, counter);
			if (damageValue > 0) {
				computedDamagedGroups.remove(computedDamagedGroups.size()-1);
			}
			damageCounter.set(damageValue);
			return valid;
		}
	}
	
	private static boolean check(List<Integer> computedDamagedGroups, int[] damagedGroups) {
		if (computedDamagedGroups.size() > damagedGroups.length) { return false; }
		for (int i=0;i<computedDamagedGroups.size();i++) {
			if (computedDamagedGroups.get(i) != damagedGroups[i]) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean checkEqual(List<Integer> computedDamagedGroups, int[] damagedGroups) {
		if (computedDamagedGroups.size() != damagedGroups.length) { return false; }
		for (int i=0;i<computedDamagedGroups.size();i++) {
			if (computedDamagedGroups.get(i) != damagedGroups[i]) {
				return false;
			}
		}
		return true;
	}
}


