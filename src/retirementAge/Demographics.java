/**
 * 
 */
package retirementAge;

import java.util.ArrayList;

import sim.engine.SimState;
import sim.engine.Steppable;
import agents.Agent;
import agents.ImitatorAgent;
import agents.RandomAgent;
import agents.RationalAgent;

/**
 * What this is going to be is just a glorified matrix of agents. <br>
 * It does have a method to fill itself/reset which is nice, I guess. <br> <br>
 * 
 * It's step makes all the cohort one year older, kills off the oldest and creates a fresh new batch of young agents
 * @author carrknight
 *
 */
public class Demographics implements Steppable{
	private static final long serialVersionUID = 1L;
	
	/**
	 * This is where we store all our agents
	 */
	protected Agent[][] agentMatrix;
	private RetirementAgeModel model;

	/**
	 * This is the constructor for the matrix population.
	 * @param proportionRandom the % of agents that will be random
	 * @param proportionRational the % of agents being rational
	 * @param random the randomizer (needed to fill the matrix)
	 */
	public Demographics(RetirementAgeModel model)
	{
		//sanity check
		if(model.proportionRandom + model.proportionRational > 1)
			//throw new exception if probabilities go above one
			throw new RuntimeException("Proportions are out of whack!");
		
		//if we are here, things are okay
		
		this.model = model;

		//create the agent matrix: for each allowed age add the cohort size given by the RetirementAgeModel
		agentMatrix = new Agent[model.maxAge-model.minAge + 1][model.cohortSize];
	}
	
	private Agent createNewAgent(int age) {
		double pick = model.random.nextDouble();
		// the death rate is drawn from U~[min,max] 
		int deathAge = model.random.nextInt(model.maxAge - model.minDeathAge) + model.minDeathAge;
		Agent agent;
		
		//which agent will it be?
		if (pick < model.proportionRandom)
			agent = new RandomAgent(age, deathAge, model);
		else if (pick < model.proportionRandom+model.proportionRational)
			agent = new RationalAgent(age, deathAge, model);
		else
			agent = new ImitatorAgent(age, deathAge, model);
		
		return agent;
	}

	/**
	 * This fills all the demographic matrix with brand new agents
	 * @param proportionRandom the % of agents that will be random
	 * @param proportionRational the % of agents being rational
	 * @param random the randomizer to fill the matrix
	 */
	public void initAgents() {

		//for each cohort
		for(int age = 0; age < agentMatrix.length; age++)
			//for each agent in the cohort
			for(int agent = 0; agent < agentMatrix[age].length; agent++)
				agentMatrix[age][agent] = createNewAgent(model.minAge + age);	
		
		// initialize the social networks
		//for each cohort
		for(int age = 0; age < agentMatrix.length; age++)
			//for each agent in the cohort
			for(int agent = 0; agent < agentMatrix[age].length; agent++)
				if (agentMatrix[age][agent] instanceof ImitatorAgent) {
					ImitatorAgent a = (ImitatorAgent)agentMatrix[age][agent];
					if (a.getAge() >= (model.minAge + model.networkAgeExtent))
						a.fillNetwork();
				}
		
		copyToGrid();
	}
	
	/**
	 * Copy the visible portion of the agent matrix to the agent grid in the model.
	 * The grid in the model will actually be displayed.
	 */
	private void copyToGrid() {
		// Copy the agentMatrix to the ObjectGrid2D, in transposed order.
		// This is to make them scroll top to bottom instead of left to right.
		int ageGap = model.minDisplayAge - model.minAge;
		for (int age = ageGap; age < agentMatrix.length; age++)
			for (int j = 0; j < agentMatrix[0].length; j++)
				model.agents.field[j][age-ageGap] = agentMatrix[age][j];
	}
	
	/**
	 * This method returns the cohort of agents of that specific year!  
	 */
	public Agent[] getCohort(int age)
	{
		//return the link to that cohort!
		return agentMatrix[age-model.minAge];
	}
	
