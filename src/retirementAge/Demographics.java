/**
 * 
 */
package retirementAge;

import sim.engine.SimState;
import sim.engine.Steppable;
import ec.util.MersenneTwisterFast;
import agents.Agent;
import agents.ImitatorAgent;
import agents.RandomAgent;
import agents.RationalAgent;
import agents.Status;

/**
 * What this is going to be is just a glorified matrix of agents. <br>
 * It does have a method to fill itself/reset which is nice, I guess. <br> <br>
 * 
 * It's step makes all the cohort one year older, kills off the oldest and creates a fresh new batch of young agents
 * @author carrknight
 *
 */
public class Demographics implements Steppable{
	
	/**
	 * This is where we store all our agents
	 */
	protected Agent[][] agentMatrix;

	/**
	 * This is the constructor for the matrix population.
	 * @param proportionRandom the % of agents that will be random
	 * @param proportionRational the % of agents being rational
	 * @param random the randomizer (needed to fill the matrix)
	 */
	public Demographics(double proportionRandom, double proportionRational, MersenneTwisterFast random)
	{
		//sanity check
		if(proportionRandom + proportionRational > 1)
			//throw new exception if probabilities go above one
			throw new RuntimeException("Proportions are out of whack!");
		
		//if we are here, things are okay
		//create the agent matrix: for each allowed age add the cohort size given by the RetirementAgeModel
		agentMatrix = new Agent[RetirementAgeModel.maxAge-RetirementAgeModel.minAge + 1][RetirementAgeModel.cohortSize];

		//now fill it
		reset(proportionRandom, proportionRational,random);

	}

	/**
	 * This fills all the demographic matrix with brand new agents
	 * @param proportionRandom the % of agents that will be random
	 * @param proportionRational the % of agents being rational
	 * @param random the randomizer to fill the matrix
	 */
	public void reset(double proportionRandom, double proportionRational, MersenneTwisterFast random) {

		//for each cohort
		for(int age = 0; age<agentMatrix.length; age++)
			//for each agent in the cohort
			for(int agent = 0; agent<agentMatrix[age].length; agent++)
			{
				//draw a random number
				double pick = random.nextDouble();
				//which agent will it be?
				if(pick< proportionRandom)
					//random agent
					//with the age of the cohort and the death rate U~[min,max] and the randomizer from the SimState
					agentMatrix[age][agent] = new RandomAgent(age + RetirementAgeModel.minAge, 
							//death time
							random.nextInt(RetirementAgeModel.maxAge - RetirementAgeModel.minDeathAge)+RetirementAgeModel.minDeathAge,
							//randomizer
							random);
				else
					//which agent will it be?
					if(pick< proportionRandom+proportionRational)
					{
						//rational agent
						//with the age of the cohort and the death rate U~[min,max]
						agentMatrix[age][agent] = new RationalAgent(age + RetirementAgeModel.minAge, 
								//death time
								random.nextInt(RetirementAgeModel.maxAge - RetirementAgeModel.minDeathAge)+RetirementAgeModel.minDeathAge);
					}
				//otherwise it's an imitator!
					else
						agentMatrix[age][agent] = new ImitatorAgent(age + RetirementAgeModel.minAge, 
								//death time
								random.nextInt(RetirementAgeModel.maxAge - RetirementAgeModel.minDeathAge)+RetirementAgeModel.minDeathAge,
								//randomizer
								random,
								//a link to here
								this);
			}
		
	}

	
	/**
	 * This method returns the cohort of agents of that specific year!  
	 */
	public Agent[] getCohort(int age)
	{
		//return the link to that cohort!
		return agentMatrix[age-RetirementAgeModel.minAge];
	}

	//TODO comment about social network extent
	
	/**
	 * Makes all cohort one year older, forgets the last one and creates a new one.
	 */
	@Override
	public void step(SimState arg0) {

		//cast the argument!
		RetirementAgeModel state = (RetirementAgeModel) arg0;
		
		
		//clear off the last cohort (not sure this is needed)
		for(int i=0; i<agentMatrix[agentMatrix.length-1].length; i++)
			//making it null will surely attract the gc to the now dead agents
			agentMatrix[agentMatrix.length-1][i]=null;
		
		//now go through all the cohorts and make them point to the previous generation
		//do this in reverse order
		for(int i=agentMatrix.length-1; i>0; i--)
			agentMatrix[i] = agentMatrix[i-1];
		
		//now make the first cohort point to a new cohort
		agentMatrix[0] = new Agent[RetirementAgeModel.cohortSize];
		
		//fill in the first cohort
		for(int i=0; i < agentMatrix[0].length; i++)
		{
			
			//this is just a copy and paste from the reset function
			
			//draw a random number
			double pick = state.random.nextDouble();
			//which agent will it be?
			if(pick< state.proportionRandom)
				//random agent
				//with the age of the cohort and the death rate U~[min,max] and the randomizer from the SimState
				agentMatrix[0][i] = new RandomAgent(RetirementAgeModel.minAge, 
						//death time
						state.random.nextInt(RetirementAgeModel.maxAge - RetirementAgeModel.minDeathAge)+RetirementAgeModel.minDeathAge,
						//randomizer
						state.random);
			else
				//which agent will it be?
				if(pick< state.proportionRandom+state.proportionRational)
				{
					//rational agent
					//with the age of the cohort and the death rate U~[min,max]
					agentMatrix[0][i] = new RationalAgent(RetirementAgeModel.minAge, 
							//death time
							state.random.nextInt(RetirementAgeModel.maxAge - RetirementAgeModel.minDeathAge)+RetirementAgeModel.minDeathAge);
				}
			//otherwise it's an imitator!
				else
					agentMatrix[0][i] = new ImitatorAgent( RetirementAgeModel.minAge, 
							//death time
							state.random.nextInt(RetirementAgeModel.maxAge - RetirementAgeModel.minDeathAge)+RetirementAgeModel.minDeathAge,
							//randomizer
							state.random,
							//a link to here
							this);
		}
		
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
