package Graphics;

import java.awt.Color;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ScatterPlot extends JFrame{
	  private static final long serialVersionUID = 6294689542092367723L;

	  private XYSeries series1;
	  DecimalFormatSymbols separadoresPersonalizados;
	  DecimalFormat formato1;
	  
	  public ScatterPlot(String title) {
		    super(title);
		  	this.series1= new XYSeries("Errors");
		  	this.separadoresPersonalizados = new DecimalFormatSymbols();
		  	separadoresPersonalizados.setDecimalSeparator('.');
		  	this.formato1 = new DecimalFormat("#.00", separadoresPersonalizados);
	  }
	  
	  public void add(double x, double y){
		  this.series1.addOrUpdate(Double.parseDouble(formato1.format(x)), Double.parseDouble(formato1.format(y)));
	  }
	  
	  public void visualize() {
		  XYSeriesCollection dataset = new XYSeriesCollection();
		  dataset.addSeries(series1);
		  
		  JFreeChart chart = ChartFactory.createScatterPlot(
			        "Plot Errors", 
			        "Obtained Result", "Result Estimated", dataset);
		  
		  XYPlot plot = (XYPlot)chart.getPlot();
		    plot.setBackgroundPaint(new Color(255,228,196));
		    
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
}

