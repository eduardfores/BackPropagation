package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Tensor.Tensor;

public class MainRingSeptasrable {

	private static BufferedReader reader;
	
	public static String readTestData() throws IOException {
		
		try {
			reader = new BufferedReader(new FileReader("src/DataRings/ring-merged.csv"));
			//reader = new BufferedReader(new FileReader("src/DataRings/ring-separable.csv"));
			String line;
			StringBuilder content = new StringBuilder();
			
			while ((line = reader.readLine()) != null) {
		        content.append(line.replace(";", " "));
		        content.append(System.lineSeparator());
		        System.out.println(line);
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
		int listNodes[] = {2,10,1};
		String data;
		
		tensor.initializeTensor(listNodes);
		try {
			data=MainTest.readTestData();
			tensor.train(data,10000,50);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(tensor);
	}

}
