package br.com.jobsearchtool.webscrapper;

import java.util.List;
import org.json.JSONObject;

public class WebDomain implements Runnable{
	protected String outputPath = "";
	protected JSONObject jobsDB;
	protected int delayBetweenSubdomains = 1000;
	protected int delayBetweenJobApplication = 1000;
	protected int numOfThreads = 4;
	protected volatile boolean running = true; 
	protected String subDomainsResource;
	public void sendFinishSignal(){
		running = false;
	}
	public void exportJSON(){
		if(outputPath == "")
		{
			outputPath = System.getProperty("user.home") + "/"  + getClass().getSimpleName() + ".json";
			System.out.println("No output path provided. Using " + outputPath);
		}
		Utils.saveJSON(jobsDB, outputPath);
	}
	protected List<JSONObject> startSearch(String domain){
		return null;
	}
	@Override
	public void run() {
		if(outputPath == "")
		{
			outputPath = System.getProperty("user.home") + "/"  + getClass().getSimpleName() + ".json";
			System.out.println("No output path provided. Using " + outputPath);
		}
		jobsDB = Utils.loadJSON(outputPath);
		running = true;
		final List<String> subdomains = Utils.loadSubdomains(subDomainsResource);
		for(String domain : subdomains)
		{
			if(!running)
				break;
			if(jobsDB.has(domain))
				continue;
			System.out.println("Extracting Jobs from " + domain);
			List<JSONObject> jsonList = startSearch(domain);
			jobsDB.put(domain,jsonList);
		}
		System.out.println(getClass().getName()+" Thread finished");
	}

}
