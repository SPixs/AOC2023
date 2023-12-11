import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day07 {
	
	public static void main(String[] args) throws IOException {
		List<String> lines = Files.readAllLines(Path.of("input_day07.txt"));

		List<Hand> hands = new ArrayList<Hand>();
		for (String line : lines) {
			String hand = line.split(" ")[0];
			int bid = Integer.parseInt(line.split(" ")[1]);
			hands.add(new Hand(hand, bid));
		}
		
		// Part 1
		Collections.sort(hands);
		int rank = 1;
		int result = 0;
		for (Hand hand : hands) {
			result += (rank++)*hand.bid;
		}
		System.out.println("Result part 1 : " + result);
		
		// Part 2
		hands.forEach(h -> {
//			System.out.println(h.characteres);
			h.fixJokers();
//			System.out.println(h.characteres);
//			System.out.println();
		});
		Collections.sort(hands);
		rank = 1;
		result = 0;
		for (Hand hand : hands) {
			result += (rank++)*hand.bid;
		}
		System.out.println("Result part 2 : " + result);
	}

	public static class Hand implements Comparable<Hand> {

		private ArrayList<Character> characteres;
		private int type;
		
		private Map<Character, Long> cardOccurence;
		
		private char[] cardLabels = "23456789TJQKA".toCharArray();
		private int bid;
		
		private char[] cardLabelsPart2 = "J23456789TQKA".toCharArray();

		
		public Hand(String hand, int bid) {
			this.bid = bid;
			char[] allChars = hand.toCharArray();
			characteres = new java.util.ArrayList<Character>();
	        for (char caractere : allChars) {
	        	characteres.add(caractere);
	        }
	        
	        computeType();
		}

		private void computeType() {
			cardOccurence = characteres.stream()
	                .collect(Collectors.groupingBy(
	                        card -> card, 
	                        Collectors.counting() 
	                ));

	        long highestOccurence = cardOccurence.values().stream().mapToLong(l -> l).max().orElseThrow();
	        
	        int uniqueCards = new HashSet<Character>(characteres).size();
	        switch (uniqueCards) {
	        	case 5: type = 1; break; // High card
	        	case 4: type = 2; break; // One pair
	        	case 3: type = highestOccurence == 2 ? 3 : 4; break; // Two pair or Three of a kind
	        	case 2: type = highestOccurence == 3 ? 5 : 6; break; // Full house or Four of a kind
	        	case 1: type = 7; break;
	        	default:
	        		throw new IllegalStateException();
	        }
		}
		
		public Hand(Hand bestHand) {
			this.characteres = new ArrayList<Character>(bestHand.characteres);
		}

		public void fixJokers() {
			Hand bestHand = this;
			bestHand = recurse(0, bestHand);
			this.characteres = bestHand.characteres;
			computeType();
		}

		private Hand recurse(int i, Hand bestHand) {
			if (i >= 5) return this.compareTo(bestHand) > 0 ? this : bestHand;

			char c = characteres.get(i);
			if (c == 'J') {
				for (char nc : cardLabels) {
					Hand newHand = new Hand(bestHand);
					newHand.characteres.set(i, nc);
					newHand.computeType();
					Hand newBestHand = newHand.recurse(i+1, bestHand);
					if (newBestHand.compareTo(bestHand) > 0) bestHand = newBestHand;
				}
				return bestHand;
			}
			else {
				return recurse(i+1, bestHand);
			}
		}

		private int charIndex(char label) {
			for (int i=0;i<cardLabels.length;i++) {
				if (cardLabels[i] == label) return i;
			}
			throw new IllegalStateException();
		}

		@Override
		public int compareTo(Hand o) {
			int compare = Integer.compare(type, o.type);
			if (compare != 0) return compare;
			for (int i=0;i<5;i++) {
				compare = Integer.compare(charIndex(characteres.get(i)), charIndex(o.characteres.get(i)));
				if (compare != 0) return compare;
			}
			return compare;		
		}
	}
}
