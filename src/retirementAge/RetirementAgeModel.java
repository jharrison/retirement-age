package retirementAge;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import agents.Agent;
import sim.engine.Schedule;
import sim.engine.SimState;
import sim.engine.Steppable;
import sim.engine.Stoppable;

public class RetirementAgeModel extends SimState
{
	
	/**
	 * This is the smallest possible death age for an agent
	 */
	public static int minDeathAge = 60;
	
	/**
	 * This is the the minimum age for an agent. This is the first cohort
	 */
	public static int minAge=20;
	
	/**
	 * This is the minimum retirement age
	 */
	public static int retirementAge = 65;
	
	/**
	 * This is the C of the paper: how many agents for each cohort
	 */
	public static int cohortSize = 100;
	
	/**
	 * This is the E of the paper: how many years backwards and forwards should an agent look for when creating his social network?
	 */
	public static int networkExtent = 5;
	
	/**
	 * The minimum number of friends in an agent's network
	 */
	public static int minNetworkSize = 5;
	
	/**
	 * The maximum number of friends in an agent's network
	 */
	public static int maxNetworkSize = 25;
	
	/**
	 * The approximate proportion of agents being random
	 */
	public double proportionRandom = .05; 
	
	/**
	 * The approximate proportion of agents being rational
	 */
	public double proportionRational = .15;
	
	/**
	 * This is the last cohort and also the largest possible death age
	 */
	public static int maxAge = 100;

	/**
	 * This is the matrix of agents
	 */
	public Demographics society;
	
	public RetirementAgeModel(long seed) {
		super(seed);
	}

	@Override
	public void start() {
		super.start();
		
		//let's instantiate the matrix of agents
		society = new Demographics(0, .99, random);
		
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
				for(Agent x : newCohort)
				{
					//save the Stoppable, we are going to need it 
					Stoppable stopCondition = schedule.scheduleRepeating(schedule.getTime()+1,0,x,1);
					//pass the switch condition to the agent
					x.setSwitchOff(stopCondition);
					//done!				
				}
				
				
				/**********************************
				 * PRINT OUT STUFF
				 **********************************/
				
				//print out the age matrix!
				for(int i=0; i<society.agentMatrix.length; i++)
				{
					for(int j=0; j<society.agentMatrix[i].length; j++)
					{
						System.out.print(society.agentMatrix[i][j].getStatus().toString().charAt(0));
						System.out.print(",");
					}
					System.out.println();
				}
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	
				
			
		},1);
		
		
	}

	public static void main(String[] args) {
		doLoop(RetirementAgeModel.class, args);
		System.exit(0);
	}
}
