package Graphics;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterPlot extends JFrame {
	private static final long serialVersionUID = 6294689542092367723L;

	private XYSeries series1;
	private DecimalFormatSymbols separadoresPersonalizados;
	private DecimalFormat formato1;
	private ArrayList<Double> real;
	private ArrayList<Double> errors;
	private double error;

	public ScatterPlot(String title) {
		super(title);
		this.series1 = new XYSeries("Errors");
		this.separadoresPersonalizados = new DecimalFormatSymbols();
		separadoresPersonalizados.setDecimalSeparator('.');
		this.formato1 = new DecimalFormat("#.00", separadoresPersonalizados);
		this.real = new ArrayList<Double>();
		this.errors = new ArrayList<Double>();
	}

	/*
	 * It save the data to calculate error only.
	 */
	public void add(double predict, double real) {
		this.real.add(real);
		this.errors.add(Math.abs(real - predict));
	}

	/*
	 * Save the data to calculate the error and draw the graph.
	 */
	public void addWithGraph(double predict, double real) {
		this.real.add(real);
		this.errors.add(Math.abs(real - predict));
		this.series1.addOrUpdate(Double.parseDouble(formato1.format(predict)),
				Double.parseDouble(formato1.format(real)));
	}

	/*
	 * It print the error% only.
	 */
	public void visualize() {
		calculateError();
		System.out.println("The error of this neural network is: " + this.error + "%\n\n");
	}

	/*
	 * This function visualize the graph with the predicted and real params. It
	 * print the error% too.
	 */
	public void visualizeWithGraph() {
		XYSeriesCollection dataset = new XYSeriesCollection();

		dataset.addSeries(series1);
		calculateError();

		System.out.println("The error of this neural network is: " + this.error + "%\n\n");

		JFreeChart chart = ChartFactory.createScatterPlot("Plot Errors", "Predicted Result", "Result Estimated",
				dataset);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(new Color(255, 228, 196));

		ChartPanel panel = new ChartPanel(chart);
		setContentPane(panel);

		SwingUtilities.invokeLater(() -> {
			ScatterPlot example = this;
			example.setSize(800, 400);
			example.setLocationRelativeTo(null);
			example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			example.setVisible(true);
		});
	}

	/*
	 * This function calculate the function error of PDF like the excel example.
	 */
	private void calculateError() {
		double sumError = 0;
		double sumReal = 0;
		for (int i = 0; i < this.errors.size(); i++) {
			sumError += this.errors.get(i);
			sumReal += this.real.get(i);
		}

		this.error = (sumError / sumReal) * 100;
	}

	public double getError() {
		return error;
	}
}
