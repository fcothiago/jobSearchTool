package br.com.jobsearchtool.webscrapper;
import br.com.jobsearchtool.webscrapper.JobApplication;
import java.util.List; 
import java.time.LocalTime;
public interface WebDomain {
	List<JobApplication> searchForApplications();
	List<JobApplication> searchForApplications(LocalTime startDate);
}
