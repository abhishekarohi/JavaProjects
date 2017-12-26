import java.net.*;

public class Netwroking {

	public static void main(String[] args) 
	{
		try
		{
//			URI uri = new URI("http://username:password@myserver.com:5000/catalouge/phones?os=iOS#apple");
			URI baseUri = new URI("http://username:password@myserver.com:5000");
			URI uri = new URI("/catalouge/phones?os=iOS#apple");
//			URL url = uri.toURL();
			URL url= baseUri.resolve(uri).toURL();
			
			System.out.println("URL = " + url);
			
//			System.out.println("Scheme = " + uri.getScheme());
//			System.out.println("Scheme-Specific Part = " + uri.getSchemeSpecificPart());
//			System.out.println("Authority = " + uri.getAuthority());
//			System.out.println("User Info = " + uri.getUserInfo());
//			System.out.println("Host = " + uri.getHost());
//			System.out.println("Port = " + uri.getPort());
//			System.out.println("Path = " + uri.getPath());
//			System.out.println("Query = " + uri.getQuery());
//			System.out.println("Fragment = " + uri.getFragment());
			
		}catch (URISyntaxException e)
		{
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
