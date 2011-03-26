package agents;

import java.util.Set;

import ec.util.MersenneTwisterFast;

import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;


/**
 * This is going to be the abstract superclass of all our agents. It contains all the variable (protected) of the agents in the paper 
 * but none of the actual decision routine.
 * @author carrknight
 *
 */
public abstract class Agent implements Steppable {

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
	 * This is the death age of the agent, that is the time it dies.
	 */
	protected int deathAge;
	
	
	/**
	 * This is just a simple constructor that sets one's age and time to die. Because the class is abstract this gets called only by
	 * inheritors. It's here to make sure we set the age, deathage and start the Agent as alive <br>
	 * @param age your current age
	 * @param deathAge your death age
	 */
	public Agent(int currentAge, int deathTime){
		//assign the age
		age = currentAge;
		//assing death time
		deathAge = deathTime;
		
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
			status = Status.DEAD;
			//Quit the schedule!
			switchOff.stop();
		}
		
		//(3) otherwise, check if you want to retire!
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


	/**
	 * @return the switchOff
	 */
	public Stoppable getSwitchOff() {
		return switchOff;
	}


	/**
	 * @param switchOff the switchOff to set
	 */
	public void setSwitchOff(Stoppable switchOff) {
		this.switchOff = switchOff;
	}

	
	
	
	
	
}
