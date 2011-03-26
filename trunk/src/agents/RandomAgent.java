package agents;

import retirementAge.RetirementAgeModel;
import ec.util.MersenneTwisterFast;

public class RandomAgent extends Agent {

	/**
	 * This is the random generator, it should just be linked to the model one so we can control the seed
	 */
	private MersenneTwisterFast gen;
	
	/**
	 * On top of the Agent() constructor this also stores the link to the randomizer because it needs to use it during its decision making
	 * @param currentAge the agent's current age
	 * @param deathTime the time the agent will die
	 */
	public RandomAgent(int currentAge, int deathTime, MersenneTwisterFast random) {
		//call papa's constructor
		super(currentAge, deathTime,random);
		//link up the random generator
		gen = random;
		
	}

	/**
	 * annoying
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This agent, once it is available to him that he can retire, he will throw a coin and decides.
	 */
	@Override
	protected Status doIRetire() {

		//if you are not allowed to retire, don't
		if(age < RetirementAgeModel.retirementAge)
			//don't retire
			return Status.WORKING;
		//otherwise
		else{
			//throw a coin
			if(gen.nextBoolean())
				//50% you retire
				return Status.RETIRED;
			//50% you don't
			return Status.WORKING;
		}

	}

}
