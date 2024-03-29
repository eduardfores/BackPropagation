package Tensor;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import Graphics.ScatterPlot;

public class Tensor {

	private static final double learningRate = 0.01;
	private static final double momentum = 0.9;
	private static final double sMAX = 0.9;
	private static final double sMIN = 0.1;
	private static final double xMIN = 0;
	private static final int kFold = 4; // k-Fold Cross-Validation
	private double[] maxs;

	private CrossValidationList dataSet;
	private HashMap<Integer, Relation> tensor;
	private ListOfList results;
	private ListOfList threeHold;
	private ListOfList deltasList;
	private HashMap<Integer, Relation> changesWeigth;
	private ListOfList changesThreeHold;
    private ArrayList<Double> CVError;
    
	private ScatterPlot graph;

	public Tensor() {
		this.tensor = new HashMap<Integer, Relation>();
		this.changesWeigth = new HashMap<Integer, Relation>();
		this.dataSet = new CrossValidationList();
		this.CVError = new ArrayList<>();
		this.graph = new ScatterPlot("Plot Error");
	}

	/*
	 * This funcion need 1 list with the numbers of neurons in each layer. Ex:
	 * listNodes[0]=x -> the first layer have x neurons.
	 */
	public void initializeTensor(int listNodes[]) {
		int lenList = listNodes.length;

		this.results = new ListOfList();
		this.results.initializeListOfLists(listNodes, 0); // This lists will have got all results of each neurons
		// System.out.println(this.results);

		this.threeHold = new ListOfList();
		this.threeHold.initializeListOfLists(listNodes, 0); // this list will have got threesHold or vias
		// System.out.println(this.threeHold);

		this.deltasList = new ListOfList();
		this.deltasList.initializeListOfLists(listNodes, 0);
		// System.out.println(this.deltasList);

		this.changesThreeHold = new ListOfList();
		this.changesThreeHold.initializeListOfLists(listNodes, 0);

		for (int i = 1; i < lenList; i++) {
			this.tensor.put(new Integer(i - 1), new Relation(listNodes[i], listNodes[i - 1], true));
			this.changesWeigth.put(new Integer(i - 1), new Relation(listNodes[i], listNodes[i - 1], false));
		}
	}

	public void crossValidation(String str, int epochs, int testPatterns, String fileName, int listNodes[]) {
		int percent = 0;
		String[] data = str.split(System.lineSeparator());
		int validatorSet=0;
		Double errorCV=0.0;
		
		this.initializeTensor(listNodes);
		this.calculateMaxsOfDataSet(data);
		this.processDataSet(data, testPatterns);

		System.out.println("trainning");

		System.out.println(percent + "% of CV loop "+(validatorSet+1)+"/"+kFold);
		for (int cv = 0; cv < kFold; cv++) { // repeat 4 times the process of CV
			for (int c = 0; c < kFold; c++) { // traverse the each list
				if(c!=validatorSet) // jump the train for the validator list
					for (int e = 0; e < epochs; e++) { // repeat the process X epochs
						for (int p = 0; p < this.dataSet.getDataSet(c).size() - 1; p++) { // forEach pattern do FF and BP 
		
							Double[] pattern = this.dataSet.getPattern(c, p);
							double estimatedResultScaled = pattern[pattern.length - 1];
							for (int j = 0; j < results.getArrayList().get(0).length; j++) {
								results.getArrayList().get(0)[j] = pattern[j];
							}
		
							double finalOutput = this.feedForward();
							if (finalOutput != estimatedResultScaled)
								this.backPropagation(estimatedResultScaled);
						}
					}
				percent += 25;
				System.out.println(percent + "% of CV loop "+(validatorSet+1)+"/"+kFold);	
			}
			
			this.testCrossValidation(validatorSet);
			
			//reset all parameter for the next test
			this.initializeTensor(listNodes);
			percent=0;
			this.graph=new ScatterPlot("Plot Error");
			validatorSet++;
			
			System.out.println(percent + "% of CV loop "+(validatorSet+1)+"/"+kFold);
		}
		for(Double e: CVError)
			errorCV+=e;
		
		System.out.println("The error is "+ errorCV/4);
	}
	
