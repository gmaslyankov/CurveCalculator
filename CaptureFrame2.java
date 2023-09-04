package backend;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.eclipse.swt.SWT;

public class CaptureFrame2 {
	
	private ProjectClass project;
	private PointCapture listener;
	private int[][] coords;
	private boolean ptPressed = false;
	private boolean firstPressed = false;
	private boolean secondPressed = false;
	private Button ptCapture;
	private Button firstImage;
	private Button secondImage;
	private Button ready;
	private Label instructions;
	
	public CaptureFrame2() {
		
		final Display display = new Display();
		
		final CaptureFrame2 app = this;
		
		try {
			Shell shell = new Shell(display);
			
			shell.setLayout(new FillLayout());
			shell.setSize(1000, 450);
			
			this.ptCapture = new Button(shell, SWT.PUSH);
			this.ptCapture.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					System.out.println("Pt Capture Selected beuh");
				}
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println("Pt Capture Selected");
					if (!ptPressed) {
						ptPressed = true;
						listener = new PointCapture(new int[2][2]);
						app.instructions.setText("Select two points on the screen. After selecting the two points, make sure that the area of interest is showing a sagittal cut of the tibial bone at the center of the epicondylar axis. Then click on [Take first image] to take a first screenshot.");
						ptCapture.setText("Erase selection");
					}
					else {
						app.ptPressed = false;
						app.listener = null;
						app.instructions.setText("Start by pressing the [Points for Screenshot] button and then select two points on your screen that will determine the area that will be used for the screenshots. For example, select the top-left and bottom-right corners of the area. Ideally, use an area that is close in shape to a square.");
						app.coords = null;
						app.project = null;
						app.firstPressed = false;
						app.secondPressed = false;
						ptCapture.setText("Points for screenshots");
					}
				}
			});
			this.ptCapture.setText("Points for screenshots");
			
			this.firstImage = new Button(shell, SWT.PUSH);
			this.firstImage.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					System.out.println("First Image Selected beuh");
					
				}
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println("First Image Selected");
					if (ptPressed && !firstPressed && listener.get_final_list() != null) {
						firstPressed = true;
						coords = listener.get_final_list();
						project = new ProjectClass(coords);
						project.initImages(1);
						app.instructions.setText("The first screenshot was taken. If you made a mistake, you can take it again by clicking on the same button. If are happy with the first screenshot, make sure that the area of interest is showing a sagittal cut of the tibial bone at the midpoint between the center of the epicondylar axis and the lateral aspect of the tibial plateau. Press on [Take second image] to take the second screenshot.");
					}	
					else if (firstPressed) {
						project.initImages(1);
					}
				}
			});
			this.firstImage.setText("Take first image");
			this.secondImage = new Button(shell, SWT.PUSH);
			this.secondImage.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					System.out.println("Second Image Selected beuh");
				}
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println("Second image Selected");
					if (firstPressed && !secondPressed) {
						secondPressed = true;
						project.initImages(2);
						app.instructions.setText("The second screenshot was taken. If you made a mistake, you can take it again by clicking on the same button. If are happy with the second screenshot and ready to go to the next step, click on the [Ready] button.");

					}
					else if (secondPressed) {
						project.initImages(2);
					}
				}
			});
			this.secondImage.setText("Take second image");
			
			this.ready = new Button(shell, SWT.PUSH);
			this.ready.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					System.out.println("Ready Selected beuh");
					
				}
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println("Ready Selected");
					if (secondPressed) {
						display.dispose();
						ImageFrame2 imgFrame = new ImageFrame2(project);
					}
				}
			});
			this.ready.setText("Ready");
			
			this.instructions = new Label(shell, SWT.WRAP | SWT.BORDER);
			this.instructions.setText("Start by pressing the [Points for Screenshot] button and then select two points on your screen that will determine the area that will be used for the screenshots. For example, select the top-left and bottom-right corners of the area. Ideally, use an area that is close in shape to a square.");
	
			try {
				shell.open();
				while(!shell.isDisposed()) {
					if(!display.readAndDispatch()) {
						display.sleep();
					}
				}
			} finally {
				if (!shell.isDisposed()) {
					shell.dispose();
				}
			}
		} finally {		
		display.dispose();
		}
	}
	
	public static void main(String[] args) {
		CaptureFrame2 cap = new CaptureFrame2();
	}
	
	
}
