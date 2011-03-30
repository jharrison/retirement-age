package agents;

/**
 * In this model the agents have three states: Working,Retired,Dead. <br>
 * I was a bit unsure if I had to use two booleans or an int to define their condition. I am going to try the more natural route of using
 * a custom enum. <br> Tell me if it is okay
 * @author carrknight
 *
 */
public enum Status {
	WORKING, RETIRED, DEAD
}
