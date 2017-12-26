import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class EchoServerDriver {

	public static void main(String[] args) 
	{
	
		try 
		{
			ServerSocket serverSocket = new ServerSocket(5000);
			int threadNumber = 1;
			while(true)
			{
				Socket socket = serverSocket.accept();
				EchoThreadServer threadServer = new EchoThreadServer(socket, threadNumber);
				threadNumber += 1;
				threadServer.start();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
	}

}
