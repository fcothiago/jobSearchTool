package br.com.jobsearchtool.main;
import br.com.jobsearchtool.webscrapper.webdomains.*;
import br.com.jobsearchtool.webscrapper.LoadSubDomains;
import java.util.List;
public class main {
	public static void main(String[] args) {
		System.out.println("Hello World Seu Desempregado");
		Solides domain = new Solides();
		domain.softSearch();
	}
}
