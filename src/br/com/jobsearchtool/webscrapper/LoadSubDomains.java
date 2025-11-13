package br.com.jobsearchtool.webscrapper;
import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
public final class LoadSubDomains {
	static public List<String> load(String fileName){
		List<String> result;
		try (InputStream inputStream = LoadSubDomains.class.getResourceAsStream(fileName)) {
			if (inputStream == null)
				throw new RuntimeException("Failed to parse " + fileName + " : " + "File not found");
			Stream<String> stream = new BufferedReader(new InputStreamReader(inputStream)).lines();
			return stream.collect(Collectors.toList());
		} catch (IllegalArgumentException | IOException e) {
			throw new RuntimeException("Failed to parse " + fileName + " : " + e.getMessage());
		}
	}
}
