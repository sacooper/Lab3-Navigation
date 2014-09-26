import lejos.nxt.LCD;

/*****************
 * Group 5
 * @author Scott Cooper	- 260503452
 * @author Liqing Ding - 260457392
 * 
 * A class to display odometry information. This class should be
 * used statically from the Odometer. In addition, this class is
 * used to display the main menu to tell the user which button
 * is which mode.
 */
public class OdometerDisplay{
	
	/****
	 * Print the current values of x, y, and theta to the screen.
	 * 
	 * Modified to be called from the Odometry class, to allow for more control
	 * 
	 * @param x The current x value of the odometer in cm
	 * @param y The current y value of the domoeter in cm
	 * @param theta The current value of theta (amount the robot has rotated) in radians
	 */
	public static void print(final double x, final double y, final double theta){
		new Thread(new Runnable(){ public void run(){
			LCD.clear();
			// clear the lines for displaying odometry information
			LCD.drawString("X:                  ", 0, 0);
			LCD.drawString("Y:                  ", 0, 1);
			LCD.drawString("T:                  ", 0, 2);
	
			LCD.drawString(formattedDoubleToString(x, 2), 3, 0);
			LCD.drawString(formattedDoubleToString(y, 2), 3, 1);
			LCD.drawString(formattedDoubleToString(theta, 2), 3, 2);
			
//			  //displays theta values
//			  LCD.drawString("Theta = " + Double.toString(theta), 0, 3);
//			  LCD.drawString("Theta R= " + Double.toString(thetaR), 0, 4);
//			  LCD.drawString("Theta D= " + Double.toString(thetaD), 0, 5);
		}}).start();}

	/*****
	 * Get a properly formated value of x to places
	 * @param x The value to format
	 * @param places The number of decimal places
	 * @return A string containing "x" to "places"
	 */
	private static String formattedDoubleToString(double x, int places) {
		String result = "";
		String stack = "";
		long t;

		// put in a minus sign as needed
		if (x < 0.0)
			result += "-";

		// put in a leading 0
		if (-1.0 < x && x < 1.0)
			result += "0";
		else {
			t = (long) x;
			if (t < 0)
				t = -t;

			while (t > 0) {
				stack = Long.toString(t % 10) + stack;
				t /= 10;}
			result += stack;
		}

		// put the decimal, if needed
		if (places > 0) {
			result += ".";

			// put the appropriate number of decimals
			for (int i = 0; i < places; i++) {
				x = Math.abs(x);
				x = 10.0 * (x - Math.floor(x));
				result += Long.toString((long) x);}}
		return result;
	}

	/********
	 * Print the initial main menu, with options for floating the motor (left)
	 * or Driving in a square (right)
	 */
	public static void printMainMenu() {
		// clear the display
		LCD.clear();

		// ask the user whether the motors should Avoid Block or Go to locations
		LCD.drawString("< Left | Right >", 0, 0);
		LCD.drawString("       |        ", 0, 1);
		LCD.drawString(" Avoid | Drive  ", 0, 2);
		LCD.drawString(" Block | to loc-  ", 0, 3);
		LCD.drawString("       | ations", 0, 4);
	}

}
