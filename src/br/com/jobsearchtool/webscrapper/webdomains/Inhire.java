package br.com.jobsearchtool.webscrapper.webdomains;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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
		String url = domain+"/vagas/"+jobID+"/";
		String urlSuffix = title.toLowerCase();
		for(int i = 0;i < urlSuffix.length();i++)
		{
			char c = urlSuffix.charAt(i);
			if( ('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') )
				url += c;
			else if(c == ' ')
				url += '-';
			else if(c == '|')
				url += "or";
		}
		return url;	
	}
	private List<JobApplication> parseRequest(String json,String domain){
        JSONObject obj = new JSONObject(json);
        JSONArray jobsPage = obj.getJSONArray("jobsPage");
        List<JobApplication> result = new ArrayList<JobApplication>();
        for(int i = 0; i < jobsPage.length(); i++)
        {
        	JSONObject jobInfosObj = jobsPage.getJSONObject(i);
        	JobApplication application = new JobApplication();
        	application.setCompanyName(domain.split("\\.")[0]);
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
        	result.add(application);
        }
		return result;
	}
	@Override
	public List<JobApplication> softSearch(){
		List<JobApplication> jobs = new ArrayList<JobApplication>();
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
	            for(JobApplication job : parseRequest(response.body(),domain))
	            	jobs.add(job);           
			}catch(IOException | InterruptedException e){
				e.printStackTrace();
			}
		}
		return jobs;
	}

	@Override
	public List<JobApplication> softSearch(LocalTime startDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobApplication> deepSearch() {
		List<JobApplication> jobs = softSearch();
		for(JobApplication job : jobs)
		{
			try 
			{
				final String XTenant = job.getApplicationUrl().split("\\.")[0];
				final String[] fields = job.getApplicationUrl().split("/");
				final String Id = fields[fields.length - 2];
				HttpClient client = HttpClient.newHttpClient();
				HttpRequest request = HttpRequest.newBuilder()
            								 .uri(URI.create(apiURL+"/"+Id))
            					             .header("X-Tenant", XTenant)
            					             .GET()
            								 .build();
				HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	            if(response.statusCode() != 200)
	            	continue;
	            JSONObject obj = new JSONObject(response.body());
	            job.setApplicationDescription(obj.getString("about"));
	            job.setDate(LocalDate.ofInstant(Instant.parse(obj.getString("updatedAt")),ZoneId.systemDefault() ));
			}catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
		return jobs;
	}

	@Override
	public List<JobApplication> deepSearch(LocalTime startDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
