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
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.apache.http.client.utils.URIBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import br.com.jobsearchtool.webscrapper.JobApplication;
import br.com.jobsearchtool.webscrapper.LoadSubDomains;
import br.com.jobsearchtool.webscrapper.WebDomain;
import br.com.jobsearchtool.webscrapper.hiringdetails.*;

public class Solides implements WebDomain {
	final private String apiURL = "https://apigw.solides.com.br/jobs/v3/home/vacancy";
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
	private void extractJobWorkplace(JobApplication job){
		
	}
	private List<JobApplication> parseJsonObject(JSONObject json){
		List<JobApplication> jobs = new ArrayList<JobApplication>();
		JSONArray array = json.getJSONObject("data").getJSONArray("data");
		for(int i = 0;i < array.length();i++){
			JSONObject obj = array.getJSONObject(i);
			JobApplication job = new JobApplication();
			//presencial hibrido remoto
			job.setApplicationTitle(obj.getString("title"));
			job.setApplicationDescription(obj.getString("description"));
			job.setApplicationUrl(extractJobURL(obj));
			job.setCompanyName(obj.getString("companyName"));
			System.out.println(job);
		}
		return jobs;
	}
	public List<JobApplication> softSearch() {
		List<JobApplication> result = new ArrayList<JobApplication>();
		final List<String> domains = LoadSubDomains.load("/subdomains/solides.txt");
		for(String domain : domains){
			JSONObject obj = makeAPIRequest(domain,1);
			List<JobApplication> jobs = parseJsonObject(obj);
			break;
		}
		return result;
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
