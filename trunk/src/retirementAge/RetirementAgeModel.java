package retirementAge;

import sim.engine.SimState;

public class RetirementAgeModel extends SimState
{
	private static final long serialVersionUID = 1L;

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
