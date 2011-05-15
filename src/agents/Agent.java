package agents;

import retirementAge.RetirementAgeModel;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.util.Valuable;


/**
 * This is going to be the abstract superclass of all our agents. It contains all the variable (protected) of the agents in the paper 
 * but none of the actual decision routine.
 * @author carrknight
 *
 */
public abstract class Agent implements Steppable, Valuable {

	/**
	 * Reference to the containing model.
	 */
	protected RetirementAgeModel model;
	
	/**
	 * Now, the twist of this model is that agents die off, but the problem is that they are still cluttering the schedule because we do
	 * schedule them as repeating. This is a big problem if the simulation will run for a long time. <br>
	 * Now the nice thing is that when we call scheduleRepeating we get a Stoppable we can call to tell the schedule to stop making this agent step. <br>
	 * The problem with it is that the scheduleRepeating must be called AFTER we instantiate this agent. So we need to make sure that after we have scheduled this we call 
	 * setSwitchOff to tell this Agent what is its switch off button
	 */
	private Stoppable switchOff;
	
	
	/**
	 * Annoying serial 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This is the status of the agent. We are going to change this at the step() given whatever doIRetire() tells us
	 * @see doIRetire
	 */
	protected Status status ;
	
	/**
	 * This is the age of the agent
	 */
	protected int age;
	
	/**
	 * This is the age at which the agent will die
	 */
	protected int deathAge;
	
	/**
	 * This is used for drawing the network of the selected agent
	 */
	public boolean showInNetwork = false;
	
	/**
	 * This is used for drawing the selected agent
	 */
	public boolean selected = false;
	public void setSelected(boolean val) {
		selected = val;
	}
	
	
	/**
	 * This is just a simple constructor that sets one's age and time to die. Because the class is abstract this gets called only by
	 * inheritors. It's here to make sure we set the age, deathage and start the Agent as alive <br>
	 * @param age your current age
	 * @param deathAge your death age
	 * @param model reference to the main model
	 */
	public Agent(int currentAge, int deathAge, RetirementAgeModel model){
		//assign the age
		age = currentAge;
		//assing death time
		this.deathAge = deathAge;
		
		this.model = model;
		
		//start alive
		status = Status.WORKING;
	}
	
	
	/**
	 * Three things happen in the step() function: <br>
	 * (1) The agent ages <br>
	 * (2) If death age is reached you are dead. <br>
	 * (3) If not, check if you retire
	 */
	@Override
	public void step(SimState arg0) {
		
		//(1) the character ages
		age++;

		//(2) check if it's your turn to die
		if(age >= deathAge){
			//you will be missed
			this.die();
			//Quit the schedule!
			switchOff.stop();
			//stop linking to the switchoff
			switchOff = null;
		}
		else if (status == Status.WORKING && age>= model.retirementAge)	//(3) otherwise, check if you want to retire!
			status = doIRetire();	
	}

	/**
	 * This is the main method to override. Here we should contain all our decision making. <br>
	 * @return you either return WORKING or RETIRED. please don't return DEAD
	 */
	abstract protected Status doIRetire();

	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	/**
	 * @return the age
	 */
	public int getAge() {
		return age;
	}

	/**
	 * @return the deathAge
	 */
	public int getDeathAge() {
		return deathAge;
	}

	// I commented this out so it doesn't show in the inspector
//	/**
//	 * @return the switchOff
//	 */
//	public Stoppable getSwitchOff() {
//		return switchOff;
//	}
//
	/**
	 * @param switchOff the switchOff to set
	 */
	public void setSwitchOff(Stoppable switchOff) {
		this.switchOff = switchOff;
	}
	
	/**
	 * just changes the status to dead, but can be overriden.
	 */
	protected void die(){
		status = Status.DEAD;
	}
}
