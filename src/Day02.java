import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day02 {
	
	private static class SetOfCubes {
		
		public int redCubes;
		public int greenCubes;
		public int blueCubes;
		
	}
	
	private static class Game {
		
		private int id;
		private Set<SetOfCubes> setOfCubes;

		public Game(int id) {
			this.id = id;
		}

		public void setSetOfCubes(Set<SetOfCubes> setOfCubes) {
			this.setOfCubes = setOfCubes;
		}
	
		/**
		 * @return true if the game would have been possible if the bag had been loaded with that configuration.
		 */
		public boolean filter(int redCount, int greenCount, int blueCount) {
			for (SetOfCubes set : setOfCubes) {
				if (set.redCubes > redCount || set.greenCubes > greenCount || set.blueCubes > blueCount) return false;
			}
			return true;
		}
		
		public List<Integer> findMinCubes() {
			List<Integer> result = new ArrayList<Integer>();
			result.add(setOfCubes.stream().mapToInt(s -> s.redCubes).max().orElseThrow());
			result.add(setOfCubes.stream().mapToInt(s -> s.greenCubes).max().orElseThrow());
			result.add(setOfCubes.stream().mapToInt(s -> s.blueCubes).max().orElseThrow());
			return result;
		}
	}

	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day02.txt"));
		List<Game> games = new ArrayList<Game>();
		for (String line : lines) {
			int gameId = Integer.parseInt(line.substring(5, line.indexOf(":")));
			String[] sets = line.split(":")[1].split(";");
			Game game = new Game(gameId);
			game.setSetOfCubes(createSetOfCubes(sets));
			games.add(game);
		}
		
		// Part 1
		int result = games.stream().filter(g -> g.filter(12, 13, 14)).mapToInt(g -> g.id).sum();
		System.out.println("Result part 1 : " + result);
		
		// Part 2
		result = games.stream().map(g -> g.findMinCubes()).mapToInt(l -> l.stream().reduce(1, (a, b) -> a * b)).sum();
		System.out.println("Result part 2 : " + result);
	}
	
	private static int extractCubeCount(String phrase, String color) {
		String pattern = "(\\d+)\\s+" + color;
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(phrase);

        return matcher.find() ? Integer.parseInt(matcher.group(1)) : 0;
	}

	private static Set<SetOfCubes> createSetOfCubes(String[] sets) {
		Set<SetOfCubes> result = new HashSet<SetOfCubes>();
		for (String set : sets) {
			
			SetOfCubes setOfCubes = new SetOfCubes();
			setOfCubes.redCubes = extractCubeCount(set, "red");
			setOfCubes.greenCubes = extractCubeCount(set, "green");
			setOfCubes.blueCubes = extractCubeCount(set, "blue");

			result.add(setOfCubes);
		}
		return result;
	}

}
