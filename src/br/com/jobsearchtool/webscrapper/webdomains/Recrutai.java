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
import java.lang.Thread;

import br.com.jobsearchtool.webscrapper.JobApplication;
import br.com.jobsearchtool.webscrapper.Utils;
import br.com.jobsearchtool.webscrapper.hiringdetails.WorkPlaceType;

public class Recrutai implements Runnable {
	final private String apiURLSuffix = "/company/public-jobs/*/*/*/*/*/?_=";
	private String outputPath = "";
	private JSONObject jobsDB;
	private int delayBetweenSubdomains = 1000;
	private int delayBetweenJobApplication = 500;
	private int numOfThreads = 4;
	private String extractApplicationURL(Element anchor,String domain){
		return "https://"+domain+"/"+anchor.attr("href");
	}
	private WorkPlaceType extractApplicationWorkplace(JSONObject address){
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
	public List<JobApplication> extractJobApplications(String domain) {
		List<JobApplication> jobs = new ArrayList<JobApplication>();
		try{
			final String apiURL = "https://" + domain + apiURLSuffix + System.currentTimeMillis();
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(apiURL))
					.GET()
					.build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
			if(response.statusCode() != 200)
				return jobs;
			for(JobApplication job : parseRequest(response.body(),domain))
				jobs.add(job); 
		}catch(IOException | InterruptedException e){
			e.printStackTrace();
		}
		return jobs;
	}
	public Thread startJobInfosThread(JobApplication job) {
		Thread t = new Thread(() -> {
			try {
				Document doc = Jsoup.connect(job.getApplicationUrl()).get();
				String json = doc.select("script[type=application/ld+json]").html();
				JSONObject obj = new JSONObject(json);
				job.setApplicationTitle(obj.getString("title"));
				job.setApplicationDescription(obj.getString("description"));
				Instant instant = Instant.parse(obj.getString("datePosted"));
				job.setDate(instant.atZone(ZoneId.systemDefault()).toLocalDate());
				job.setWorkplace(extractApplicationWorkplace(obj));
				job.setJobAdress(extractJobAdrees(obj));
				System.out.println("Got new JobApplication " + job.getApplicationUrl());
			}catch (IOException e){
				System.out.println("Failed to extractJobApplication from " + job.getApplicationUrl());
				e.printStackTrace();
			}
		});
		t.start();
		return t;
	}
	public void extractJobInfos(List<JobApplication> jobs) {
		for(int i = 0;i < jobs.size();i += numOfThreads)
		{
			try {
				List<Thread> threads = new ArrayList<Thread>();
				List<JobApplication> sublist = jobs.subList(i,i+numOfThreads);
				for(int j = 0; j < sublist.size();j++)
					threads.add( startJobInfosThread(sublist.get(j)) );
				for(Thread t : threads)
					t.join();
				Thread.sleep(delayBetweenJobApplication);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void run(){
		if(outputPath == "")
		{
			outputPath = System.getProperty("user.home") + "/recrutai.json";
			System.out.println("No output path provided. Using " + outputPath);
		}
		final List<String> subdomains = Utils.loadSubdomains("/subdomains/recrutai.txt");
		JSONObject jobsDB = Utils.loadJSON(outputPath);
		for(String domain : subdomains)
		{
			System.out.println("Extracting Jobs from " + domain);
			try{
				if(jobsDB.has("domain"))
					continue;
				List<JobApplication> jobs = extractJobApplications(domain);
				extractJobInfos(jobs);
	            Thread.sleep(delayBetweenSubdomains);
			}catch (InterruptedException e) {
				System.out.println("Recrutai Thread finished");
			}
			break;
		}
		Utils.saveJSON(jobsDB, outputPath);
	}
}
