package br.com.jobsearchtool.webscrapper.webdomains;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import br.com.jobsearchtool.webscrapper.JobApplication;
import br.com.jobsearchtool.webscrapper.WebDomain;
import br.com.jobsearchtool.webscrapper.hiringdetails.WorkPlaceType;

public class Quickin extends WebDomain {
	final String jobDescriptionCSSPath = "html body div#__nuxt div#__layout div div.career-main section.section-sm div.container.container-md div.mb-4"; 
	public Quickin(){
		subDomainsResource = "/pages/quickin.txt";
	}
	public String extracJobTitle(Element row){
		return row.selectFirst("th a").text();
	}
	public String extracJobUrl(Element row){
		return row.selectFirst("th a").attr("href");
	}
	public String extracJobAddress(Element row){
		return row.selectFirst("td span:nth-child(1)").text();
	}
	public WorkPlaceType extracJobWorkplace(Element row){
		switch(row.selectFirst("td span:nth-child(2)").text())
		{
			case "On-site":
				return WorkPlaceType.PRESENCIAL;
			case "Hybrid":
				return WorkPlaceType.HYBRID;
			case "Remote":
				return WorkPlaceType.HOMEOFFICE;
			default:
				return WorkPlaceType.UNKNOWN;
		}
	}
	private Thread startJobInfosThread(JobApplication job) {
		Thread t = new Thread(() -> {
			try {
				Document doc = Jsoup.connect(job.getApplicationUrl()).get();
				job.setApplicationDescription(doc.select(jobDescriptionCSSPath).text());
			}catch (IOException e){
				System.out.println("Failed to extractJobApplication from " + job.getApplicationUrl());
			}
		});
		t.setDaemon(false);
		t.start();
		return t;
	}
	public void extractJobInfos(List<JobApplication> jobs){
		for(int i = 0;i < jobs.size();i += numOfThreads)
		{
			if(!running)
				break;
			try {
				List<Thread>  threads = new ArrayList<Thread>();
				List<JobApplication> sublist = (i+numOfThreads < jobs.size()) ? jobs.subList(i, i+numOfThreads ) : jobs.subList(i, jobs.size() )   ;
				for(int j = 0; j < sublist.size();j++)
					threads.add( startJobInfosThread(sublist.get(j)) );
				for(Thread t : threads)
					t.join();
				Thread.sleep(delayBetweenJobApplication);
			}catch (InterruptedException e) {

			}
		}
	}
	public void updateCompanyName(JobApplication job,String domain){
		String [] fields = domain.split("/");
		String company = fields[fields.length - 2];
		job.setCompanyName(company);
	}
	public List<JobApplication> extractJobApplications(Document doc){
		List<JobApplication> jobs = new ArrayList<JobApplication>();
		Elements talbeRows = doc.select("div.career-main tr");
		for(Element row : talbeRows)
		{
			JobApplication job = new JobApplication();
			job.setApplicationTitle(extracJobTitle(row));
			job.setApplicationUrl(extracJobUrl(row));
			job.setJobAdress(extracJobAddress(row));
			job.setWorkplace(extracJobWorkplace(row));
			jobs.add(job);
		}
		return jobs;
	}
	@Override
	protected List<JSONObject> startSearch(String domain){
		List<JobApplication> jobs = new ArrayList<JobApplication>();
		List<JSONObject> result = new ArrayList<JSONObject>();
		try {
			Document doc = Jsoup.connect(domain).get();
			for(JobApplication job : extractJobApplications(doc))
			{
				updateCompanyName(job,domain);
				jobs.add(job);
			}
			extractJobInfos(jobs);
			for(JobApplication job : jobs)
				result.add(job.toJSONObject());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
