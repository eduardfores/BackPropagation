package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Tensor.Tensor;

public class MainRingMerged {

	private static BufferedReader reader;
	private final static String FILE="mergedResults.txt";
	public static String readTestData(String file) throws IOException {
		
		try {
			reader = new BufferedReader(new FileReader(file));
			String line;
			StringBuilder content = new StringBuilder();
			
			while ((line = reader.readLine()) != null) {
		        content.append(line.replace(",", " "));
		        content.append(System.lineSeparator());
		    }
			
			return content.toString();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Tensor tensor = new Tensor();
		int listNodes[] = {2,20,10,5,1}; //merged
		String data;
		String test;
		
		tensor.initializeTensor(listNodes);
		try {
			data=MainRingSeptasrable.readTestData("src/DataRing/ring-merged.csv");
			test=MainRingSeptasrable.readTestData("src/DataRing/ring-test.csv");
			tensor.train(data,10000,test,FILE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(tensor);
	}

}
