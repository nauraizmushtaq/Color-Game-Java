package game;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;

import assets.Deck;
import assets.Pile;

public class Game extends Thread
{
	private ArrayList<Player> gamePlayers;
	private int numberOfDecks;
	private boolean isOver;
	private Pile pileToPlay = new Pile();		//dump
	private Pile pileToDistribute = new Pile();
	private String suitColor = new String();		//color of the game
	private Object gameStatus = new Object();
	
	
	public Game(ArrayList<Socket> _players)
	{
		isOver = false;
		gamePlayers = new ArrayList<Player>();
		
		/* Creating players and giving them socket to talk to client */
		for (int i = 0, size = _players.size(); i < size; ++i)
		{
			gamePlayers.add(new Player(pileToPlay, gameStatus, _players.remove(0)));
		}
		
		Properties prop = new Properties();
		InputStream input = null;
		
		try
		{
			input = new FileInputStream("start.properties");
			prop.load(input);
			
			numberOfDecks = Integer.parseInt(prop.getProperty("NumberOfDecks", "2"));
			if(numberOfDecks < 1 || numberOfDecks > 4)
			{
				System.out.println("Decks out of range.");	
				System.out.println("Game Ended");
				isOver = true;
				return;
			}
		}
		catch (IOException ex) 
		{
			ex.printStackTrace();
		}
		System.out.println("Game created");	
	}
	
	public boolean isOver()
	{
		return isOver;
	}
	
	public void run() 
	{
		
		/* Maintaining log file */
		PrintWriter fout = null;
		try 
		{
			fout = new PrintWriter(new BufferedWriter(new FileWriter("score.txt")));
			fout.println("Number Of Players = " + gamePlayers.size());
			fout.println("Number Of Decks = " + numberOfDecks);
			
			for (int i = 0; i < gamePlayers.size(); ++i)
			{
				fout.println("PlayerName" + Integer.valueOf(i+1) + ": " + gamePlayers.get(i).getPlayerName());
			}
		}
		catch (IOException e1) 
		{
			e1.getMessage();
		}
		
		/* Introducing players to each other */
		PrintWriter playerOut = null;
		for ( int i = 0; i < gamePlayers.size(); ++i)
		{
			playerOut = gamePlayers.get(i).getPlayerOutStream();
			playerOut.println();
			playerOut.println("Number Of Decks = " + numberOfDecks);
			playerOut.println("Number Of Players In Game = " + gamePlayers.size());
			playerOut.println("Name Of Players:");
			for (int j = 0; j < gamePlayers.size(); ++j)
			{
				playerOut.println("Player" + Integer.valueOf(j+1) + ": " + gamePlayers.get(j).getPlayerName());
			}
		}
		
		/* Combining decks in pile and shuffling */
		for (int i = 0; i < numberOfDecks; ++i)
		{
			pileToDistribute.addCardArray(new Deck().getCardArray());
		}
		pileToDistribute.shuffle();
		
		/* Distributing cards until jack of any suit is found */
		boolean jackFound = false;
		int jackPlayer = -1;
		int totalCards = (52 * numberOfDecks);
		for (int i = 0; i < totalCards && !jackFound; ++i)
		{
			jackFound = gamePlayers.get(i%gamePlayers.size()).addCardandCheckJack(pileToDistribute.getCard(0));
			pileToDistribute.removeCard(0);
			if(jackFound)
			{
				jackPlayer = i % gamePlayers.size();
			}
		}
		
		/* jack player selecting suit */
		suitColor = gamePlayers.get(jackPlayer).getSuitColor();
		
		/* log file */
		fout.println(gamePlayers.get(jackPlayer).getPlayerName() + " got jack.");
		fout.println("Color selected by " + gamePlayers.get(jackPlayer).getPlayerName() + " is " + suitColor);
		
		/* telling suit color to all players */
		for(int i = 0; i < gamePlayers.size(); ++i)
		{
			gamePlayers.get(i).setSuitColor(suitColor, gamePlayers.get(jackPlayer).getPlayerName());
		}
		
		/* Pile to distribute reseted */
		for (int i = 0, size = gamePlayers.size(); i < size; ++i)
		{
			pileToDistribute.addCardArray(gamePlayers.get(i).getCardArray());
			gamePlayers.get(i).removeAllCards();
		}
		
		/* Pile distributed */
		for (int i = 0, size = gamePlayers.size(); pileToDistribute.getCardArray().size() != 0; ++i)
		{
			gamePlayers.get(i%size).addCard(pileToDistribute.getCard(0));
			pileToDistribute.removeCard(0);
		}
		
//		//logfile
//		ArrayList<Card> cards = null;
//		int size;
// 		for (int i = 0; i<numberOfPlayers; ++i)
//		{
// 			cards = playerArr[i].getCardArray();
// 			size = cards.size();
// 			fout.println("Cards of player " + playerArr[i].getName());
// 			for (int j = 0; j < size; ++j)
// 			{
// 				fout.println(cards.get(j));
// 			}
//		}
		
		/* threads Started */
		for (int i = 0, numberOfPlayers = gamePlayers.size(); i < numberOfPlayers; ++i)
		{
			synchronized (gameStatus) 
			{
				gamePlayers.get(i).start();	
				try 
				{
					gameStatus.wait();
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		/* Turns. first turn goes to jack player */
		for (int i = 0; i < (gamePlayers.size()*2); ++i)
		{			
			gamePlayers.get((jackPlayer + i)%gamePlayers.size()).resumePlayer();
			synchronized(gameStatus)
			{
				try 
				{
					gameStatus.wait();
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		int max = 0, index = 0;
		for (int j = 0; j < gamePlayers.size(); ++j)
		{
			if(max < gamePlayers.get(j).getTotalValue())
			{
				max = gamePlayers.get(j).getTotalValue();
				index = j;
			}
		}
		
		for(int j = 0;j < gamePlayers.size(); ++j)
			if(j != index)
				gamePlayers.get(j).gameWonBy(gamePlayers.get(index).getPlayerName(), max);
			else
				gamePlayers.get(index).wonGame();
		
		/* log file */
		fout.println("\nPlayer: " + gamePlayers.get(index).getPlayerName() + " Won by card value = " + max);
		fout.flush();
		fout.close();
		
		for (int i = 0; i < gamePlayers.size(); ++i)
		{			
			
			gamePlayers.get((jackPlayer + i)%gamePlayers.size()).resumePlayer();
			synchronized(gameStatus)
			{
				try 
				{
					gameStatus.wait();
				}
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}
			}
		}
		
		isOver = true;
	}
}