package game;

import java.net.Socket;
import java.util.ArrayList;

public class Lobby extends Thread
{
	ArrayList<Socket> players;
	ArrayList<Game> games;
	
	public Lobby()
	{
		players = new ArrayList<Socket>();
		games = new ArrayList<Game>();
	}
	
	public void addPlayer(Socket _connectSocket)
	{
		players.add(_connectSocket);
	}
	
	
	/* Creates games for all possible players. Min Players = 2, Max Players = 4 */
	public void createGames()
	{
		ArrayList<Socket> gamePlayers = new ArrayList<Socket>();
		while (players.size() > 1)
		{
			while (!players.isEmpty() && gamePlayers.size() < 4)
			{
				gamePlayers.add(players.remove(0));
			}
			Game game = new Game(gamePlayers);
			game.start();
			games.add(game);
		} 
	}
	
	/* Removes Games that are Over */
	public void removePlayedGames()
	{
		for(int i = 0; i < games.size(); ++i)
		{
			if (games.get(i).isOver())
			{
				games.remove(i);
				--i;
				System.out.println("Game Removed");
			}
		}
	}
	
	public void run()
	{
		try 
		{
			while (true)
			{
				/* sleep for 30 seconds */
				Thread.sleep(30000);
				createGames();
				removePlayedGames();
			}
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}		
	}
}