package DataStructure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.sun.org.apache.xalan.internal.xsltc.compiler.sym;

public class Tensor {

	private static final double learningRate= 0.1;
	private static final double momentum=0.1;
	private static final double MAX=50;
	
	private HashMap<Integer, Relation> tensor;
	private ListOfList results;
	private ListOfList threeHold;
	private ListOfList deltasList;
	private HashMap<Integer, Relation> changesWeigth;
	private ListOfList changesThreeHold;
	
	public Tensor() {
		this.tensor=new HashMap<Integer, Relation>();
		this.results=new ListOfList();
		this.threeHold=new ListOfList();
		this.deltasList= new ListOfList();
		this.changesWeigth= new HashMap<Integer, Relation>();
		this.changesThreeHold= new ListOfList();
	}
	
	/*
	 * This funcion need 1 list with the numbers of neurons in each layer.
	 * Ex: listNodes[0]=x -> the first layer have x neurons. 
	 */
	public void initializeTensor(int listNodes[]) {
		int lenList=listNodes.length;
		
		this.results.initializeListOfLists(listNodes, 0); //This lists will have got all results of each neurons
		//System.out.println(this.results);
		
		this.threeHold.initializeListOfLists(listNodes, 0); //this list will have got threesHold or vias 
		//System.out.println(this.threeHold);
		
		this.deltasList.initializeListOfLists(listNodes, 0);
		//System.out.println(this.deltasList);
		
		this.changesThreeHold.initializeListOfLists(listNodes, 0);
		
		for(int i=1; i<lenList; i++) {
			this.tensor.put(new Integer(i-1), new Relation(listNodes[i], listNodes[i-1],true));
			this.changesWeigth.put(new Integer(i-1), new Relation(listNodes[i], listNodes[i-1],false));
		}
	}

	
	public double execute(String str, double xMax, double xMin) {
		double output=0.0;
		String[] data=str.split(" ");
		
		Double input=null;
		//take the first params that we must input in the NN
		for(int j=0; j<results.getArrayList().get(0).length; j++) {
			input=scaleParams(1, 0, xMax, xMin, Double.valueOf(data[j]));
			results.getArrayList().get(0)[j]=input;
		}
		
		Double actualRelation=0.0;
		Double neuronResult=0.0;
		Double threeHold=0.0;
		Double outPutNeuron=0.0;
		
		for (int j=0; j<this.tensor.size();j++) { //this j is the actual layer
			for(int k=0; k<this.tensor.get(j).getRelation().length; k++) { //this k is the number of neurons of the actual layer
				for(int l=0; l<this.tensor.get(j).getRelation()[k].length; l++) { //this l is the number of neurons of the last layer
					actualRelation=this.tensor.get(j).getRelation()[k][l];
					neuronResult=this.results.getArrayList().get(j)[l];
					threeHold=this.threeHold.getArrayList().get(j+1)[k]; //we want the threeHold from the actual neuron
					
					outPutNeuron=outPutNeuron+(actualRelation*neuronResult);
				}
				outPutNeuron-=threeHold;
				outPutNeuron=sigmoid(outPutNeuron);
				
				if((j+1)<this.results.getArrayList().size())
					this.results.getArrayList().get(j+1)[k]=outPutNeuron;
				
				output=outPutNeuron;
				outPutNeuron=0.0;
			}
		}
		return this.unscaleParams(1, 0, MAX, 0, output);
		//return output;
	}
	
	public void train(String str, double xMax, double xMin, int epochs) {
		String[] data=str.split("\n");
		for (int e=0; e<epochs; e++) {
			for(int i=0; i<data.length; i++) {
				Random rand = new Random();
				String[] params=data[rand.nextInt(data.length)].split(" "); //take one list with params + result
				Double input=null;
				//take the first params that we must input in the NN
				for(int j=0; j<results.getArrayList().get(0).length; j++) {
					input=scaleParams(1, 0, xMax, xMin, Double.valueOf(params[j]));
					results.getArrayList().get(0)[j]=input;
				}
				//System.out.println(this.results);
				
				double finalOutput=feedForward(str, xMax, xMin, epochs);
				double estimatedResultScaled=scaleParams(1, 0, xMax, xMin, Double.valueOf(params[params.length-1]));
				if(finalOutput!=estimatedResultScaled)
					this.backPropagation(estimatedResultScaled);
			}
		}
	}
	
	private double feedForward(String str, double xMax, double xMin, int epochs) {			
		Double actualRelation=0.0;
		Double neuronResult=0.0;
		Double threeHold=0.0;
		Double outPutNeuron=0.0;
		double finalOutput= 0.0;
		
		for (int j=0; j<this.tensor.size();j++) { //this j is the actual layer
			for(int k=0; k<this.tensor.get(j).getRelation().length; k++) { //this k is the number of neurons of the actual layer
				for(int l=0; l<this.tensor.get(j).getRelation()[k].length; l++) { //this l is the number of neurons of the last layer
					actualRelation=this.tensor.get(j).getRelation()[k][l];
					neuronResult=this.results.getArrayList().get(j)[l];
					threeHold=this.threeHold.getArrayList().get(j+1)[k]; //we want the threeHold from the actual neuron
					
					outPutNeuron=outPutNeuron+(actualRelation*neuronResult);
				}
				outPutNeuron-=threeHold;
				outPutNeuron=sigmoid(outPutNeuron);
				
				if((j+1)<this.results.getArrayList().size())
					this.results.getArrayList().get(j+1)[k]=outPutNeuron;
				finalOutput=outPutNeuron;
				outPutNeuron=0.0;
			}
		}
		return finalOutput;
		//System.out.println(this.results);
	}
	
