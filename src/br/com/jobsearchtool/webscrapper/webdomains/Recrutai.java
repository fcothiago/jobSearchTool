package br.com.jobsearchtool.webscrapper.webdomains;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.jobsearchtool.webscrapper.JobApplication;
import br.com.jobsearchtool.webscrapper.LoadSubDomains;
import br.com.jobsearchtool.webscrapper.WebDomain;
import br.com.jobsearchtool.webscrapper.hiringdetails.WorkPlaceType;

public class Recrutai implements WebDomain {
	final String apiURLSuffix = "/public-locations/vagas/*";
	private String extractApplicationURL(JSONObject profile,String domain){
		return domain+"/jobs/"+profile.getString("id");
	}
	private String extractApplicationAddress(JSONObject address){
		if(!address.getBoolean("isRemote"))
			return "";
		return address.getString("city")+", "+address.getString("state")+", "+address.getString("country_name");
	}
	private WorkPlaceType extractApplicationWorplace(JSONObject address){
		if(!address.getBoolean("isRemote"))
			return WorkPlaceType.HOMEOFFICE;
		return WorkPlaceType.PRESENCIAL;
	}
	private List<JobApplication> parseRequest(String json,String domain){
		List<JobApplication> jobs = new ArrayList<JobApplication>();
		JSONObject obj = new JSONObject(json);
		JSONArray profilesList = obj.getJSONArray("profilesList");
		JSONArray addrList = obj.getJSONArray("addrList");
		for(int i = 1;i < profilesList.length();i++)
		{
			JSONObject profile = profilesList.getJSONObject(i);
			JSONObject address = addrList.getJSONObject(i);
			JobApplication job = new JobApplication();
			job.setApplicationTitle(profile.getString("title"));
			job.setApplicationUrl(extractApplicationURL(profile, domain));
			job.setJobAdress(extractApplicationAddress(address));
			job.setWorkplace(extractApplicationWorplace(profile));
			break;
		}
		return jobs;
	}
	@Override
	public List<JobApplication> softSearch() {
		List<JobApplication> jobs = new ArrayList<JobApplication>();
		final List<String> domains = LoadSubDomains.load("/subdomains/recrutai.txt");
		for(String domain : domains)
		{
			final String apiURL = domain + apiURLSuffix;
			try{
	            HttpClient client = HttpClient.newHttpClient();
	            HttpRequest request = HttpRequest.newBuilder()
	            								 .uri(URI.create(apiURL))
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobApplication> deepSearch(LocalTime startDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
