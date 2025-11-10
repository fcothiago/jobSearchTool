package br.com.jobsearchtool.webscrapper;
import br.com.jobsearchtool.webscrapper.JobApplication;
import java.util.List; 
import java.time.LocalTime;
public interface WebDomain {
	List<JobApplication> softSearch();
	List<JobApplication> softSearch(LocalTime startDate);
	List<JobApplication> deepSearch();
	List<JobApplication> deepSearch(LocalTime startDate);
}