	private void backPropagation(double estimatedResult) {
				
		calculateDelta(estimatedResult);
		
		//System.out.println(this.tensor+"\n\n");
		//System.out.println(this.results+"\n\n");
		//System.out.println(this.deltasList);
		
		calculateErrorWeight(); //here, we calculate all errors of weights
		calculateErrorThresHold(); //here we calculate all errors of vias
		updateWeigth();
		updateThresHolds();		
	}
	
	private void calculateDelta(double estimatedResult) {
		ArrayList<Double[]> results=this.results.getArrayList();
		double result=results.get(results.size()-1)[0];
		double derivatedResult=this.sigmoidDerivate(result);
		
		//calculate delta of output layer and save it in its respective data structure
		double deltaL= derivatedResult*(result-estimatedResult);
		this.deltasList.getArrayList()
			.get(this.deltasList.getArrayList().size()-1)[0]=deltaL;
		
		double derivateAuxiliar=0.0;
		double summatori=0.0;
		double weigth=0.0;
		double deltaAuxiliar=0.0;
		
		for (int j=results.size()-2; j>0; j--) { //this j is the actual layer
			for (int k=0; k<results.get(j).length; k++) {
				result=results.get(j)[k];
				derivateAuxiliar=sigmoidDerivate(result);
				
				//in the Relation of tensor the k is the second index Relation[][k]
				// the index l will be the first in Relation -> Relation[l][k]
				for(int l=0; l<this.tensor.get(j).getRelation().length; l++) {
					weigth=this.tensor.get(j).getRelation()[l][k];
					summatori=summatori+(deltaL*weigth);
					deltaL=this.deltasList.getArrayList().get(j+1)[l];
				}
				deltaAuxiliar=derivateAuxiliar*summatori;
				this.deltasList.getArrayList().get(j)[k]=deltaAuxiliar;
				summatori=0.0;
			}
		}
	}
	
	private void calculateErrorWeight() {
		ArrayList<Double[]> results=this.results.getArrayList();
		double result=results.get(results.size()-1)[0];
		double weigth=0.0;
		double deltaAuxiliar=0.0;
		
		for (int j=this.deltasList.getArrayList().size()-1; j>0; j--) { //this j is the actual layer
			for(int k=0; k<this.deltasList.getArrayList().get(j).length; k++) {
				deltaAuxiliar=this.deltasList.getArrayList().get(j)[k];
				for(int l=0; l<this.changesWeigth.get(j-1).getRelation()[k].length; l++) {
					result=results.get(j-1)[l];
					weigth=this.changesWeigth.get(j-1).getRelation()[k][l];
					this.changesWeigth.get(j-1).getRelation()[k][l]=
							(momentum*weigth)-(learningRate*deltaAuxiliar*result);
				}
			}
		}
	}
	
	private void calculateErrorThresHold() {
		double threeHoldAuxiliar=0.0;
		double deltaAuxiliar=0.0;
		
		for (int j=this.deltasList.getArrayList().size()-1; j>0; j--) { //this j is the actual layer
			for(int k=0; k<this.deltasList.getArrayList().get(j).length; k++) {
				deltaAuxiliar=this.deltasList.getArrayList().get(j)[k];
				threeHoldAuxiliar=this.changesThreeHold.getArrayList().get(j)[k];
				this.changesThreeHold.getArrayList().get(j)[k]=
						(learningRate*deltaAuxiliar)+(momentum*threeHoldAuxiliar);
			}
		}
	}
	
	private void updateWeigth() {
		for (int j=this.changesWeigth.size()-1; j>0; j--) { //this j is the actual layer
			for(int k=0; k<this.changesWeigth.get(j).getRelation().length; k++) {
				for(int l=0; l<this.changesWeigth.get(j).getRelation()[k].length; l++) {				
					this.tensor.get(j).getRelation()[k][l]+=this.changesWeigth.get(j).getRelation()[k][l];
				}
			}
		}
	}
	
	private void updateThresHolds() {
		for (int j=this.changesThreeHold.getArrayList().size()-1; j>0; j--) { //this j is the actual layer
			for(int k=0; k<this.changesThreeHold.getArrayList().get(j).length; k++) {
				this.threeHold.getArrayList().get(j)[k]+=this.changesThreeHold.getArrayList().get(j)[k];
			}
		}
	}
	
	private double sigmoid(double x) {
	    return (1/( 1 + Math.pow(Math.E,(-1*x))));
	}
	
	private double sigmoidDerivate(double result) {
		//We use the sigmoid activation function and 
		//the derivate of this function is this f'(x)=h*(1-h)
		return result*(1-result); //result from NN
	}
	
	private Double scaleParams(double sMax, double sMin, double xMax, double xMin, Double x) {
		return (sMin+(((sMax-sMin)/(xMax-xMin))*(x-xMin)));
	}
	
	private Double unscaleParams(double sMax, double sMin, double xMax, double xMin, Double s) {
		return (xMin+(((xMax-xMin)/(sMax-sMin))*(s-sMin)));
	}
	
	@Override
	public String toString() {
		String aux="";
		for(int i=0; i< tensor.size(); i++) {
			aux=aux+"Relation "+i+" {"+tensor.get(i).toString()+"}\n";
		}
		return aux;
	}
	
	
}
