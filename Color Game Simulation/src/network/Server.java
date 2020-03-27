package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import game.Lobby;

public class Server 
{
	public static final int SERVER_PORT = 1234;
	public static void main(String[] args)
	{
		ServerSocket welcomeSocket = null; 
		try 
		{
			welcomeSocket = new ServerSocket(SERVER_PORT);
			Lobby lobby = new Lobby();
			lobby.start();
			
			while(true)
			{
				Socket connectSocket = welcomeSocket.accept();
				System.out.println("Client arrived.");
				lobby.addPlayer(connectSocket);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				welcomeSocket.close();
			} catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}
