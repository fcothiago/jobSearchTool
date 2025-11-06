package br.com.jobsearchtool.webscrapper;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.HttpURLConnection;
public class HTTPDownload {
	private static int connectTimeout = 5000;
	private static int readTimeout = 5000;
	public static String getRequest(String address) {
        String data = "";
		try{
			URL url = new URL(address);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(connectTimeout);
			conn.setReadTimeout(readTimeout);
            int status = conn.getResponseCode();
            if(status != HttpURLConnection.HTTP_OK)
            	throw new RuntimeException("HTTP request failed with code " + status);
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            byte [] buffer = new byte[1024];
            int bytesRead = 0;
            do{
            	bytesRead = in.read(buffer);
            	data += ( new String(buffer, java.nio.charset.StandardCharsets.UTF_8) );
            }while(bytesRead != -1);
        	conn.disconnect();
		}catch (IOException e) {
            System.out.println("HTTP request  " + e.getMessage());
        }finally{
        }
		return data;
	}
}
