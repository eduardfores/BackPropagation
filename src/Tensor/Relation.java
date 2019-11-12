package Tensor;

import java.util.Random;

public class Relation {

	private Double[][] relation;

	//we put init= true if we want the matrix initialize with random numbers
	public Relation(int x, int y, boolean init) {
		this.relation = new Double[x][y];
		initializeRelations(init);
	}

	public Double[][] getRelation() {
		return relation;
	}

	public void setRelation(Double[][] relation) {
		this.relation = relation;
	}

	private void initializeRelations(boolean init) {
		if (init) {
			Random rand = new Random();
			for (int i = 0; i < this.relation.length; i++) {
				for (int j = 0; j < this.relation[i].length; j++) {
					this.relation[i][j] = (((0) + (2 - 0)) * rand.nextDouble()) - 1;
				}
			}
		} else {
			for (int i = 0; i < this.relation.length; i++) {
				for (int j = 0; j < this.relation[i].length; j++) {
					this.relation[i][j] = 0.0;
				}
			}
		}
	}

	@Override
	public String toString() {
		String aux = "";
		for (int i = 0; i < this.relation.length; i++) {
			for (int j = 0; j < this.relation[i].length; j++) {
				aux = aux + this.relation[i][j] + " ";
			}
			aux = aux + "\n";
		}
		return aux;
	}

}
