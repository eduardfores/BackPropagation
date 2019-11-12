package Tensor;

import java.util.ArrayList;
import java.util.Random;

public class ListOfList {
	
	private ArrayList<Double []> arrayList;
	
	public ListOfList() {
		this.arrayList=new ArrayList<Double[]>();
	}
	
	public void initializeListOfLists(int listNodes[], int type) {
		int lenList=listNodes.length;
		
		for(int i=0; i<lenList; i++) {
			this.arrayList.add(new Double[listNodes[i]]);
		}
		initializeParams(type);
	}
	
	/*
	 * if you send 1 will be initialized as thressHold and with 0 will be results
	 */
	private void initializeParams(int type) {
		switch(type) {
			case 0:
				for(Double[] list : this.arrayList) {
					for(int i=0; i < list.length; i++) {
						list[i]=0.0;
					}
				}
				break;
			case 1:
				Random rand = new Random();
				
				for(Double[] list : this.arrayList) {
					for(int i=0; i < list.length; i++) {
						list[i]=(((0)+(2-0))*rand.nextDouble())-1;
					}
				}
				break;
		}
	}

	
	public ArrayList<Double[]> getArrayList() {
		return arrayList;
	}

	public void setArrayList(ArrayList<Double[]> arrayList) {
		this.arrayList = arrayList;
	}

	public void addList(Double list[]) {
		this.arrayList.add(list);
	}
	
	@Override
	public String toString() {
		String aux="";
		for(int i=0; i< this.arrayList.size(); i++) {
			for(int j=0; j< this.arrayList.get(i).length; j++) {
				aux=aux+this.arrayList.get(i)[j]+" ";
			}
			aux=aux+"\n";
		}
		return aux;
	}
	
	
}
