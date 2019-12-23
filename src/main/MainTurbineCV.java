package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Tensor.Tensor;

public class MainTurbineCV {

	private static BufferedReader reader;
	private final static String FILE="turbineResults.txt";
	public static String readTestData() throws IOException {

		try {
			reader = new BufferedReader(new FileReader("src/DataTurbine/A1-turbine.txt"));
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
		int listNodes[] = { 4, 20, 1 };
		String data;

		try {
			data = MainTurbine.readTestData();
			tensor.crossValidation(data, 10000, 50, FILE, listNodes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