	/*
	 * This function use only the feed forward to use the Neural Network to predict
	 * some result.
	 */
	public double execute(String str) {
		double output = 0.0;
		String[] data = str.split(" ");

		Double input = null;
		// take the first params that we must input in the NN
		for (int j = 0; j < results.getArrayList().get(0).length; j++) {
			input = scaleParams(sMAX, sMIN, maxs[j], xMIN, Double.valueOf(data[j]));
			results.getArrayList().get(0)[j] = input;
		}

		Double actualRelation = 0.0;
		Double neuronResult = 0.0;
		Double threeHold = 0.0;
		Double outPutNeuron = 0.0;

		for (int j = 0; j < this.tensor.size(); j++) { // this j is the actual layer
			for (int k = 0; k < this.tensor.get(j).getRelation().length; k++) { // this k is the number of neurons of
																				// the actual layer
				for (int l = 0; l < this.tensor.get(j).getRelation()[k].length; l++) { // this l is the number of
																						// neurons of the last layer
					actualRelation = this.tensor.get(j).getRelation()[k][l];
					neuronResult = this.results.getArrayList().get(j)[l];
					threeHold = this.threeHold.getArrayList().get(j + 1)[k]; // we want the threeHold from the actual
																				// neuron
					outPutNeuron = outPutNeuron + (actualRelation * neuronResult);
				}
				outPutNeuron -= threeHold;
				outPutNeuron = sigmoid(outPutNeuron);

				if ((j + 1) < this.results.getArrayList().size())
					this.results.getArrayList().get(j + 1)[k] = outPutNeuron;

				output = outPutNeuron;
				outPutNeuron = 0.0;
			}
		}
		return this.unscaleParams(1, 0, maxs[maxs.length - 1], 0, output);
		// return output;
	}

	/*
	 * This function use the FeddForward and the BP to train the Neural Network. And
	 * use the test function to calculate the test error
	 */
	public void train(String str, int epochs, int testPatterns, String fileName) {
		int percent = 0;
		String[] data = str.split(System.lineSeparator());
		this.calculateMaxsOfDataSet(data);
		this.processDataSet(data, testPatterns);

		System.out.println("trainning");

		System.out.println(percent + "%");
		for (int c = 0; c < kFold; c++) {
			for (int e = 0; e < epochs; e++) {
				for (int p = 0; p < this.dataSet.getDataSet(c).size() - 1; p++) {

					Double[] pattern = this.dataSet.getPattern(c, p);
					double estimatedResultScaled = pattern[pattern.length - 1];
					for (int j = 0; j < results.getArrayList().get(0).length; j++) {
						results.getArrayList().get(0)[j] = pattern[j];
					}

					double finalOutput = this.feedForward();
					if (finalOutput != estimatedResultScaled)
						this.backPropagation(estimatedResultScaled);
				}
			}
			percent += 25;
			System.out.println(percent + "%");
		}

		this.test(fileName);

	}

	/*
	 * This function train the neuralnetwork but use one external data to test it
	 */
	public void train(String str, int epochs, String file, String fileName) {
		int percent = 0;
		String[] data1 = str.split(System.lineSeparator());
		String[] data2 = str.split(System.lineSeparator());
		this.calculateMaxsOfDataSet(data1);
		this.processDataSet(data1, data2);

		System.out.println("trainning");
		System.out.println(percent + "%");
		for (int c = 0; c < kFold; c++) {
			for (int e = 0; e < epochs; e++) {
				for (int p = 0; p < this.dataSet.getDataSet(c).size() - 1; p++) {

					Double[] pattern = this.dataSet.getPattern(c, p);
					double estimatedResultScaled = pattern[pattern.length - 1];
					for (int j = 0; j < results.getArrayList().get(0).length; j++) {
						results.getArrayList().get(0)[j] = pattern[j];
					}

					double finalOutput = this.feedForward();
					if (finalOutput != estimatedResultScaled)
						this.backPropagation(estimatedResultScaled);
				}
			}
			percent += 25;
			System.out.println(percent + "%");
		}

		this.test(fileName);

	}

