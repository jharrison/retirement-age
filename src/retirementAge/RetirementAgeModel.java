package retirementAge;

import java.util.ArrayList;

import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;
import sim.field.grid.ObjectGrid2D;
import sim.util.Interval;
import agents.Agent;
import agents.Status;

public class RetirementAgeModel extends SimState
{
	private static final long serialVersionUID = 1L;

	/**
	 * This is the smallest possible death age for an agent
	 */
	public int minDeathAge = 60;
	public int getMinDeathAge() { return minDeathAge; }
	public void setMinDeathAge(int val) { minDeathAge = val; }
    public Object domMinDeathAge() { return new Interval(50, 100); }
	
	/**
	 * This is the the minimum age for an agent. This is the first cohort
	 */
	public int minAge=20;
	public int getMinAge() { return minAge; }
	public void setMinAge(int val) { minAge = val; }
    public Object domMinAge() { return new Interval(0, 40); }
	
	/**
	 * This is the last cohort and also the largest possible death age
	 */
	public int maxAge = 100;
	public int getMaxAge() { return maxAge; }
	public void setMaxAge(int val) { maxAge = val; }
    public Object domMaxAge() { return new Interval(0, 100); }

	/**
	 * Minimum age to display in the GUI
	 */
	public int minDisplayAge = 55;	
	public int getMinDisplayAge() { return minDisplayAge; }
	public void setMinDisplayAge(int val) { minDisplayAge = val; }
    public Object domMinDisplayAge() { return new Interval(20, 60); }
	
	/**
	 * This is the minimum retirement age
	 */
	public int retirementAge = 65;
	public int getRetirementAge() { return retirementAge; }
	public void setRetirementAge(int val) { retirementAge = val; }
    public Object domRetirementAge() { return new Interval(50, 75); }
	
	/**
	 * This is the C of the paper: how many agents for each cohort
	 */
	public int cohortSize = 100;
	public int getCohortSize() { return cohortSize; }
	public void setCohortSize(int val) { cohortSize = val; }
    public Object domCohortSize() { return new Interval(60, 200); }
	
	/**
	 * This is the E of the paper: how many years backwards and forwards should an agent look for when creating his social network?
	 */
	public int networkExtent = 5;
	public int getNetworkExtent() { return networkExtent; }
	public void setNetworkExtent(int val) { networkExtent = val; }
    public Object domNetworkExtent() { return new Interval(0, 10); }
	
	/**
	 * The minimum number of friends in an agent's network
	 */
	public int minNetworkSize = 5;
	public int getMinNetworkSize() { return minNetworkSize; }
	public void setMinNetworkSize(int val) { minNetworkSize = val; }
    public Object domMinNetworkSize() { return new Interval(0, 20); }
	
	/**
	 * The maximum number of friends in an agent's network
	 */
	public int maxNetworkSize = 25;
	public int getMaxNetworkSize() { return maxNetworkSize; }
	public void setMaxNetworkSize(int val) { maxNetworkSize = val; }
    public Object domMaxNetworkSize() { return new Interval(20, 50); }
	
	/**
	 * The approximate proportion of agents being random
	 */
	public double proportionRandom = .05; 
	public double getProportionRandom() { return proportionRandom; }
	public void setProportionRandom(double val) { proportionRandom = val; }
    public Object domProportionRandom() { return new Interval(0.0, 1.0); }
	
	/**
	 * The approximate proportion of agents being rational
	 */
	public double proportionRational = .15;
	public double getProportionRational() { return proportionRational; }
	public void setProportionRational(double val) { proportionRational = val; }
    public Object domProportionRational() { return new Interval(0.0, 1.0); }
    
    public double randomAgentRetirementProb = 0.5;
	public double getRandomAgentRetirementProb() { return randomAgentRetirementProb; }
	public void setRandomAgentRetirementProb(double val) { randomAgentRetirementProb = val; }
    public Object domRandomAgentRetirementProb() { return new Interval(0.0, 1.0); }
    
    public double getProportionRetired() {
    	if (society == null)
    		return 0;
    	int retiredCount = 0, eligibleCount = 0;
		ArrayList<Agent> allAgents = society.getAllAgents();
		for (Agent a : allAgents)
			if ((a.getAge() >= retirementAge) && (a.getStatus() != Status.DEAD)) {
				eligibleCount++;
				if (a.getStatus() == Status.RETIRED)
					retiredCount++;
			}
		
		return retiredCount / (double)eligibleCount;
    }
	
	/**
	 * 2D Grid containing the agents. Note that the agents are also held in the demographics class
	 * in the agentMatrix, which is where they should be modified.
	 */
	public ObjectGrid2D agents;
		

	/**
	 * This is the matrix of agents
	 */
	public Demographics society;
	
	public RetirementAgeModel(long seed) {
		super(seed);
	}
	
	public void init() {
		int numCohorts = maxAge - minDisplayAge + 1;
		agents = new ObjectGrid2D(cohortSize, numCohorts);
	}
	
	@SuppressWarnings("serial")
	@Override
	public void start() {
		super.start();
		init();
		
		//let's instantiate the matrix of agents
		society = new Demographics(this);
		society.initAgents();
		
		//now we need to cycle through all the agents in society and tell the schedule about them
		//get all the agents
		ArrayList<Agent> initialAgents = society.getAllAgents();
		//cycle through them
		for(Agent x : initialAgents)
		{
			//first let's schedule them (order 0)
			//save the Stoppable, we are going to need it 
			Stoppable stopCondition = schedule.scheduleRepeating(Schedule.EPOCH,0,x,1);
			//pass the switch condition to the agent
			x.setSwitchOff(stopCondition);
			//done!
		}
		
		//now we should schedule the society to step (this updates the matrix)
		schedule.scheduleRepeating(Schedule.EPOCH,1,society,1);
		
		//now, each turn a new set of agents gets created and we need to schedule them. 
		//We are going to create a steppable that each turn schedules the new cohort 
		//Also, as long as we don't have a GUI, let's print out stuff here
		schedule.scheduleRepeating(Schedule.EPOCH,2,new Steppable(){
			
			//this'll be an anonymous implementation
			public void step(SimState arg0) {
				//get the new guys
				Agent[] newCohort = society.getCohort(minAge);
				//schedule them
				for(Agent a : newCohort)
				{
					//save the Stoppable, we are going to need it 
					Stoppable stopCondition = schedule.scheduleRepeating(schedule.getTime()+1,0,a,1);
					//pass the switch condition to the agent
					a.setSwitchOff(stopCondition);
					//done!				
				}
				
				
				/**********************************
				 * PRINT OUT STUFF
				 **********************************/
				
				//print out the age matrix!
//				for(int i=0; i < agents.field.length; i++)
//				{
//					for(int j=0; j < agents.field[i].length; j++)
//					{
//						System.out.print(((Agent)agents.field[i][j]).getStatus().toString().charAt(0));
//						System.out.print(",");
//					}
//					System.out.println();
//				}
//				try {
//					TimeUnit.SECONDS.sleep(1);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		});		
	}

	public static void main(String[] args) {
		doLoop(RetirementAgeModel.class, args);
		System.exit(0);
	}
}
