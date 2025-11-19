package br.com.jobsearchtool.main;
import br.com.jobsearchtool.webscrapper.webdomains.*;
import br.com.jobsearchtool.webscrapper.Utils;
import java.util.List;
import java.util.Scanner;

public class main {
	public static void main(String[] args) throws InterruptedException {
		System.out.println("Hello World Seu Desempregado");
		
		Inhire domainInhire = new Inhire();
		Solides domainSolides = new Solides();
		Recrutai domainRecrutai = new Recrutai();

		Thread taskInhire = new Thread(domainInhire); 
		Thread taskSolides = new Thread(domainSolides); 
		Thread taskRecrutai = new Thread(domainRecrutai); 
		
		taskInhire.start();
		taskSolides.start();
		taskRecrutai.start();

		Scanner sc = new Scanner(System.in);
		sc.nextLine();
		
		domainInhire.sendFinishSignal();
		domainSolides.sendFinishSignal();
		domainRecrutai.sendFinishSignal();

		taskInhire.join();
		taskSolides.join();
		taskRecrutai.join();
		
		domainInhire.exportJSON();
		domainRecrutai.exportJSON();
		domainRecrutai.exportJSON();

	}
}