	private double feedForward() {
		Double actualRelation = 0.0;
		Double neuronResult = 0.0;
		Double threeHold = 0.0;
		Double outPutNeuron = 0.0;
		double finalOutput = 0.0;

		for (int j = 0; j < this.tensor.size(); j++) { // this j is the actual layer
			for (int k = 0; k < this.tensor.get(j).getRelation().length; k++) { // this k is the number of neurons of
																				// the actual layer
				for (int l = 0; l < this.tensor.get(j).getRelation()[k].length; l++) { // this l is the number of
																						// neurons of the last layer
					actualRelation = this.tensor.get(j).getRelation()[k][l];
					neuronResult = this.results.getArrayList().get(j)[l];
					threeHold = this.threeHold.getArrayList().get(j + 1)[k]; // we want the threeHold from the actual
																				// neuron

					outPutNeuron = outPutNeuron + (actualRelation * neuronResult);
				}
				outPutNeuron -= threeHold;
				outPutNeuron = sigmoid(outPutNeuron);

				if ((j + 1) < this.results.getArrayList().size())
					this.results.getArrayList().get(j + 1)[k] = outPutNeuron;
				finalOutput = outPutNeuron;
				outPutNeuron = 0.0;
			}
		}
		return finalOutput;
	}

	private void backPropagation(double estimatedResult) {
		calculateDelta(estimatedResult);
		calculateErrorWeight(); // here, we calculate all errors of weights
		calculateErrorThresHold(); // here we calculate all errors of vias
		updateWeigth();
		updateThresHolds();
	}

	private void calculateDelta(double estimatedResult) {
		ArrayList<Double[]> results = this.results.getArrayList();
		double result = results.get(results.size() - 1)[0];
		double derivatedResult = this.sigmoidDerivate(result);

		// calculate delta of output layer and save it in its respective data structure
		double deltaL = derivatedResult * (result - estimatedResult);
		this.deltasList.getArrayList().get(this.deltasList.getArrayList().size() - 1)[0] = deltaL;

		double derivateAuxiliar = 0.0;
		double summatori = 0.0;
		double weigth = 0.0;
		double deltaAuxiliar = 0.0;

		for (int j = results.size() - 2; j > 0; j--) { // this j is the actual layer
			for (int k = 0; k < results.get(j).length; k++) {
				result = results.get(j)[k];
				derivateAuxiliar = sigmoidDerivate(result);

				// in the Relation of tensor the k is the second index Relation[][k]
				// the index l will be the first in Relation -> Relation[l][k]
				for (int l = 0; l < this.tensor.get(j).getRelation().length; l++) {
					weigth = this.tensor.get(j).getRelation()[l][k];
					summatori = summatori + (deltaL * weigth);
					deltaL = this.deltasList.getArrayList().get(j + 1)[l];
				}
				deltaAuxiliar = derivateAuxiliar * summatori;
				this.deltasList.getArrayList().get(j)[k] = deltaAuxiliar;
				summatori = 0.0;
			}
		}
	}

	private void calculateErrorWeight() {
		ArrayList<Double[]> results = this.results.getArrayList();
		double result = results.get(results.size() - 1)[0];
		double weigth = 0.0;
		double deltaAuxiliar = 0.0;

		for (int j = this.deltasList.getArrayList().size() - 1; j > 0; j--) { // this j is the actual layer
			for (int k = 0; k < this.deltasList.getArrayList().get(j).length; k++) {
				deltaAuxiliar = this.deltasList.getArrayList().get(j)[k];
				for (int l = 0; l < this.changesWeigth.get(j - 1).getRelation()[k].length; l++) {
					result = results.get(j - 1)[l];
					weigth = this.changesWeigth.get(j - 1).getRelation()[k][l];
					this.changesWeigth.get(j - 1).getRelation()[k][l] = (momentum * weigth)
							- (learningRate * deltaAuxiliar * result);
				}
			}
		}
	}

	private void calculateErrorThresHold() {
		double threeHoldAuxiliar = 0.0;
		double deltaAuxiliar = 0.0;

		for (int j = this.deltasList.getArrayList().size() - 1; j > 0; j--) { // this j is the actual layer
			for (int k = 0; k < this.deltasList.getArrayList().get(j).length; k++) {
				deltaAuxiliar = this.deltasList.getArrayList().get(j)[k];
				threeHoldAuxiliar = this.changesThreeHold.getArrayList().get(j)[k];
				this.changesThreeHold.getArrayList().get(j)[k] = (learningRate * deltaAuxiliar)
						+ (momentum * threeHoldAuxiliar);
			}
		}
	}

