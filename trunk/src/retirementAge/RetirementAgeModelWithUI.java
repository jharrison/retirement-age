package retirementAge;

import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JFrame;

import portrayals.AgentGridPortrayal2D;
import portrayals.AgentPortrayal2D;

import sim.display.Console;
import sim.display.Controller;
import sim.display.Display2D;
import sim.display.GUIState;
import sim.engine.SimState;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.LocationWrapper;
import sim.portrayal.grid.ObjectGridPortrayal2D;
import sim.util.Bag;
import agents.Agent;

/**
 * GUI for Axtell's & Epstein's retirement age model.
 * 
 * @author jharrison
 *
 */
public class RetirementAgeModelWithUI extends GUIState
{
	public Display2D display;
	public JFrame displayFrame;
	AgentGridPortrayal2D agentPortrayal = new AgentGridPortrayal2D();

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