	/**
	 * Makes all cohort one year older, forgets the last one and creates a new one.
	 */
	@Override
	public void step(SimState arg0) {
		//clear off the last cohort (not sure this is needed)
		for (int i = 0; i < agentMatrix[agentMatrix.length-1].length; i++)
			//making it null will surely attract the gc to the now dead agents
			agentMatrix[agentMatrix.length-1][i]=null;
		
		//now go through all the cohorts and make them point to the previous generation
		//do this in reverse order
		for (int i = agentMatrix.length-1; i > 0; i--)
			agentMatrix[i] = agentMatrix[i-1];
		
		//now make the first cohort point to a new cohort
		agentMatrix[0] = new Agent[model.cohortSize];
		
		//fill in the first cohort
		for (int i = 0; i < agentMatrix[0].length; i++)
			agentMatrix[0][i] = createNewAgent(model.minAge);

		copyToGrid();
	}

	/**
	 * Return a list containing ALL the agents in the matrix. This is used by the SimState to schedule all the agents.
	 * @return
	 */
	public ArrayList<Agent> getAllAgents()
	{
		ArrayList<Agent> list = new ArrayList<Agent>();
		// loop through each agent in each cohort and add them
		for (Agent[] cohort : agentMatrix)
			for (Agent x : cohort)
				list.add(x);
		
		return list;
	}	
	
	/*
	
//this is a test
	public static void mainAge(String[] args)
	{
		//randomizer
		MersenneTwisterFast random = new MersenneTwisterFast(0l);
		//create the table!
		Demographics pippo = new Demographics(.05, .15,random );
		
		//print out the age matrix!
		for(int i=0; i<pippo.agentMatrix.length; i++)
		{
			for(int j=0; j<pippo.agentMatrix[i].length; j++)
			{
				System.out.print(pippo.agentMatrix[i][j].getAge());
				System.out.print(",");
			}
			System.out.println();
		}
		//step
		pippo.step(new RetirementAgeModel(0));
		System.out.println("**********************************************");
		//print out the age matrix!
		for(int i=0; i<pippo.agentMatrix.length; i++)
		{
			for(int j=0; j<pippo.agentMatrix[i].length; j++)
			{
				System.out.print(pippo.agentMatrix[i][j].getAge());
				System.out.print(",");
			}
			System.out.println();
		}
		
	}
	
	//this is a test
	public static void mainType(String[] args)
	{
		//randomizer
		MersenneTwisterFast random = new MersenneTwisterFast(0l);
		//create the table!
		Demographics pippo = new Demographics(.05, .15,random );
		
		//print out the age matrix!
		for(int i=0; i<pippo.agentMatrix.length; i++)
		{
			for(int j=0; j<pippo.agentMatrix[i].length; j++)
			{
				//print out the class name (actually just its third character: n--> random, t--> rational i---> imitator
				if(pippo.agentMatrix[i][j] instanceof RandomAgent)
					System.out.print("n");
				else if(pippo.agentMatrix[i][j] instanceof RationalAgent)
					System.out.print("t");
				else 
					System.out.print("i");
				System.out.print(",");
			}
			System.out.println();
		}
		//step
		pippo.step(new RetirementAgeModel(0));
		System.out.println("**********************************************");
		//print out the age matrix!
		for(int i=0; i<pippo.agentMatrix.length; i++)
		{
			for(int j=0; j<pippo.agentMatrix[i].length; j++)
			{
				if(pippo.agentMatrix[i][j] instanceof RandomAgent)
					System.out.print("n");
				else if(pippo.agentMatrix[i][j] instanceof RationalAgent)
					System.out.print("t");
				else 
					System.out.print("i");
				System.out.print(",");
			}
			System.out.println();
		}
		
	}
	
	//this is a test
	public static void main(String[] args)
	{
		//randomizer
		MersenneTwisterFast random = new MersenneTwisterFast(0l);
		//create the table!
		Demographics pippo = new Demographics(.05, .15,random );
		
		//print out the age matrix!
		for(int i=0; i<pippo.agentMatrix.length; i++)
		{
			for(int j=0; j<pippo.agentMatrix[i].length; j++)
			{
				System.out.print(pippo.agentMatrix[i][j].getStatus().toString().charAt(0));
				System.out.print(",");
			}
			System.out.println();
		}
		//step
		pippo.step(new RetirementAgeModel(0));
		pippo.agentMatrix[0][0].setStatus(Status.DEAD);
		System.out.println("**********************************************");
		//print out the age matrix!
		for(int i=0; i<pippo.agentMatrix.length; i++)
		{
			for(int j=0; j<pippo.agentMatrix[i].length; j++)
			{
				System.out.print(pippo.agentMatrix[i][j].getStatus().toString().charAt(0));
				System.out.print(",");
			}
			System.out.println();
		}
		
	}
	*/
}
