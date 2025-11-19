package br.com.jobsearchtool.webscrapper.webdomains;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.jobsearchtool.webscrapper.JobApplication;
import br.com.jobsearchtool.webscrapper.Utils;
import br.com.jobsearchtool.webscrapper.WebDomain;
import br.com.jobsearchtool.webscrapper.hiringdetails.WorkPlaceType;

public class Solides extends WebDomain {
	final private String apiURL = "https://apigw.solides.com.br/jobs/v3/home/vacancy";
	public Solides(){
		subDomainsResource = "/subdomains/solides.txt";
	}
	private JSONObject makeAPIRequest(String domain,int page){
		final String slug = domain.split("\\.")[0];
		JSONObject obj = null;
		try {
			URI uri = new  URIBuilder(apiURL)
			           	  .addParameter("slug", slug)
			              .addParameter("page", String.valueOf(page))
			              .build();
			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();
			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if(response.statusCode() != 200)
            	return null;
            obj = new JSONObject(response.body());
		} catch (URISyntaxException | IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return obj;
	}
	private String extractJobAdress(JSONObject json){		
		return json.getString("city") + " " + json.getString("state") ;
	}
	private String extractJobURL(JSONObject json){
		String [] fields = json.getString("redirectLink").split("\\.");
		String prefix = fields[0];
		String suffix = fields[1].replaceFirst("vacancies", "vaga");
		return prefix + ".vagas.solides.com.br" + suffix;
	}
	private WorkPlaceType extractJobWorkplace(JSONObject json){
		switch(json.getString("jobType")) 
		{
			case "presencial":
				return WorkPlaceType.PRESENCIAL;
			case "remoto":
				return WorkPlaceType.HOMEOFFICE;
			case "hibrido":
				return WorkPlaceType.HYBRID;
			default:
				return WorkPlaceType.UNKNOWN;
		}
	}
	private List<String> extractKeyWords(JSONObject json){
		final String [] fields = {"benefits","education","occupationAreas","recruitmentContractType"};
		List<String> result = new ArrayList<String>();
		for(String field : fields)
		{
			JSONArray array = json.getJSONArray(field);
			for(int i = 0;i < array.length();i++)
				result.add(array.getJSONObject(i).getString("name"));
		}
		return result;
	}
	private List<JobApplication> parseJsonObject(JSONObject json){
		List<JobApplication> jobs = new ArrayList<JobApplication>();
		JSONArray array = json.getJSONObject("data").getJSONArray("data");
		for(int i = 0;i < array.length();i++){
			JSONObject obj = array.getJSONObject(i);
			JobApplication job = new JobApplication();
			job.setApplicationTitle(obj.getString("title"));
			job.setApplicationDescription(obj.getString("description"));
			job.setApplicationUrl(extractJobURL(obj));
			job.setCompanyName(obj.getString("companyName"));
			job.setWorkplace(extractJobWorkplace(obj));
			job.setApplicationKeyWords(extractKeyWords(obj));
			job.setDate(LocalDate.parse(obj.getString("createdAt")));
			jobs.add(job);
		}
		return jobs;
	}
	public List<JobApplication> softSearch(String domain ) {
		JSONObject obj = makeAPIRequest(domain,1);
		List<JobApplication> jobs = parseJsonObject(obj);
		return jobs;
	}
	@Override
	protected List<JSONObject> startSearch(String domain){
		List<JSONObject> result = new ArrayList<JSONObject>();
		int lastPage = 1;
		for(int page = 1;page <= lastPage;page++)
		{
			if(!running)
				break;
			try {
				JSONObject obj = makeAPIRequest(domain,page);
				lastPage = obj.getJSONObject("data").getInt("totalPages");
				List<JobApplication> jobs = parseJsonObject(obj);
				for(JobApplication job : jobs)
				{
					System.out.println("Got new JobApplication " + job.getApplicationUrl());
					result.add(job.toJSONObject());
				}
				Thread.sleep(delayBetweenJobApplication);
			}catch(InterruptedException | JSONException e){
				
			}
		}
		return result;
	}
}
