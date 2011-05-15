package agents;

import retirementAge.RetirementAgeModel;

public class RandomAgent extends Agent {
	
	/**
	 * On top of the Agent() constructor this also stores the link to the SimState randomizer 
	 * because it needs to use it during its decision making
	 * @param currentAge the agent's current age
	 * @param deathTime the time the agent will die
	 * @param model reference to the main model
	 */
	public RandomAgent(int currentAge, int deathTime, RetirementAgeModel model) {
		//call papa's constructor
		super(currentAge, deathTime, model);
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
		if(age < model.retirementAge)
			//don't retire
			return Status.WORKING;
		//otherwise
		else{
			if (model.random.nextDouble() < model.randomAgentRetirementProb)
				return Status.RETIRED;
			else
				return Status.WORKING;
		}
	}
	
	@Override
	public double doubleValue() {
		switch (status) {
		case RETIRED:	return 0;
		case DEAD:		return 1;
		default:		return 4;	// RANDOM
		}
	}

}
