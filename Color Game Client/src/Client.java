import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client 
{
	public static void main(String[] args) 
	{
		String hostName = "abid-laptop";
		int hostPort = 1234;
		Socket clientSocket = null; 
		try 
		{
			clientSocket = new Socket(hostName, hostPort);
			PrintWriter outToUser = new PrintWriter(System.out, true);
			BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter outToServer = new PrintWriter(clientSocket.getOutputStream(), true);
			String inputFromServer = new String();
			while(true)
			{		
				inputFromServer = inFromServer.readLine();
				/* -1 is a flag that means read from user and send data to server */
				if(inputFromServer.equals("0"))		
				{
					outToServer.println(inFromUser.readLine());
				}
				else if(inputFromServer.equals("-1"))
				{
					break;
				}
				else
				{
					outToUser.println(inputFromServer);
				}
			}
		}
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
			try 
			{
				if (clientSocket != null)
					clientSocket.close();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	}
}