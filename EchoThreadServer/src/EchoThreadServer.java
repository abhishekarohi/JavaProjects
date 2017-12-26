import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public class EchoThreadServer extends Thread

{
	
	Socket socket;
	int threadiD;
	
	public EchoThreadServer(Socket s, int t)
	{
		socket = s;	
		threadiD = t;
		
	}
	
	public void run()
	{
		try 
		{
			BufferedReader input = new BufferedReader (new InputStreamReader(socket.getInputStream()));
			PrintWriter output;
			output = new PrintWriter(socket.getOutputStream(),true);
			while(true)
			{
				String echoString = input.readLine();
				if (echoString.toUpperCase().equals("EXIT"))
				{
					
					break;
				}
					
				else
					output.println("Thread " + threadiD + " Echo:" + echoString);
			}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
