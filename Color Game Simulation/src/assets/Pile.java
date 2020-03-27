package assets;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Pile 
{
	//attributes
	private ArrayList<Card> pile;
	
	//constructor
	public Pile()
	{
		pile = new ArrayList<Card>();
	}
	
	//functions
	public void addCard(Card card)
	{
		pile.add(card);
	}
	
	public void addCardArray(ArrayList<Card> arr)
	{
		pile.addAll(arr);
	}
	
	public Card getCard(int index)
	{
		return pile.get(index);
	}
	
	public ArrayList<Card> getCardArray()		//return card array and removes the array
	{
		return pile;
	}
	
	public void removeCard(int index)
	{
		pile.remove(index);
	}
	
	public void removeAllCards()
	{
		pile.removeAll(pile);
	}
	
	public Card getCard(String suit, int value)throws Exception
	{
		Card tempCard;
		Iterator<Card> cardIterator = pile.iterator();
		while (cardIterator.hasNext())
		{
			tempCard = cardIterator.next();
			if (tempCard.getValue() == value && tempCard.getSuit().equals(suit))
			{
				return tempCard;
			}
		}
		throw new Exception("Card not found.");
	}
	
	public boolean isEmpty()
	{
		return pile.isEmpty();
	}
	
	public int size()
	{
		return pile.size();
	}
	
	public void displayCards(PrintWriter out)
	{
		Card tempCard;
		Iterator<Card> cardIterator = pile.iterator();
		for (int i = 0; cardIterator.hasNext(); ++i)
		{
			tempCard = cardIterator.next();
			out.print("Card " + Integer.valueOf(i+1) + ": " + "Name: " + tempCard.getName() +
					" suit: " + tempCard.getSuit() + " Value: " + tempCard.getValue());
			if (tempCard.isPic())
			{
				out.println("   Picture Card");
			}
			else
			{
				out.println("   Number Card");
			}
		}
	}	
	
	public void displayCards()
	{
		Card tempCard;
		Iterator<Card> cardIterator = pile.iterator();
		for (int i = 0; cardIterator.hasNext(); ++i)
		{
			tempCard = cardIterator.next();
			System.out.print("Card " + Integer.valueOf(i+1) + ": " + "Name: " + tempCard.getName() +
					" suit: " + tempCard.getSuit() + " Value: " + tempCard.getValue());
			if (tempCard.isPic())
			{
				System.out.println("   Picture Card");
			}
			else
			{
				System.out.println("   Number Card");
			}
		}
	}	
	
	public void shuffle()
	{
		Collections.shuffle(pile);
	}
	
	public void sortBySuitThenType()
	{
		Collections.sort(pile);
	}
	
	public Card replace(Card c, int index)
	{
		Card temp =	pile.get(index);
		pile.remove(index);
		pile.add(index, c);
		return temp;
	}
}