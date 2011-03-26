package agents;

import java.util.Set;

import retirementAge.RetirementAgeModel;
import ec.util.MersenneTwisterFast;

public class ImitatorAgent extends Agent {

	/**
	 * This is the set of agents that represents our network. I chose to use a set because: <br> 
	 * (1) It's easy to deal with duplicates <br>
	 * (2) There is hardly any other feature besides pointing to other agents in the paper
	 */
	protected Set<Agent> socialNetwork;
	
	/**
	 * This just calls the Agent() superclass constructor
	 * @param currentAge your current age
	 * @param deathTime the year you die
	 * @param randomGenerator randomizer (needed to randomly generate the network)
	 */
	public ImitatorAgent(int currentAge, int deathTime,
			MersenneTwisterFast randomGenerator) {
		//call the super constructor
		super(currentAge, deathTime);
		
		//Create a network
		fillNetwork(randomGenerator);
		
	}

	/**
	 * This is called to create the network
	 */
	protected void fillNetwork(MersenneTwisterFast gen)
	{
		//TODO do this!
	}
	
	


	/**
	 * annoying
	 */
	private static final long serialVersionUID = 1L;
	
	

	@Override
	protected Status doIRetire() {
		
		//initialize a counter for your friends that are eligible of retiring
		double friendsThatCanRetire=0;
		//initialize a counter for your friends that have retired
		double friendsThatHaveRetired = 0;
		
		//now cycle through all your friends
		for(Agent x : socialNetwork)
		{
			//if x is old enough (and he is not dead)
			if(x.getAge() >= RetirementAgeModel.retirementAge && x.getStatus() != Status.DEAD)
			{
				//x is a friend that can retire
				friendsThatCanRetire++;
				//is he actually retired?
				if(x.getStatus() == Status.RETIRED)
					//if so count that
					friendsThatHaveRetired++;
			}
		}
		
		//now check the threshold
		if(friendsThatHaveRetired / friendsThatCanRetire >= .5d)
			//if 50+% of your friends have retired, retire
			return Status.RETIRED;
		else
			//else keep working
			return Status.WORKING;
		
	}

}
