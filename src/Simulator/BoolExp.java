package Simulator;

/**
 * Allows lambda functions of boolean expressions to be passed to the basic gate creators to dictate their internal logic.
 */
@FunctionalInterface
public interface BoolExp {
	
	public boolean exp(boolean a, boolean b);
}
