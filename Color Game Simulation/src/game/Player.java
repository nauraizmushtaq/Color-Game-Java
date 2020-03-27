package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import assets.Card;
import assets.Pile;

public class Player extends Thread  
{
	private String name;
	private Pile handCards;
	private Pile pileToPlay;		//pile where every player is throwing cards
	private String suitColor;
	private Object gameStatus;
	private Socket clientPlayer;
	private PrintWriter playerOut;
	private BufferedReader playerIn;
	
	public Player(Pile pileToPlay, Object gameStatus, Socket _clientPlayer)
	{
		handCards = new Pile();
		suitColor = new String();
		this.pileToPlay = pileToPlay;
		this.gameStatus = gameStatus;
		this.clientPlayer = _clientPlayer;
		
		try 
		{
			playerOut = new PrintWriter(clientPlayer.getOutputStream(), false);			//auto flush is off
			playerIn = new BufferedReader(new InputStreamReader(clientPlayer.getInputStream()));
			
			playerOut.print("Enter Your Name: ");
			getInputFromPlayer();
			name = playerIn.readLine();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String getPlayerName()
	{
		return name;
	}
	
	public boolean addCardandCheckJack(Card card)		//Returns true if card added is jack of any suit
	{
		handCards.addCard(card);
		if (card.getName().equals("jack"))
		{
			return true;
		}
		return false;
	}
	
	public void addCard(Card card)
	{
		handCards.addCard(card);
	}
	
	public ArrayList<Card> getCardArray()
	{
		return handCards.getCardArray();
	}
	
	public void removeAllCards()
	{
		handCards.removeAllCards();
	}
	
	public void display()
	{
		handCards.displayCards(playerOut);
		playerOut.flush();
	}
	
	public void setSuitColor(String color, String jackPerson)
	{
		suitColor = color;
		playerOut.println("\n" + jackPerson + " got jack card and selected " + color + " as suit of the game.");
	}
	
	public String getSuitColor()
	{
		suitColor = "hearts";		//default
		
		try
		{
			int choice;
			playerOut.println();
			playerOut.println("You got jack. PLease select suit for the game.");
			playerOut.println("1. Hearts");
			playerOut.println("2. Spades");
			playerOut.println("3. Diamond");
			playerOut.println("4. Clubs");
			playerOut.print("Select suit: ");
			getInputFromPlayer();
			
			choice = Integer.valueOf(playerIn.readLine());
			while(choice < 1 || choice > 4)
			{
				playerOut.println("Wrong selection. Select again.");
				playerOut.println("1. Hearts");
				playerOut.println("2. Spades");
				playerOut.println("3. Diamonds");
				playerOut.println("4. Clubs");
				playerOut.print("Select suit: ");
				getInputFromPlayer();
				choice = Integer.valueOf(playerIn.readLine());
			}
			
			switch(choice)
			{
			case 1:
				suitColor = "hearts";
				break;
			case 2:
				suitColor = "spades";
				break;
			case 3:
				suitColor = "diamonds";
				break;
			case 4 :
				suitColor = "clubs";
			}
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
		
		return suitColor;
	}
	
	public int getTotalValue()
	{
		int colorSuit = 0;
		int nonColorSuit = 0;
		for(int i = 0; i < handCards.size(); ++i)
		{
			if(handCards.getCard(i).getSuit().equals(suitColor))
			{
				colorSuit = colorSuit + handCards.getCard(i).getValue();
			}
			else
			{
				nonColorSuit = nonColorSuit + handCards.getCard(i).getValue();				
			}
		}
		return nonColorSuit + (colorSuit * 2);
	}
	
	public void suspendPlayer()
	{
		try 
		{
			wait();
		}
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}
	
	public synchronized void resumePlayer() 
	{
		notify();
	}
	
	private void resumeGameThread()
	{
		synchronized(gameStatus)
		{
			gameStatus.notify(); 
		}
	}
	
	private int selectCard(Pile pile) throws IOException, SocketException
	{
		int cardNum = 0;
		pile.displayCards(playerOut);
		playerOut.print("Select Card: ");
		getInputFromPlayer();
		try
		{
			cardNum = Integer.valueOf(playerIn.readLine()) - 1;
		}
		catch(NumberFormatException ex)
		{
			cardNum = -1;
		}
		
		while (cardNum < 0 || cardNum >= pile.size() )
		{
			playerOut.print("Out of Range. Enter again:");
			getInputFromPlayer();
			try
			{
				cardNum = Integer.valueOf(playerIn.readLine()) - 1;
			}
			catch(NumberFormatException ex)
			{
				cardNum = -1;
			}
		}
		return cardNum;
	}
	
	public void gameWonBy(String name, int score)
	{
		playerOut.println("\n" + name + " Won by card value = " + score);
		playerOut.flush();
	}
	
	public void wonGame()
	{
		playerOut.println("\nYou won the game by value: " + this.getTotalValue());
		playerOut.flush();
	}
	
	/* flag which tells the client to get input form user and send input */
	private void getInputFromPlayer()
	{
		playerOut.flush();
		playerOut.println();
		playerOut.println("0");
		playerOut.flush();
	}
	
	/* flag which tells the client to close */
	public void closeClient()
	{
		playerOut.flush();
		playerOut.println();
		playerOut.println("-1");
		playerOut.flush();
	}
	
	public PrintWriter getPlayerOutStream()
	{
		return playerOut;
	}
	
	public synchronized void run()
	{
		handCards.sortBySuitThenType();
		
		synchronized (gameStatus)
		{
			gameStatus.notify();
		}	
		suspendPlayer();
		
		//Dumping 5 cards
		try
		{
			int cardNum;
			playerOut.println("\nSelect any five cards to throw:");
			for (int i = 0; i < 5; ++i)
			{
				try {
					cardNum = selectCard(handCards);
				} catch (SocketException ex) {
					return;
				}
						
				pileToPlay.addCard(handCards.getCard(cardNum));
				handCards.removeCard(cardNum);
				playerOut.println("Card thrown successfully.");
				playerOut.println();
				playerOut.flush();
			}
			
			resumeGameThread();
			suspendPlayer();
			
			//Replacing 2 cards from pile
			playerOut.println("\nPick two cards from pile:");
			int card;
			
			for(int i = 0; i < 2; ++i)
			{
				playerOut.println("Open Pile: ");
				card = selectCard(pileToPlay);	
				handCards.addCard(pileToPlay.getCard(card));
				pileToPlay.removeCard(card);
				playerOut.println("Successfully picked.");
				playerOut.flush();
			}
			
			playerOut.println("\nPick two cards from hand cards: ");
			for(int i = 0; i < 2; ++i)
			{
				playerOut.println("HandCards: ");
				card = selectCard(handCards);	
				pileToPlay.addCard(handCards.getCard(card));
				handCards.removeCard(card);
				playerOut.println("Successfully replaced.");
				playerOut.flush();
			}
			
			resumeGameThread();
			suspendPlayer();
			
			try 
			{
				closeClient();
				playerOut.close();
				playerIn.close();
				clientPlayer.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
			resumeGameThread();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
}