import java.awt.Point;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day04 {
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day04.txt"));

		List<Card> originalCards = new ArrayList<Card>();
		int result = 0;
		int cardIndex = 1;
		for (String line : lines) {
			String[] allNumbers = line.split(":")[1].split("\\|");
			Set<Integer> winningNumbers = Arrays.stream(allNumbers[0].trim().split(" ")).filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toSet());
			Set<Integer> numbers = Arrays.stream(allNumbers[1].trim().split(" ")).filter(s -> !s.isEmpty()).map(Integer::parseInt).collect(Collectors.toSet());
			int point = 0;
			int score = 0;
			for (int number : numbers) {
				if (winningNumbers.contains(number)) { 
					point = point == 0 ? 1 : point << 1; 
					score++;
				}
			}
			originalCards.add(new Card(cardIndex++, score));
			result += point;
		}
		
		System.out.println("Result part 1 : " + result);
		
		System.out.println("Processing part 2 with brute force (to be optimized)..." + result);
		Set<Card> cardsToProcess = new HashSet<Card>(originalCards);
		int processedCards = 0;
		while (!cardsToProcess.isEmpty()) {
			Card nextCard = cardsToProcess.iterator().next();
			processedCards++;
			cardsToProcess.remove(nextCard);
			for (int i=nextCard.id+1;i<nextCard.id+1+nextCard.score;i++) {
				cardsToProcess.add(originalCards.get(i-1).getCopy());
			}
		}

		// 5704953
		System.out.println("Result part 2 : " + processedCards);
	}

	private static class Card {
		
		private int id;
		private int score;

		public Card(int id, int score) {
			this.id = id; 
			this.score = score;
		}

		public Card getCopy() { return new Card(id, score); }
		
	}
}
