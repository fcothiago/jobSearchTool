package br.com.jobsearchtool.webscrapper.webdomains;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.time.Instant;
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

import br.com.jobsearchtool.webscrapper.JobApplication;
import br.com.jobsearchtool.webscrapper.LoadSubDomains;
import br.com.jobsearchtool.webscrapper.WebDomain;
import br.com.jobsearchtool.webscrapper.hiringdetails.WorkPlaceType;

public class Recrutai implements WebDomain {
	final String apiURLSuffix = "/company/public-jobs/*/*/*/*/*/?_=";
	private String extractApplicationURL(Element anchor,String domain){
		return "https://"+domain+"/"+anchor.attr("href");
	}
	private String extractApplicationAddress(){
		return "";
	}
	private WorkPlaceType extractApplicationWorplace(JSONObject address){
		return WorkPlaceType.PRESENCIAL;
	}
	private List<JobApplication> parseRequest(String json,String domain){
		List<JobApplication> jobs = new ArrayList<JobApplication>();
		JSONObject obj = new JSONObject(json);
		String html = obj.getString("html");
		Document doc = Jsoup.parse(html);
		Elements items = doc.select("a");
		for(Element anchor : items )
		{
			JobApplication job = new JobApplication();
			job.setApplicationUrl(extractApplicationURL(anchor,domain));
			jobs.add(job);
		}
		return jobs;
	}
	@Override
	public List<JobApplication> softSearch() {
		List<JobApplication> jobs = new ArrayList<JobApplication>();
		final List<String> domains = LoadSubDomains.load("/subdomains/recrutai.txt");
		for(String domain : domains)
		{
			try{
				final String apiURL = "https://" + domain + apiURLSuffix + System.currentTimeMillis();
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
	private WorkPlaceType extractWorkPlace(JSONObject obj){
		if(obj.has("jobLocationType") && obj.getString("jobLocationType") == "TELECOMMUTE")
			return WorkPlaceType.HOMEOFFICE;
		if(obj.has("jobLocation"))
			return WorkPlaceType.PRESENCIAL;
		return WorkPlaceType.UNKNOWN;
	}
	private String extractJobAdrees(JSONObject obj){
		if(obj.has("jobLocation"))
		{
			JSONObject address = obj.getJSONObject("jobLocation").getJSONObject("address");
			return  address.getString("streetAddress") + ", "
				  + address.getString("addressLocality") + ", "
				  + address.getString("addressCountry");
		}
		return "";
	}
	public List<JobApplication> deepSearch() {
		List<JobApplication> jobs = softSearch();
		for(JobApplication job : jobs){
	        try {
	    		Document doc = Jsoup.connect(job.getApplicationUrl()).get();
				String json = doc.select("script[type=application/ld+json]").html();
				JSONObject obj = new JSONObject(json);
				job.setApplicationTitle(obj.getString("title"));
				job.setApplicationDescription(obj.getString("description"));
				Instant instant = Instant.parse(obj.getString("datePosted"));
				job.setDate(instant.atZone(ZoneId.systemDefault()).toLocalDate());
				job.setWorkplace(extractApplicationWorplace(obj));
				job.setJobAdress(extractJobAdrees(obj));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public List<JobApplication> deepSearch(LocalTime startDate) {
		// TODO Auto-generated method stub
		return null;
	}

}
