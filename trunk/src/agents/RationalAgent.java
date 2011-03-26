package agents;

import retirementAge.RetirementAgeModel;

public class RationalAgent extends Agent {

	public RationalAgent(int currentAge, int deathTime) {
		super(currentAge, deathTime);
	}



	/**
	 * Annoying serial
	 */
	private static final long serialVersionUID = 1L;

	
	
	/**
	 * Rational Agent is quite boring. It simply checks what's its own age, what's the retirement age and if the first is above or equal to the second then it retires
	 */
	@Override
	protected Status doIRetire() {
		//do I qualify for retrement?
		if(this.age >= RetirementAgeModel.retirementAge)
			//if so, retire!
			return Status.RETIRED;
		else
			//otherwise let's slave away for this year!
			return Status.WORKING;
	}

}
