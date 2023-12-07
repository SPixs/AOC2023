import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day05 {
	
	private static class Range {
		
		private long start;
		private long end; // exclusive

		public Range(long start, long end) {
			this.start = start;
			this.end = end;
		}

		public Set<Range> partition(Collection<Range> otherRanges) {
			
			Set<Range> result = new HashSet<Range>();

			boolean intersectFound = true;
			result.add(this);
			while (intersectFound) {
				intersectFound = false;
				for (Range r : new HashSet<Range>(result)) {
					for (Range other :  otherRanges) {
						List<Range> partitions = r.partion(other);
						if (partitions.size() > 1) {
							intersectFound = true;
							result.remove(r);
							result.addAll(partitions);
						}
					}
					
				}
			}
			
			
			return result;
		}

		private List<Range> partion(Range otherRange) {
			 
			List<Range> result = new ArrayList<Range>();
			long intersectionStart = Math.max(start, otherRange.start);
			long intersectionEnd = Math.min(end, otherRange.end);

			
			// Si les intervalles se chevauchent
	        if (intersectionStart < intersectionEnd) {
	        	Range firstRange = new Range(start, intersectionStart);
	        	Range intersectionRange = new Range(intersectionStart, intersectionEnd);
	        	Range lastRange = new Range(intersectionEnd, end);
	        	if (firstRange.isValid()) result.add(firstRange);
	        	if (intersectionRange.isValid()) result.add(intersectionRange);
	        	if (lastRange.isValid()) result.add(lastRange);
	        }
			
            return result;
		}

		private boolean isValid() {
			return end > start;
		}

		private boolean intersect(Range otherRange) {
			return end > otherRange.start && start < otherRange.end;
		}
		
		@Override
		public String toString() {
			return "["+start+"-"+(end-1)+"]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (int) (end ^ (end >>> 32));
			result = prime * result + (int) (start ^ (start >>> 32));
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
			Range other = (Range) obj;
			if (end != other.end)
				return false;
			if (start != other.start)
				return false;
			return true;
		}
	}
	
	private static class Mapping {
		
		private List<AbstractMap.SimpleEntry<Range, Range>> rangesPairs;
		
		public Mapping(List<Rule> rules) {
			
			rangesPairs = rules.stream().map(r -> new AbstractMap.SimpleEntry<Range, Range>(new Range(r.from, r.from+r.length), new Range(r.to, r.to+r.length))).collect(Collectors.toList());
		}

		private List<Range> forward(Range range) {
			Set<Range> partitionRanges = partitionRanges(Collections.singletonList(range), getSourcesRanges());
			return partitionRanges.stream().map(r -> {
				for (AbstractMap.SimpleEntry<Range, Range> pair : rangesPairs) {
					if (pair.getKey().intersect(r)) {
						long delta = pair.getValue().start - pair.getKey().start;
						return new Range(r.start+delta, r.end+delta);
					}
				}
				return r; 
			}).collect(Collectors.toList());
		}

		private Set<Range> partitionRanges(List<Range> ranges, List<Range> otherRanges) {
			Set<Range> result = new HashSet<Range>();
			for (Range range : ranges) {
				result.addAll(range.partition(otherRanges));
			}
			return result;
		}
			
		private List<Range> getSourcesRanges() {
			return rangesPairs.stream().map(AbstractMap.SimpleEntry::getKey).collect(Collectors.toList());
		}
	}
	
	public static class Rule {

		private long from;
		private long to;
		private long length;

		public Rule(long from, long to, long length) {
			this.from = from;
			this.to = to;
			this.length = length;
		}

		public boolean accept(long value) {
			return value >= from && value - from < length;
		}
		
		public long map(long value) {
			return to + value - from;
		}
	}

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day05.txt"));

		List<Long> seeds = null;		
		List<Rule> seedToSoilRules = new ArrayList<Rule>();
		List<Rule> soilToFertilizer = new ArrayList<Rule>();
		List<Rule> fertilizerToWater = new ArrayList<Rule>();
		List<Rule> waterToLight = new ArrayList<Rule>();
		List<Rule> lightToTemperature = new ArrayList<Rule>();
		List<Rule> temperatureToHumidity = new ArrayList<Rule>();
		List<Rule> humidityToLocation = new ArrayList<Rule>();;

		List<Rule> currentRule = null;
		for (String line : lines) {
			if (line.contains("seeds")) {
				seeds = Arrays.stream(line.split(":")[1].trim().split(" ")).map(Long::parseLong).collect(Collectors.toList());
			}
			else if (line.contains("map")) {
				if (line.contains("seed-to-soil")) { currentRule = seedToSoilRules; }
				else if (line.contains("soil-to-fertilizer")) { currentRule = soilToFertilizer; }
				else if (line.contains("fertilizer-to-water")) { currentRule = fertilizerToWater; }
				else if (line.contains("water-to-light")) { currentRule = waterToLight; }
				else if (line.contains("light-to-temperature")) { currentRule = lightToTemperature; }
				else if (line.contains("temperature-to-humidity")) { currentRule = temperatureToHumidity; }
				else if (line.contains("humidity-to-location")) { currentRule = humidityToLocation; }
			}				
			else if (!line.isEmpty()) {
				Long[] values = Arrays.stream(line.split(" ")).map(Long::parseLong).toArray(Long[]::new);;
				currentRule.add(new Rule(values[1], values[0], values[2]));
			}
		}
		
		Mapping seedToSoilMapping = new Mapping(seedToSoilRules);
		Mapping soilToFertilizerMapping = new Mapping(soilToFertilizer);
		Mapping fertilizerToWaterMapping = new Mapping(fertilizerToWater);
		Mapping waterToLightMapping = new Mapping(waterToLight);
		Mapping lightToTemperatureMapping = new Mapping(lightToTemperature);
		Mapping temperatureToHumidityMapping = new Mapping(temperatureToHumidity);
		Mapping humidityToLocationMapping = new Mapping(humidityToLocation);
		
		// Part 1
		long startTime = System.nanoTime();
		long minLocation = seeds.stream().map(s -> new Range(s, s+1))
			.map(range -> seedToSoilMapping.forward(range)).flatMap(l -> l.stream())
			.map(range -> soilToFertilizerMapping.forward(range)).flatMap(l -> l.stream()) //.collect(Collectors.toList());
			.map(range -> fertilizerToWaterMapping.forward(range)).flatMap(l -> l.stream())
			.map(range -> waterToLightMapping.forward(range)).flatMap(l -> l.stream())
			.map(range -> lightToTemperatureMapping.forward(range)).flatMap(l -> l.stream())
			.map(range -> temperatureToHumidityMapping.forward(range)).flatMap(l -> l.stream())
			.map(range -> humidityToLocationMapping.forward(range)).flatMap(l -> l.stream()) //.collect(Collectors.toList())
			.mapToLong(range -> range.start).min().orElseThrow();
		
		
		System.out.println("Part 1 time : " + (System.nanoTime() - startTime) / 1000000 + "ms");
		System.out.println("Result part 1 : " + minLocation);

		// Part 2
		startTime = System.nanoTime();
		List<Range> ranges = new ArrayList<Range>();
		for (int i=0;i<seeds.size();i+=2) {
			ranges.add(new Range(seeds.get(i), seeds.get(i)+seeds.get(i+1)+1));
		}
			
		minLocation = ranges.stream().map(range -> seedToSoilMapping.forward(range)).flatMap(l -> l.stream())
			.map(range -> soilToFertilizerMapping.forward(range)).flatMap(l -> l.stream()) //.collect(Collectors.toList());
			.map(range -> fertilizerToWaterMapping.forward(range)).flatMap(l -> l.stream())
			.map(range -> waterToLightMapping.forward(range)).flatMap(l -> l.stream())
			.map(range -> lightToTemperatureMapping.forward(range)).flatMap(l -> l.stream())
			.map(range -> temperatureToHumidityMapping.forward(range)).flatMap(l -> l.stream())
			.map(range -> humidityToLocationMapping.forward(range)).flatMap(l -> l.stream()) //.collect(Collectors.toList())
			.mapToLong(range -> range.start).min().orElseThrow();
			
		System.out.println("Part 2 time : " + (System.nanoTime() - startTime) / 1000000 + "ms");
		System.out.println("Result part 2 : " + minLocation);
			
	}
}