	private void updateWeigth() {
		for (int j = this.changesWeigth.size() - 1; j > 0; j--) { // this j is the actual layer
			for (int k = 0; k < this.changesWeigth.get(j).getRelation().length; k++) {
				for (int l = 0; l < this.changesWeigth.get(j).getRelation()[k].length; l++) {
					this.tensor.get(j).getRelation()[k][l] += this.changesWeigth.get(j).getRelation()[k][l];
				}
			}
		}
	}

	private void updateThresHolds() {
		for (int j = this.changesThreeHold.getArrayList().size() - 1; j > 0; j--) { // this j is the actual layer
			for (int k = 0; k < this.changesThreeHold.getArrayList().get(j).length; k++) {
				this.threeHold.getArrayList().get(j)[k] += this.changesThreeHold.getArrayList().get(j)[k];
			}
		}
	}

	private double sigmoid(double x) {
		return (1 / (1 + Math.pow(Math.E, (-1 * x))));
	}

	private double sigmoidDerivate(double result) {
		// We use the sigmoid activation function and
		// the derivate of this function is this f'(x)=h*(1-h)
		return result * (1 - result); // result from NN
	}

	private Double scaleParams(double sMax, double sMin, double xMax, double xMin, Double x) {
		return (sMin + (((sMax - sMin) / (xMax - xMin)) * (x - xMin)));
	}

	private Double unscaleParams(double sMax, double sMin, double xMax, double xMin, Double s) {
		return (xMin + (((xMax - xMin) / (sMax - sMin)) * (s - sMin)));
	}

	/*
	 * This function calulate all maxs of each parameter of the patterns to scale it
	 * correctly
	 */
	private void calculateMaxsOfDataSet(String data[]) {
		this.maxs = new double[data[0].split(" ").length]; // initialize the array according the dataset

		for (int i = 0; i < this.maxs.length; i++) {
			this.maxs[i] = 0.0;
		}

		for (int i = 0; i < data.length; i++) {
			String[] params = data[i].split(" ");
			for (int j = 0; j < params.length; j++) {
				Double aux = Double.parseDouble(params[j]);
				if (this.maxs[j] == 0.0) {
					this.maxs[j] = aux;
				} else {
					if (this.maxs[j] < aux) {
						this.maxs[j] = aux;
					}
				}
			}
		}
	}

	/*
	 * This function process the DataSet to do the cross-validation and scaling the
	 * parameters with the max searched previously
	 */
	private void processDataSet(String data[], int testPatterns) {
		System.out.println("Processing data");
		int crossDataLength = (data.length - testPatterns);
		int lenghtSet = crossDataLength / kFold;
		int firstSet;
		int finalSet;

		// first 4 data sets
		for (int i = 0; i < kFold; i++) {
			firstSet = (i != kFold - 1) ? ((crossDataLength) - (lenghtSet * (i + 1))) : 0;
			finalSet = ((crossDataLength) - (lenghtSet * i));
			ArrayList<Double[]> auxList = new ArrayList<>();

			for (int j = firstSet; j < finalSet; j++) {
				String[] params = data[j].split(" ");
				Double[] pattern = new Double[params.length];
				for (int k = 0; k < params.length; k++) {
					pattern[k] = scaleParams(sMAX, sMIN, this.maxs[k], xMIN, Double.valueOf(params[k]));
				}
				auxList.add(pattern);
			}
			this.dataSet.addDataList(auxList);
		}

		ArrayList<Double[]> auxList = new ArrayList<>();

		// test data
		for (int j = crossDataLength; j < data.length; j++) {
			String[] params = data[j].split(" ");
			Double[] pattern = new Double[params.length];
			for (int k = 0; k < params.length; k++) {
				pattern[k] = scaleParams(sMAX, sMIN, this.maxs[k], xMIN, Double.valueOf(params[k]));
			}
			auxList.add(pattern);
		}
		this.dataSet.addDataList(auxList);

	}

