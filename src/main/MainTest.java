package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import DataStructure.Tensor;

public class MainTest {

	public static String readTestData() throws IOException {
		
		try {
			BufferedReader reader = new BufferedReader(new FileReader("src/testData/test2.csv"));
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
		int listNodes[] = {2,5,10,5,1};
		String data;
		
		tensor.initializeTensor(listNodes);
		try {
			data=MainTest.readTestData();
			tensor.train(data,50,0,2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(tensor);
		//System.out.println(tensor.execute("0 0", 10, 0));
		System.out.println(tensor.execute("2 2", 50, 0));
		System.out.println(tensor.execute("5 10", 50, 0));
		System.out.println(tensor.execute("1 1", 50, 0));
		System.out.println(tensor.execute("3 9", 50, 0));
	}

}
