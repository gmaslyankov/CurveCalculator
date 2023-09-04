package backend;

import org.eclipse.swt.widgets.Label;
import java.lang.Math;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import java.util.ArrayList;

public class ImageLabel{
	
	private int id = 0;
	private Display display;
	private ImageData imgData;
	private Image image;
	private Image curr_image;
	private BufferedImage original_image;
	private Label label;
	private double zoom;
	
	public ImageLabel(Display display, Shell shell, double scalling, BufferedImage image, int id) {
		
		this.id = id;
		this.original_image = resize(image, scalling);
		this.display = display;
		this.imgData = convertToSWT(original_image);
		this.image = new Image(this.display, imgData);
		this.curr_image = this.image;
		this.label = new Label(shell, SWT.WRAP | SWT.BORDER);
		this.label.setImage(this.curr_image);
		this.zoom = 1.0;
	}	
	public void reInit1() {
		this.image = new Image(this.display, this.imgData);
		this.curr_image = this.image;
		this.label.setImage(this.curr_image);
	}
	public void reInit2() {
		this.curr_image = zoom_resize(this.image, this.zoom);
		this.label.setImage(curr_image);
	}
	
	public double get_zoom() {
		return this.zoom;
	}
	
	public Label getLabel() {
		return this.label;
	}
	
	public void img_switch() {
		this.curr_image = zoom_resize(this.image, 1.0);
		this.label.setImage(curr_image);
	}
	
	public boolean zoom() {
		boolean restart;
		if (this.zoom > 2.0) {
			this.curr_image = this.image;
			this.label.setImage(this.curr_image);
			this.zoom = 1.0;
			restart = true;
		}
		else {
			this.zoom = this.zoom*1.2;
			this.curr_image = zoom_resize(this.image, this.zoom);
			this.label.setImage(this.curr_image);
			restart = false;
		}
		return restart;
	}
	public Image zoom_resize(Image input, double scalling) {
		Rectangle img_size = input.getBounds();
		int new_width = (int) (img_size.width*scalling);
		int new_height = (int) (img_size.height*scalling);
		Image output = new Image(this.display, img_size);
		GC gc = new GC(output);
		gc.drawImage(input,(int) ((new_width - img_size.width)/(2*scalling)),(int) ((new_height - img_size.height)/(2*scalling)), (int) (img_size.width/scalling), (int) (img_size.height/scalling), 0, 0, img_size.width, img_size.height);
		gc.dispose();
		return output;
	}
	
	public void drawPoint(int x, int y, int type) {
		GC gc = new GC(this.curr_image); 
		if (type == 1) {
			gc.setForeground(this.display.getSystemColor(SWT.COLOR_BLUE));
			gc.setLineWidth(4);
			//System.out.println("Drawing circle from: " + (x-5) + " , " +(y-5) + " to " + (x+5) + " , " + (y+5));
			gc.drawOval(x-5, y-5, 10, 10); 
		}
		else if (type == 2) {
			gc.setForeground(this.display.getSystemColor(SWT.COLOR_GREEN));
			gc.setLineWidth(2);
			gc.drawOval(x-2, y-2, 4, 4);
		}
		gc.dispose();
		this.label.redraw();
	}
	
	public void drawLine(int[] pt1, int[] pt2, int type) {
		GC gc = new GC(this.curr_image); 
		if (type == 1 || type == 2) {
			gc.setLineWidth(4);
			gc.setForeground(this.display.getSystemColor(SWT.COLOR_RED));
		}
		else if (type == 3) {
			gc.setLineWidth(2);
			gc.setForeground(this.display.getSystemColor(SWT.COLOR_GREEN));
		}
		if (type == 1) {
			gc.drawLine(pt1[0],pt1[1],pt2[0],pt2[1]);
		}
		else if (type == 2 || type == 3) {
			Rectangle rect = this.image.getBounds();
			int height = rect.height;
			int width = rect.width;
			//Note: we use y = mx + b formula for linear function
			if ((pt1[0] - pt2[0]) != 0) {
				double m =  ((double) (pt1[1] - pt2[1])) / ((double) (pt1[0] - pt2[0]));
				double b = ((double) pt1[1]) - m*((double) pt1[0]);
				//System.out.println(m);
				//System.out.println(b);
				int x1 = (int) ((-b)/m);
				//System.out.println(x1);
				int y1 = (int) b; 
				//System.out.println(y1);
				int x2 = (int) ((height - b)/m);
				//System.out.println(x2);
				int y2 = (int) (m*width + b);
				//System.out.println(y2);
				if (x1 < 0) {
					if (x2 > width) {
						gc.drawLine(0, y1, width, y2);
					}
					else {
						gc.drawLine(0, y1, x2, height);
					}
				}
				else if (x1 > width) {
					if (x2 < 0) {
						gc.drawLine(width, y2, 0, y1);
					}
					else {
						gc.drawLine(width, y2, x2, height);
					}
				}
				else {
					if (x2 < 0) {
						gc.drawLine(x1, 0, 0, y1);
					}
					else if (x2 > width) {
						gc.drawLine(x1, 0, width, y2);
					}
					else {
						gc.drawLine(x1, 0, x2, height);	
					}
				}
				
				
			}
			else {
				int x1 = pt1[0];
				int y1 = 0; 
				int x2 = pt2[0];
				int y2 = height;
				gc.drawLine(x1, y1, x2, y2);
			}
		}
		gc.dispose();
		this.label.redraw();
	}
	
