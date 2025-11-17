package br.com.jobsearchtool.webscrapper;

import java.util.List;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

public final class Utils {
	static public List<String> loadSubdomains(String fileName){
		List<String> result;
		try (InputStream inputStream = Utils.class.getResourceAsStream(fileName)) {
			if (inputStream == null)
				throw new RuntimeException("Failed to parse " + fileName + " : " + "File not found");
			Stream<String> stream = new BufferedReader(new InputStreamReader(inputStream)).lines();
			return stream.collect(Collectors.toList());
		} catch (IllegalArgumentException | IOException e) {
			throw new RuntimeException("Failed to parse " + fileName + " : " + e.getMessage());
		}
	}
	static public JSONObject loadJSON(String path) {
        try{
			String conteudo = Files.readString(Paths.get(path));
	        return new JSONObject(conteudo);
		} catch (IOException e) {
		}
		return new JSONObject();
	}
	static public void saveJSON(JSONObject obj,String path) {
        try{
        	Files.writeString(
                    Paths.get(path),
                    obj.toString(4), // "4" = identação bonita
                    StandardCharsets.UTF_8
                );
		} catch (IOException e){
			System.out.println("Could not export json to " + path );
			e.printStackTrace();
		}
	}
}
