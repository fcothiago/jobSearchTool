package br.com.jobsearchtool.webscrapper;
import br.com.jobsearchtool.webscrapper.hiringdetails.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class JobApplication {
	@Override
	public String toString() {
		return "JobApplication [applicationUrl=" + applicationUrl + ", applicationTitle=" + applicationTitle
				+ ", applicationDescription=" + applicationDescription + ", jobAdress=" + jobAdress + ", companyName="
				+ companyName + ", countryCode=" + countryCode + ", paymentPerMonth=" + paymentPerMonth
				+ ", applicationKeyWords=" + applicationKeyWords + ", workplace=" + workplace + ", journey=" + journey
				+ ", contract=" + contract + "]";
	}
	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		obj.put("url",getApplicationUrl());
		obj.put("title",getApplicationTitle());
		obj.put("description",getApplicationDescription());
		obj.put("keywords",getApplicationKeyWords());
		obj.put("workplace",getWorkplace().toString());
		obj.put("address",getJobAdress());
		obj.put("company",getCompanyName());
		return obj;
	}
	private String applicationUrl; 
	private String applicationTitle; 
	private String applicationDescription;
	private String jobAdress;
	private String companyName;
	private String countryCode;
	private float paymentPerMonth;
	private LocalDate date;
	private List<String> applicationKeyWords;
	private WorkPlaceType workplace = WorkPlaceType.UNKNOWN;
	private JourneyType journey = JourneyType.UNKNOWW;
	private ContractType contract = ContractType.UNKNOWN;
	public JobApplication(){
		
	}
	public String getApplicationUrl() {
		return applicationUrl;
	}
	public void setApplicationUrl(String applicationUrl) {
		this.applicationUrl = applicationUrl;
	}
	public String getApplicationTitle() {
		return applicationTitle;
	}
	public void setApplicationTitle(String applicationTitle) {
		this.applicationTitle = applicationTitle;
	}
	public String getApplicationDescription() {
		return applicationDescription;
	}
	public void setApplicationDescription(String applicationDescription) {
		this.applicationDescription = applicationDescription;
	}
	public String getJobAdress() {
		return jobAdress;
	}
	public void setJobAdress(String jobAdress) {
		this.jobAdress = jobAdress;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public float getPaymentPerMonth() {
		return paymentPerMonth;
	}
	public void setPaymentPerMonth(float paymentPerMonth) {
		this.paymentPerMonth = paymentPerMonth;
	}
	public float getPaymentPerYear() {
		return paymentPerMonth*12;
	}
	public void setPaymentPerYear(float paymentPerYear) {
		this.paymentPerMonth = paymentPerYear/12;
	}
	public List<String> getApplicationKeyWords() {
		return applicationKeyWords;
	}
	public void setApplicationKeyWords(List<String> applicationKeyWords) {
		this.applicationKeyWords = applicationKeyWords;
	}
	public WorkPlaceType getWorkplace() {
		return workplace;
	}
	public void setWorkplace(WorkPlaceType workplace) {
		this.workplace = workplace;
	}
	public JourneyType getJourney() {
		return journey;
	}
	public void setJourney(JourneyType journey) {
		this.journey = journey;
	}
	public ContractType getContract() {
		return contract;
	}
	public void setContract(ContractType contract) {
		this.contract = contract;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
}
