package retirementAge;

import sim.engine.SimState;

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

	public RetirementAgeModel(long seed) {
		super(seed);
	}

	@Override
	public void start() {
		super.start();
		System.out.println("A useless commit");
		
		// here's a useless comment
	}

	public static void main(String[] args) {
		doLoop(RetirementAgeModel.class, args);
		System.exit(0);
	}
}
