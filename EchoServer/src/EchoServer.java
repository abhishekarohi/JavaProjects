import java.io.*;
import java.net.*;

public class EchoServer {

	public static void main(String[] args) 
	{
		try {
			ServerSocket serverSocket = new ServerSocket(5000);
			Socket socket = serverSocket.accept();
			
			BufferedReader input = new BufferedReader (new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
			
			while(true)
			{
				String echoString = input.readLine();
				if (echoString.toUpperCase().equals("EXIT"))
				{
					serverSocket.close();
					break;
				}
					
				else
					output.println("Server Echo: " + echoString);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		

	}

}
