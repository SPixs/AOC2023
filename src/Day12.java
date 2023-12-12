import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Day12 {
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day12.txt"));

		// Part 1
		BigInteger result = BigInteger.ZERO;
		for (String line : lines) {
			char[] springs = line.split(" ")[0].toCharArray();
			int[] damagedGroups = Arrays.stream(line.split(" ")[1].split(",")).mapToInt(Integer::parseInt).toArray();
			result = result.add(countArrangement(springs, damagedGroups)); 
		}
		
		System.out.println("Result part 1 : " + result);
		
		// Part 2
		result = lines.parallelStream().map(line -> countArrangements(line)).reduce(BigInteger.ZERO, (a,b) -> BigInteger.ZERO.add(a).add(b));
		System.out.println("Result part 2 : " + result);
	}
	
	private static BigInteger countArrangements(String line) {
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
		
		BigInteger arrangements = countArrangement(extendedSprings, extendeddDamagedGroups);
		return arrangements;
	}
	
	
	
	private static BigInteger countArrangement(char[] springs, int[] damagedGroups) {
		Map<CacheKey, BigInteger> cache = new ConcurrentHashMap<CacheKey, BigInteger>();
		return countArrangement(0, springs, damagedGroups, new ArrayList<Integer>(), new AtomicInteger(), cache);
	}
	
	
	private static BigInteger countArrangement(int index, char[] springs, int[] damagedGroups, List<Integer> computedDamagedGroups, 
			AtomicInteger damageCounter, Map<CacheKey, BigInteger> cache) {

		CacheKey cacheKey = new CacheKey(index, computedDamagedGroups, damagedGroups, damageCounter.intValue());
		BigInteger result = cache.get(cacheKey);
		if (result != null) {
			return result;
		}
		
		if (damageCounter.get() > 0 && computedDamagedGroups.size() >= damagedGroups.length) {
			return BigInteger.ZERO;
		}
		
		if (damageCounter.get() > 0 && damageCounter.get() > damagedGroups[computedDamagedGroups.size()]) {
			return BigInteger.ZERO;
		}
		
		if (index >= springs.length) {
			if (damageCounter.get() > 0) {
				computedDamagedGroups.add(damageCounter.get()); 
			}
			boolean valid = computedDamagedGroups.size() == damagedGroups.length && computedDamagedGroups.get(computedDamagedGroups.size()-1) == damagedGroups[computedDamagedGroups.size()-1];
			if (damageCounter.get() > 0) {
				computedDamagedGroups.remove(computedDamagedGroups.size()-1);
			}
			return valid ? BigInteger.ONE : BigInteger.ZERO;
		}
		
		char state = springs[index];

		if (state == '#') { 
			damageCounter.incrementAndGet();  
			result = countArrangement(index + 1, springs, damagedGroups, computedDamagedGroups, damageCounter, cache);
			damageCounter.decrementAndGet();
			cache.put(cacheKey, result);
			return result;
		}
		if (state == '.') { 
			int damageValue = damageCounter.get();
			boolean valid = true;
			if (damageValue > 0) {
				computedDamagedGroups.add(damageValue); 
				valid = computedDamagedGroups.size() <= damagedGroups.length && damageValue == damagedGroups[computedDamagedGroups.size()-1];
				damageCounter.set(0); 
			}
			result = valid ? countArrangement(index + 1, springs, damagedGroups, computedDamagedGroups, damageCounter, cache) : BigInteger.ZERO;
			if (damageValue > 0) {
				computedDamagedGroups.remove(computedDamagedGroups.size()-1);
			}
			damageCounter.set(damageValue);
			cache.put(cacheKey, result);
			return result;
		}
		else {
			// simulate '#'
			damageCounter.incrementAndGet();
			result = BigInteger.ZERO.add(countArrangement(index + 1, springs, damagedGroups, computedDamagedGroups, damageCounter, cache));
			damageCounter.decrementAndGet();
			
			// simulate '.'
			boolean check = true;
			int damageValue = damageCounter.get();
			if (damageValue > 0) {
				computedDamagedGroups.add(damageValue); 
				check = computedDamagedGroups.size() <= damagedGroups.length && damageValue == damagedGroups[computedDamagedGroups.size()-1];
				damageCounter.set(0); 
			}
			result = check ? result.add(countArrangement(index + 1, springs, damagedGroups, computedDamagedGroups, damageCounter, cache)): result;
			if (damageValue > 0) {
				computedDamagedGroups.remove(computedDamagedGroups.size()-1);
			}
			damageCounter.set(damageValue);
			cache.put(cacheKey, result);
			return result;
		}
	}
	
	private static class CacheKey {
		
		private int index;
		private List<Integer> computedDamagedGroups;
		private int[] damagedGroups;
		private int damageCounter;
		
		public CacheKey(int index, List<Integer> computedDamagedGroups, int[] damagedGroups, int damageCounter) {
			this.index = index;
			this.computedDamagedGroups = new ArrayList<Integer>(computedDamagedGroups);
			this.damagedGroups = damagedGroups;
			this.damageCounter = damageCounter;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((computedDamagedGroups == null) ? 0 : computedDamagedGroups.hashCode());
			result = prime * result + damageCounter;
			result = prime * result + Arrays.hashCode(damagedGroups);
			result = prime * result + index;
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
			CacheKey other = (CacheKey) obj;
			if (computedDamagedGroups == null) {
				if (other.computedDamagedGroups != null)
					return false;
			} else if (!computedDamagedGroups.equals(other.computedDamagedGroups))
				return false;
			if (damageCounter != other.damageCounter)
				return false;
			if (!Arrays.equals(damagedGroups, other.damagedGroups))
				return false;
			if (index != other.index)
				return false;
			return true;
		}

		
	}
}


