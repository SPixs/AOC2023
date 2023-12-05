import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day05 {
	
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
		
		long minLocation = Long.MAX_VALUE;
		for (long seed : seeds) {
			long soil = map(seedToSoilRules, seed);
			long fertilizer = map(soilToFertilizer, soil);
			long water = map(fertilizerToWater, fertilizer);
			long light = map(waterToLight, water);
			long temperature = map(lightToTemperature, light);
			long humidity = map(temperatureToHumidity, temperature);
			long location = map(humidityToLocation, humidity);
			
			minLocation = Math.min(minLocation, location);
			
		}
		
		System.out.println("Result part 1 : " + minLocation);
		
		System.out.println("Computing part 2... (to be optimized)");
		minLocation = Long.MAX_VALUE;
		for (int i=0;i<seeds.size();i+=2) {
			for (long seed = seeds.get(i); seed < seeds.get(i) + seeds.get(i+1); seed++) {
				long soil = map(seedToSoilRules, seed);
				long fertilizer = map(soilToFertilizer, soil);
				long water = map(fertilizerToWater, fertilizer);
				long light = map(waterToLight, water);
				long temperature = map(lightToTemperature, light);
				long humidity = map(temperatureToHumidity, temperature);
				long location = map(humidityToLocation, humidity);
				
				minLocation = Math.min(minLocation, location);
			}
		}
		
		System.out.println("Result part 2 : " + minLocation);
		
	}

	private static long map(List<Rule> rules, long value) {
		for (Rule rule : rules) {
			if (rule.accept(value)) return rule.map(value);
		}
		return value;
	}

}
