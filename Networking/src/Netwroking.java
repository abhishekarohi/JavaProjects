import java.io.*;
import java.net.*;

public class Netwroking {

	public static void main(String[] args) 
	{
		try 
			{
				URL url = new URL("http://example.org");
				HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
				urlConnection.setRequestMethod("GET");
				urlConnection.setRequestProperty("User-Agent", "Chrome");
				urlConnection.setReadTimeout(30000);
				
				int respCode = urlConnection.getResponseCode();
				System.out.println("Responce Code " + respCode);
				
				
				if (respCode != 200)
				{
					System.out.println("Error reading the web page");
					return;
				}
				
				BufferedReader inStream = new BufferedReader(
						new InputStreamReader(urlConnection.getInputStream()));
				String inputLine = "";
				
				while(inputLine != null)
				{
					inputLine = inStream.readLine();
					System.out.println(inputLine);					
				}
				inStream.close();		
			
			} 
			catch (MalformedURLException e) 
			{			
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}		

}
