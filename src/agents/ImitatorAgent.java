package agents;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import retirementAge.Demographics;
import retirementAge.RetirementAgeModel;
import sim.engine.SimState;
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
	 * @param model reference to the main model
	 */
	public ImitatorAgent(int currentAge, int deathAge, RetirementAgeModel model) {
		//call the super constructor
		super(currentAge, deathAge, model);
	}

	/**
	 * This is called to create the network. It is called automatically during the first step when it realizes the socialNetwork doesn't exist
	 */
	protected void fillNetwork()
	{
		//instantiate the network!
		socialNetwork = new HashSet<Agent>();

		//how big should the network be?
		//U~[min,max]
		int networkSize = model.random.nextInt(model.maxNetworkSize- model.minNetworkSize) 
						  + model.minNetworkSize ;
		
		//how far the model should go
		int extent = model.random.nextInt(model.networkExtent+1);
		
		//what is the actual age min and age max?
		
		//can't go below the minAge
		int minFriendAge = Math.max(this.age-extent, model.minAge);
		//can't go above the maxAge
		int maxFriendAge = Math.min(this.age+extent, model.maxAge);
		//now we have the two bounds
		
		//find #networkSize friends
		for(int i=0; i < networkSize; i++)
		{
			//this will be true if we managed to add a new friend (rather than an existing one)
			boolean isValid;
			//this do will pick a friend
			do{
				//pick the age of your friend
				int friendAge;
				
				//if the extent is 0, only choose friends of your own age
				if(minFriendAge==maxFriendAge)
					//this is the age
					friendAge = this.age;
				else
					friendAge = model.random.nextInt(1+maxFriendAge-minFriendAge) + minFriendAge;
				
				//now get the cohort associated with that age
				Agent[] cohort = model.society.getCohort(friendAge);
				
				//add an agent at random to the map
				//If that friend was not already in, isValid is true
				isValid = socialNetwork.add(cohort[model.random.nextInt(cohort.length)]);
				
			}while(!isValid);
	
		}
		
		
	}
	
	
	//override the super step
	public void step(SimState arg0)
	{
		//if the network don't exist, create it!
		if(socialNetwork == null)
			//fill the network!
			fillNetwork();
		//call the Agent step
		super.step(arg0);
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
			if(x!= null && x.getAge() >= model.retirementAge && x.getStatus() != Status.DEAD)
			{
				//x is a friend that can retire
				friendsThatCanRetire++;
				//is he actually retired?
				if(x.getStatus() == Status.RETIRED)
					//if so count that
					friendsThatHaveRetired++;
			}
		}
		
		//before we go further, let's remove dead friends from the social network or gc can't remove them
		cleanNetwork();
		
		//now check the threshold
		if(friendsThatHaveRetired / friendsThatCanRetire >= .5d)
			//if 50+% of your friends have retired, retire
			return Status.RETIRED;
		else
			//else keep working
			return Status.WORKING;
		
	}
	
	private void cleanNetwork(){
		
		//prepare the collection where we store what to remove
		Collection<Agent> toRemove = new ArrayList<Agent>();
		
		for(Agent x : socialNetwork)
		{
			if(x == null || x.getStatus() == Status.DEAD)
				//if x is dead, add it to the toRemove list
				toRemove.add(x);
		}
		
		//now remove them from the set
		socialNetwork.removeAll(toRemove);
		
	}

	@Override
	public double doubleValue() {
		switch (status) {
		case RETIRED:	return 0;
		case DEAD:		return 1;
		}
		
		return 3;
	}

	
}
