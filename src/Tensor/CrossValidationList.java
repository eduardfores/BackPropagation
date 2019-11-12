package Tensor;

import java.util.ArrayList;

public class CrossValidationList {
	private ArrayList<ArrayList<Double []>> list;

	public CrossValidationList() {
		this.list=new ArrayList<>();
	}

	public ArrayList<ArrayList<Double[]>> getList() {
		return list;
	}
	
	public void addDataList(ArrayList<Double []> list) {
		this.list.add(list);
	}
	
	public ArrayList<Double[]> getDataSet(int c){
		return this.list.get(c);
	}
	
	public Double[] getPattern(int c, int p) {
		return this.list.get(c).get(p);
	}
}
