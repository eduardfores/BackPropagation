package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import DataStructure.Tensor;

public class MainTest {

	private static BufferedReader reader;

	public static String readTestData() throws IOException {
		
		try {
			reader = new BufferedReader(new FileReader("src/testData/test4.csv"));
			String line;
			StringBuilder content = new StringBuilder();
			
			while ((line = reader.readLine()) != null) {
		        content.append(line);
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
		int listNodes[] = {2,10,1};
		String data;
		
		tensor.initializeTensor(listNodes);
		try {
			data=MainTest.readTestData();
			tensor.train(data,20,0,1000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(tensor);
		System.out.println(tensor.execute("2 1", 20, 0));
		System.out.println(tensor.execute("2 2", 20, 0));
		System.out.println(tensor.execute("5 10", 20, 0));
		System.out.println(tensor.execute("1 1", 20, 0));
		System.out.println(tensor.execute("3 8", 20, 0));
		System.out.println(tensor.execute("9 9", 20, 0));
	}

}