	public void drawCurve(ProjectClass project) {
		System.out.println("Drawing curve:");
		int[] origin = project.get_origin();
		double n = project.get_n();
		double a = project.get_a();
		int direction = project.get_direction();
		double ratio = project.get_ratio()*project.get_ratio_scale();
		int distance = 1;
		double[][] matrix = project.get_matrix();
		double[][] inv_matrix = project.get_inv_matrix();
		if (direction == 1) {
			for (int i = 0; i < 700; i++) {
				int x = (int) (i*distance);
				int y = (int) (((a*(Math.pow(x, n))))*Math.pow(ratio, 1-n));
				// this.drawPoint(x, y, 1);
				int new_x = (int) (x*matrix[0][0] + y*matrix[0][1]);
				int new_y = (int) (x*matrix[1][0] + y*matrix[1][1]);
				// this.drawPoint(new_x, new_y, 2);
				new_x = origin[0] + new_x;
				new_y = origin[1] - new_y;
				this.drawPoint(new_x, new_y, 2);
				
				// new_x = origin[0] + x;
				// new_y = origin[1] + y;
				// this.drawPoint(new_x, new_y, 2);
				// System.out.println("New point! first " + x + " , " + y);
			}
		}
		else if (direction == -1) {
			for (int i = 0; i < 450; i++) {
				int x = (int) (i*distance);
				int y = (int) (((a*(Math.pow(x, n))))*Math.pow(ratio,1 - n));
				// this.drawPoint(x, y, 1);
				int new_x = (int) (x*inv_matrix[0][0] + y*inv_matrix[0][1]);
				int new_y = (int) (x*inv_matrix[1][0] + y*inv_matrix[1][1]);
				// this.drawPoint(new_x, new_y, 2);
				new_x = origin[0] - new_x;
				new_y = origin[1] - new_y;
				this.drawPoint(new_x, new_y, 2);
				// 
				// new_x = origin[0] + x;
				// new_y = origin[1] + y;
				// this.drawPoint(new_x, new_y, 2);
				// System.out.println("New point! second " + x + " , " + y);
			}
		}
	}
	
	public void drawGrid(int[] pt1, int[] pt2 ,int spacing) {
		Rectangle rect = this.image.getBounds();
		int height = rect.height;
		int width = rect.width;
		int vertical_lines = height / spacing;
		int horizontal_lines = width / spacing;
		
		if ((pt1[0] - pt2[0]) != 0) {
			double m =  ((double) (pt1[1] - pt2[1])) / ((double) (pt1[0] - pt2[0]));
			double inverse_m = (-1)/m;
			
			double x_change_for_vertical = Math.sqrt((double)(spacing*spacing)/(1+inverse_m*inverse_m));
			double y_change_for_vertical = x_change_for_vertical*inverse_m;
			
			for (int j=0; j < vertical_lines; j++) {
				int[] new_pt1 = {pt1[0] + (int) (x_change_for_vertical*j), pt1[1] + (int) (y_change_for_vertical*j)};
				int[] new_pt2 = {pt2[0] + (int) (x_change_for_vertical*j), pt2[1] + (int) (y_change_for_vertical*j)};
				this.drawLine(new_pt1, new_pt2, 3);		
			}
			
			for (int j=0; j < vertical_lines; j++) {
				int[] new_pt1 = {pt1[0] - (int) (x_change_for_vertical*j), pt1[1] - (int) (y_change_for_vertical*j)};
				int[] new_pt2 = {pt2[0] - (int) (x_change_for_vertical*j), pt2[1] - (int) (y_change_for_vertical*j)};
				this.drawLine(new_pt1, new_pt2, 3);	
			}
			
			int center_x = (int) ((pt1[0] + pt2[0])/2);
			int center_y = (int) ((pt1[1] + pt2[1])/2);
			int second_x = center_x + 100;
			int second_y = center_y + (int) (100*inverse_m);
			
			double x_change_for_horizontal = Math.sqrt((double)(spacing*spacing)/(1+m*m));
			double y_change_for_horizontal = x_change_for_horizontal*m;
			
			for (int j=0; j < horizontal_lines; j++) {
				int[] new_pt1 = {center_x + (int) (x_change_for_horizontal*j), center_y + (int) (y_change_for_horizontal*j)};
				int[] new_pt2 = {second_x + (int) (x_change_for_horizontal*j), second_y + (int) (y_change_for_horizontal*j)};
				this.drawLine(new_pt1, new_pt2, 3);		
			}
			
			for (int j=0; j < horizontal_lines; j++) {
				int[] new_pt1 = {center_x - (int) (x_change_for_horizontal*j), center_y - (int) (y_change_for_horizontal*j)};
				int[] new_pt2 = {second_x - (int) (x_change_for_horizontal*j), second_y - (int) (y_change_for_horizontal*j)};
				this.drawLine(new_pt1, new_pt2, 3);		
			}
		}
		else {
			
			for (int j=0; j < vertical_lines; j++) {
				int[] new_pt1 = {pt1[0] + spacing*j, pt1[1]};
				int[] new_pt2 = {pt2[0] + spacing*j, pt2[1]};
				this.drawLine(new_pt1, new_pt2, 3);		
			}
			
			for (int j=0; j < vertical_lines; j++) {
				int[] new_pt1 = {pt1[0] - spacing*j, pt1[1]};
				int[] new_pt2 = {pt2[0] - spacing*j, pt2[1]};
				this.drawLine(new_pt1, new_pt2, 3);	
			}
			
			int center_x = (int) ((pt1[0] + pt2[0])/2);
			int center_y = (int) ((pt1[1] + pt2[1])/2);
			int second_x = center_x + 100;
			int second_y = center_y;
			
			for (int j=0; j < horizontal_lines; j++) {
				int[] new_pt1 = {center_x, center_y + spacing*j};
				int[] new_pt2 = {second_x, second_y  + spacing*j};
				this.drawLine(new_pt1, new_pt2, 3);		
			}
			
			for (int j=0; j < horizontal_lines; j++) {
				int[] new_pt1 = {center_x, center_y - spacing*j};
				int[] new_pt2 = {second_x, second_y - spacing*j};
				this.drawLine(new_pt1, new_pt2, 3);	
			}
		}
	}
	
