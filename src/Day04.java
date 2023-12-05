import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day04 {
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day04.txt"));

		// Part 1
		List<Card> originalCards = new ArrayList<Card>();
		int result = 0;
		int cardIndex = 1;
		for (String line : lines) {
			String[] allNumbers = line.split(":")[1].split("\\|");
			Set<Integer> winningNumbers = Arrays.stream(allNumbers[0].trim().split(" ")).filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toSet());
			Set<Integer> numbers = Arrays.stream(allNumbers[1].trim().split(" ")).filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toSet());
			
			int score = (int) numbers.stream().filter(winningNumbers::contains).count();
			int point = (1 << score) >> 1;

			originalCards.add(new Card(cardIndex++, score));
			result += point;
		}
		
		System.out.println("Result part 1 : " + result);

		// Part 2
		int processedCards = 0;
		for (Card card : originalCards) {
				for (int i=card.id;i<card.id+card.score;i++) {
					originalCards.get(i).count += card.count;
				}
				processedCards += card.count;
		}
		System.out.println("Result part 2 : " + processedCards);
	}

	private static class Card {
		
		private int id;
		private int score;
		protected int count;

		public Card(int id, int score) {
			this.id = id; 
			this.score = score;
			this.count = 1;
		}
	}
}