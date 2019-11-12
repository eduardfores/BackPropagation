package main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Tensor.Tensor;

public class MainTurbine {

	private static BufferedReader reader;

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
		int listNodes[] = { 4, 10, 20, 10, 1 };
		String data;

		tensor.initializeTensor(listNodes);
		try {
			data = MainTurbine.readTestData();
			tensor.train(data, 1000);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(tensor);
		System.out.println(tensor.execute("598.00 63.940 62.920 5.500"));
		System.out.println(tensor.execute("618.00 82.690 81.540 8.000"));
	}

}