	public static BufferedImage resize(BufferedImage input, double scalling) {
		int new_width = (int) (input.getWidth()*scalling);
		int new_height = (int) (input.getHeight()*scalling);
		BufferedImage output = new BufferedImage(new_width, new_height ,input.getType());
		Graphics2D g2d = output.createGraphics();
		g2d.drawImage(input, 0, 0, new_width, new_height, null); //new_width, new_height, null);
		g2d.dispose();
		
		return output;
	}
	
	public static ImageData convertToSWT(BufferedImage bufferedImage) {
	    if (bufferedImage.getColorModel() instanceof DirectColorModel) {
	        DirectColorModel colorModel = (DirectColorModel) bufferedImage.getColorModel();
	        PaletteData palette = new PaletteData(
	            colorModel.getRedMask(),
	            colorModel.getGreenMask(),
	            colorModel.getBlueMask()
	        );
	        ImageData data = new ImageData(
	            bufferedImage.getWidth(),
	            bufferedImage.getHeight(), colorModel.getPixelSize(),
	            palette
	        );
	        WritableRaster raster = bufferedImage.getRaster();
	        int[] pixelArray = new int[3];
	        for (int y = 0; y < data.height; y++) {
	            for (int x = 0; x < data.width; x++) {
	                raster.getPixel(x, y, pixelArray);
	                int pixel = palette.getPixel(
	                    new RGB(pixelArray[0], pixelArray[1], pixelArray[2])
	                );
	                data.setPixel(x, y, pixel);
	            }
	        }
	        return data;
	    } else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
	        IndexColorModel colorModel = (IndexColorModel) bufferedImage.getColorModel();
	        int size = colorModel.getMapSize();
	        byte[] reds = new byte[size];
	        byte[] greens = new byte[size];
	        byte[] blues = new byte[size];
	        colorModel.getReds(reds);
	        colorModel.getGreens(greens);
	        colorModel.getBlues(blues);
	        RGB[] rgbs = new RGB[size];
	        for (int i = 0; i < rgbs.length; i++) {
	            rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
	        }
	        PaletteData palette = new PaletteData(rgbs);
	        ImageData data = new ImageData(
	            bufferedImage.getWidth(),
	            bufferedImage.getHeight(),
	            colorModel.getPixelSize(),
	            palette
	        );
	        data.transparentPixel = colorModel.getTransparentPixel();
	        WritableRaster raster = bufferedImage.getRaster();
	        int[] pixelArray = new int[1];
	        for (int y = 0; y < data.height; y++) {
	            for (int x = 0; x < data.width; x++) {
	                raster.getPixel(x, y, pixelArray);
	                data.setPixel(x, y, pixelArray[0]);
	            }
	        }
	        return data;
	    }
	    return null;
	}
	
}