	private void processDataSet(String data[], String test[]) {
		System.out.println("Processing data");
		int crossDataLength = data.length;
		int lenghtSet = crossDataLength / kFold;
		int firstSet;
		int finalSet;

		// first 4 data sets
		for (int i = 0; i < kFold; i++) {
			firstSet = (i != kFold - 1) ? ((crossDataLength) - (lenghtSet * (i + 1))) : 0;
			finalSet = ((crossDataLength) - (lenghtSet * i));
			ArrayList<Double[]> auxList = new ArrayList<>();

			for (int j = firstSet; j < finalSet; j++) {
				String[] params = data[j].split(" ");
				Double[] pattern = new Double[params.length];
				for (int k = 0; k < params.length; k++) {
					pattern[k] = scaleParams(sMAX, sMIN, this.maxs[k], xMIN, Double.valueOf(params[k]));
				}
				auxList.add(pattern);
			}
			this.dataSet.addDataList(auxList);
		}

		ArrayList<Double[]> auxList = new ArrayList<>();

		// test dataset
		for (int j = 0; j < test.length; j++) {
			String[] params = test[j].split(" ");
			Double[] pattern = new Double[params.length];
			for (int k = 0; k < params.length; k++) {
				pattern[k] = scaleParams(sMAX, sMIN, this.maxs[k], xMIN, Double.valueOf(params[k]));
			}
			auxList.add(pattern);
		}
		this.dataSet.addDataList(auxList);

	}

	/*
	 * Do the same as the execute function but this function use The variable graph
	 * to calculate the error of Neural Network.
	 */
	private void test(String file) {
		System.out.println("Testing");

		FileWriter fichero = null;
        PrintWriter pw = null;
        
        try
        {
        	fichero = new FileWriter("src/Results/"+file);
            pw = new PrintWriter(fichero);
            
			for (int p = 0; p < this.dataSet.getDataSet(kFold).size() - 1; p++) {
	
				//prepare data to do the feedForward
				Double[] pattern = this.dataSet.getPattern(kFold, p);
				double estimatedResultScaled = pattern[pattern.length - 1];
				for (int j = 0; j < this.results.getArrayList().get(0).length; j++) {
					this.results.getArrayList().get(0)[j] = pattern[j];
				}
	
				double finalOutput = this.feedForward(); //predict
	
				String line=""; //save the predict in document
				for (int i=0; i<pattern.length; i++)
					line+=String.valueOf(pattern[i])+" ";
				
				pw.println(line+finalOutput);
				
				this.graph.addWithGraph(this.unscaleParams(1, 0, maxs[maxs.length - 1], 0, finalOutput),
						this.unscaleParams(1, 0, maxs[maxs.length - 1], 0, estimatedResultScaled));
			}
	
			pw.println("Error: "+this.graph.visualizeWithGraph()+"%");//show the scatter plot and save the error
			
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           try {
           // Nuevamente aprovechamos el finally para 
           // asegurarnos que se cierra el fichero.
           if (null != fichero)
              fichero.close();
           } catch (Exception e2) {
              e2.printStackTrace();
           }
        }
	}

	/*
	 * Do the same as the execute function but this function use The variable graph
	 * to calculate the error of Neural Network.
	 */
	private void testCrossValidation(int Sx) {
		System.out.println("Testing "+(Sx+1)+"/"+kFold);

			for (int p = 0; p < this.dataSet.getDataSet(Sx).size() - 1; p++) {
	
				//prepare data to do the feedForward
				Double[] pattern = this.dataSet.getPattern(Sx, p);
				double estimatedResultScaled = pattern[pattern.length - 1];
				
				for (int j = 0; j < this.results.getArrayList().get(0).length; j++) {
					this.results.getArrayList().get(0)[j] = pattern[j];
				}
	
				double finalOutput = this.feedForward(); //predict
				
				this.graph.add(finalOutput,estimatedResultScaled);
			}			
    
			CVError.add(this.graph.getError());
	}
	
	@Override
	public String toString() {
		String aux = "";
		for (int i = 0; i < tensor.size(); i++) {
			aux = aux + "Relation " + i + " {" + tensor.get(i).toString() + "}\n";
		}
		return aux;
	}
}
