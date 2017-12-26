import java.net.*;
import java.io.*;
import java.util.*;

public class EchoClient {

	public static void main(String[] args) 
	{
		try {
			Socket socket = new Socket("localhost",5000);
			
			BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter output = new PrintWriter(socket.getOutputStream(),true);
			
			Scanner in = new Scanner(System.in);
			
			while(true)
			{
				System.out.println("Enter Command");
				String command = in.nextLine();
				output.println(command);
				if (command.toUpperCase().equals("EXIT"))
				{
					socket.close();
					in.close();
					break;
				}
				else
				{
					System.out.println(input.readLine());
				}
			}
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
