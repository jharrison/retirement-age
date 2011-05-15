package retirementAge;

import java.awt.Color;

import javax.swing.JFrame;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.LocationWrapper;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import agents.Agent;
import agents.AgentPortrayal2D;

/**
 * GUI for Axtell's & Epstein's retirement age model.
 * 
 * @author jharrison
 *
 */
@SuppressWarnings("serial")
public class RetirementAgeModelWithUI extends GUIState
{
	public Display2D display;
	public JFrame displayFrame;

	// Custom portrayal that allows us to select agents so they can draw their social network
	ObjectGridPortrayal2D agentPortrayal = new ObjectGridPortrayal2D() {
		@Override
		public boolean setSelected(LocationWrapper wrapper, boolean selected) {
			if (wrapper == null) return true;
			if (wrapper.getFieldPortrayal() != this) return true;

			Object obj = wrapper.getObject();
			if (obj instanceof Agent) ((Agent)obj).setSelected(selected);

			return super.setSelected(wrapper, selected);
		}
	};

	public RetirementAgeModelWithUI(SimState state) {
		super(state);
	}
	
	public RetirementAgeModelWithUI() {
    	super(new RetirementAgeModel(System.currentTimeMillis())); 
	}

    public static String getName() {
    	return "Retirement Age Model";
	}
    
    public Object getSimulationInspectedObject() { return state; }

	public void setupPortrayals() {
		
		agentPortrayal.setPortrayalForAll(new AgentPortrayal2D());
		agentPortrayal.setField(((RetirementAgeModel)state).agents);

		display.reset();
		display.repaint();
	}

	@Override
	public void init(Controller c) {
		super.init(c);

		display = new Display2D(800, 640, this, 1);
													
		displayFrame = display.createFrame();
		c.registerFrame(displayFrame); 										
		displayFrame.setVisible(true);

		display.attach(agentPortrayal, "Agents");

		display.setBackdrop(Color.black);

		((Console)controller).setSize(380, 600);
	}

	@Override
	public void start() {
		super.start();
		setupPortrayals();
	}
    
    @Override
	public void finish() {
		super.finish();
	}

	public void quit() {
		super.quit();

		if (displayFrame != null) 
			displayFrame.dispose();
		displayFrame = null;
		display = null;
	}

    public static void main(String[] args) {
    	new RetirementAgeModelWithUI().createController();
    }

}
