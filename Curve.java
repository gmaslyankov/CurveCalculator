package backend;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;
import java.lang.Math.*;

public class Curve {
	
	public static double[][] pts_transf(int[][] list, ProjectClass project) {
		
		double[][] matrix = project.get_matrix();
		double[][] new_list = new double[list.length][2];
		
		System.out.println("After matrix multiplication:");
		
		for(int i=0; i<list.length; i++) {
			new_list[i][0] = list[i][0]*matrix[0][0] + list[i][1]*matrix[0][1];
			new_list[i][1] = list[i][0]*matrix[1][0] + list[i][1]*matrix[1][1];
			System.out.println(new_list[i][0] + "," + new_list[i][1]);
		}
		
		double origin_x = new_list[list.length-1][0];
		double origin_y = new_list[list.length-1][1];
		
		System.out.println("After pts translation:");
		
		if (origin_x >= new_list[0][0]) {
			project.set_direction(-1);
			for(int i=0; i<new_list.length; i++) {
				
				new_list[i][0] = new_list[i][0]*(-1) + origin_x + 0.001;
				new_list[i][1] = new_list[i][1]*(-1) + origin_y + 0.001;
				System.out.println(new_list[i][0] + "," + new_list[i][1]);
			}	
		}
		else if (origin_x < new_list[0][0]) {
			project.set_direction(1);
			for(int i=0; i<new_list.length; i++) {
				
				new_list[i][0] = new_list[i][0] - origin_x + 0.001;
				new_list[i][1] = new_list[i][1]*(-1) + origin_y + 0.001;
				System.out.println(new_list[i][0] + "," + new_list[i][1]);
	
			}	
		}
		
		System.out.println("After conversion from pixels to centimeters:");
		
		double ratio = project.get_ratio()*project.get_ratio_scale();
		System.out.println("ratio used: " + ratio);
		
		for(int i=0; i<new_list.length; i++) {
			new_list[i][0] = new_list[i][0]/ratio;
			new_list[i][1] = new_list[i][1]/ratio;
			System.out.println(new_list[i][0] + "," + new_list[i][1]);
		}
		
		for (int i=new_list.length-2; i>=0; i--) {
			if (new_list[i][0] < new_list[new_list.length-1][0] || new_list[i][0] < 0.0 || new_list[i][1] < 0.0) {
				return null;
			}
		}
		return new_list;
	}
	
	public static void setCurve(ProjectClass project) {
		
		double[][] pts = project.get_curve_pts();
		
		double[] y = new double[pts.length];
		double[] x = new double[pts.length];
		double[] log_y = new double[pts.length];
		double[] log_x = new double[pts.length];
		
		System.out.println("Linearized points:");
		/*
		for (int i=0; i < (pts.length-1); i++) {
			y[i] = Math.pow(Math.log(pts[i][1]), 2.0);
			x[i][0] = Math.pow(Math.log(pts[i][0]), 2.0);
			x[i][1] = Math.log(pts[i][0]);
			
			System.out.println(x[i][0] + "," + x[i][0] + "," + y[i]);
			
		}
		*/
		for (int i=0; i < (pts.length-1); i++) {
			y[i] = pts[i][1];
			x[i] = pts[i][0];
			log_y[i] = Math.log(pts[i][1]);
			log_x[i] = Math.log(pts[i][0]);
			
			System.out.println(log_x[i] + "," + log_y[i]);
		}
		
		SimpleRegression model = new SimpleRegression();
		
		for (int i=0; i<pts.length;i++) {
			model.addData(log_x[i], log_y[i]);
		}
		
		System.out.println("n:" + model.getSlope());
		System.out.println("a:" + Math.exp(model.getIntercept()));
		
		//project.set_n(model.getSlope());
		//project.set_a(Math.exp(model.getIntercept()));
		//project.set_accuracy(model.getRSquare());	
		
		
		double[][] w_log_x = new double[pts.length][2];
		double[] w_log_y = new double[pts.length];
		
		for (int i=0; i < pts.length; i++) {
			w_log_y[i] = y[i]*log_y[i];
			w_log_x[i][0] = y[i]*log_x[i];
			w_log_x[i][1] = y[i];
		}
		
		OLSMultipleLinearRegression model2 = new OLSMultipleLinearRegression();
		model2.setNoIntercept(true);
		model2.newSampleData(w_log_y, w_log_x);
		
	    double[] regressionParameters = model2.estimateRegressionParameters();

		System.out.println("n:" + regressionParameters[0]);
		System.out.println("a:" + Math.exp(regressionParameters[1]));
		
		project.set_n(regressionParameters[0]);
		project.set_a(Math.exp(regressionParameters[1]));
		//project.set_accuracy(model.getRSquare());
		 
	}
	
	public static void main(String[] args) {
		double[][] pts = new double[][]{new double[]{0.2,0.5}, new double[]{0.9,0.9}, new double[]{1.5,1.1}, new double[]{2.7,1.5}, new double[]{5.4,2.0}, new double[]{92.0,7.8}};
		int[][] coords = new int[][]{new int[]{100,100}, new int[]{800,800}};
		ProjectClass project = new ProjectClass(coords);
		project.set_curve(pts);	
		setCurve(project);
		System.out.println(project.get_a() + "x^" + project.get_n() + ", accuracy:" + project.get_accuracy());
		}

}
