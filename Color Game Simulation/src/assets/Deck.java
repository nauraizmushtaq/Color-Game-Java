package assets;

import java.util.ArrayList;

public class Deck 
{
	private static final int totalSuits = 4;
	private static final int cardsInOneSuit = 13;
	private ArrayList<Card> deck;
	
	public Deck()
	{
		String suit = new String();
		deck = new ArrayList<Card>();
		for (int i = 0; i < totalSuits; ++i)
		{
			if (i == 0)
			{
				suit = "hearts";
			}
			else if (i == 1)
			{
				suit = "spades";
			}
			else if (i == 2)
			{
				suit = "diamonds";
			}
			else 
			{
				suit = "clubs";
			}
			
			for (int j = 0; j < cardsInOneSuit; ++j)
			{
				
				Card card = new Card(suit, j+2);
				deck.add(card);
			}
		}
	}
	
	public ArrayList<Card> getCardArray()
	{
		return deck;
	}
}