/**
 * 
 */
package net.java.amateras.uml.classdiagram.figure;

import net.java.amateras.uml.classdiagram.model.AggregationModel;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * @author Takahiro Shida.
 * @author Martin Georgiev
 * 
 */
public class AggregationConnectionFigure extends PolylineConnection implements PresentationFigure {
	
	private Label labelStereoType;
	private Label labelFromMultiplicity;
	private Label labelToMultiplicity;
	
	private Label labelFromName;
	private Label labelToName;
	
	public AggregationConnectionFigure(AggregationModel model) {
		labelStereoType = new Label();
		if(!model.getStereoType().equals("")){
			labelStereoType.setText("<<" + model.getStereoType() + ">>");
		}
		
		labelFromMultiplicity = new Label();
		labelFromMultiplicity.setText(model.getFromMultiplicity());
		
		labelToMultiplicity = new Label();
		labelToMultiplicity.setText(model.getToMultiplicity());
		
		labelFromName = new Label();
		labelFromName.setText(model.getSzFromLabel());
		
		labelToName = new Label();
		labelToName.setText(model.getSzToLabel());
		
		add(labelStereoType, new ConnectionLocator(this, ConnectionLocator.MIDDLE));
		
		ConnectionEndpointLocator sourceEndpointLocator = new ConnectionEndpointLocator(this, false);
		sourceEndpointLocator.setVDistance(15);
		add(labelFromMultiplicity, sourceEndpointLocator);
		
		ConnectionEndpointLocator sourceEndpointNameLocator = new ConnectionEndpointLocator(this, false);
		sourceEndpointNameLocator.setVDistance(-15);
		add(labelFromName, sourceEndpointNameLocator);
		
		ConnectionEndpointLocator targetEndpointLocator = new ConnectionEndpointLocator(this, true);
		targetEndpointLocator.setVDistance(15);
		add(labelToMultiplicity, targetEndpointLocator);
		
		ConnectionEndpointLocator targetEndpointNameLocator = new ConnectionEndpointLocator(this, true);
		targetEndpointNameLocator.setVDistance(-15);
		add(labelToName, targetEndpointNameLocator);
		
		PolygonDecoration decoration = new PolygonDecoration();
		PointList decorationPointList = new PointList();
		decorationPointList.addPoint( 0, 0);
		decorationPointList.addPoint(-2, 2);
		decorationPointList.addPoint(-4, 0);
		decorationPointList.addPoint(-2,-2);
		decoration.setTemplate(decorationPointList);
		decoration.setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		setTargetDecoration(decoration);
		
	}
	
	public void update(AggregationModel model) {
		labelFromMultiplicity.setText(model.getFromMultiplicity());
		labelToMultiplicity.setText(model.getToMultiplicity());
		labelFromName.setText(model.getSzFromLabel());
		labelToName.setText(model.getSzToLabel());
		if(!model.getStereoType().equals("")){
			labelStereoType.setText("<<" + model.getStereoType() + ">>");
		} else {
			labelStereoType.setText("");
		}		
	}
	
	public Label getStereoTypeLabel() {
		return labelStereoType;
	}
	/* (non-Javadoc)
	 * @see net.java.amateras.uml.figure.PresentationFigure#updatePresentation(net.java.amateras.uml.model.AbstractUMLModel)
	 */
	public void updatePresentation(AbstractUMLModel model) {
		labelStereoType.setForegroundColor(model.getForegroundColor());
		labelFromMultiplicity.setForegroundColor(model.getForegroundColor());
		labelToMultiplicity.setForegroundColor(model.getForegroundColor());
		labelFromName.setForegroundColor(model.getForegroundColor());
		labelToName.setForegroundColor(model.getForegroundColor());
		setForegroundColor(model.getForegroundColor());
	}
}
