package br.com.jobsearchtool.webscrapper.webdomains;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.jobsearchtool.webscrapper.JobApplication;
import br.com.jobsearchtool.webscrapper.LoadSubDomains;
import br.com.jobsearchtool.webscrapper.WebDomain;
import br.com.jobsearchtool.webscrapper.hiringdetails.*;


public class Inhire implements WebDomain{
	final private String apiURL = "https://api.inhire.app/job-posts/public/pages";
	private String getApplicationURL(String domain,String jobID,String title) {
		String url = domain+"/vagas/" +"/";
		String urlSuffix = title.toLowerCase().replaceAll("[^a-zA-Z0-9]", "-");
		return url+urlSuffix;	
	}
	private List<JobApplication> parseRequest(String json,String domain){
        JSONObject obj = new JSONObject(json);
        JSONArray jobsPage = obj.getJSONArray("jobsPage");
        List<JobApplication> result = new ArrayList<JobApplication>();
    	System.out.println(jobsPage.length());
        for(int i = 0; i < jobsPage.length(); i++)
        {
        	JSONObject jobInfosObj = jobsPage.getJSONObject(i);
        	JobApplication application = new JobApplication();
        	application.setApplicationTitle(jobInfosObj.getString("displayName"));
        	application.setApplicationUrl(getApplicationURL(domain,jobInfosObj.getString("jobId"),application.getApplicationTitle()));
        	application.setJobAdress(jobInfosObj.getString("location"));
        	switch(jobInfosObj.getString("workplaceType"))
        	{
        		case "Remote":
        			application.setWorkplace(WorkPlaceType.HOMEOFFICE);
        			break;
        		case "On-site":
        			application.setWorkplace(WorkPlaceType.PRESENCIAL);
        			break;
         		case "Hybrid":
        			application.setWorkplace(WorkPlaceType.HYBRID);
        			break;
        		default:
        			application.setWorkplace(WorkPlaceType.UNKNOWN);
        	}
        	System.out.println(application);
        	result.add(application);
        }
		return result;
	}
	@Override
	public List<JobApplication> softSearch(){
		// TODO Auto-generated method stub
		final List<String> domains = LoadSubDomains.load("/subdomains/inhire.txt");
		for(String domain : domains)
		{
			final String XTenant = domain.split("\\.")[0];
			try{
	            HttpClient client = HttpClient.newHttpClient();
	            HttpRequest request = HttpRequest.newBuilder()
	            								 .uri(URI.create(apiURL))
	            					             .header("X-Tenant", XTenant)
	            					             .GET()
	            								 .build();
	            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	            if(response.statusCode() != 200)
	            	continue;
	            parseRequest(response.body(),domain);
	            System.out.println("Status: " + response.statusCode());
	            System.out.println("Body: " + response.body());
			}catch(MalformedURLException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		return null;
	}

	@Override
	public List<JobApplication> softSearch(LocalTime startDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobApplication> deepSearch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobApplication> deepSearch(LocalTime startDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
