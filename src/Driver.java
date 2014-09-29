/**
 * Driver.java
 * 
 * The driver class used in our design
 * Controls all of the robot's movement
 */


public class Driver extends Thread {
	
	private static final int 
		FORWARD_SPEED = 250,
		ROTATE_SPEED = 150;
	
	private double thetar, xr, yr, xDest, yDest;
	private boolean navigating;
	private Odometer odo;
	
	public Driver(Odometer odometer){
		this.odo =  odometer;
		navigating = false;}
	
	/**
	 * Has the robot move to a position, relative to starting coordinates
	 * 
	 * Calculates angle and distance to move to using basic trig and then calls
	 * the turnTo and goForward method to move to that point
	 * 
	 * @param x Coordinate of destination
	 * @param y Coordinate of destination
	 */
	public void travel(double x, double y){
		this.xDest = x;
		this.yDest = y;
		navigating = true;
		new Thread(new Runnable(){public void run(){
			//gets position. Synchronized to avoid collision
			synchronized (odo) {
				setThetar(odo.getTheta() * 180. / Math.PI);
				setXr(odo.getX());
				setYr(odo.getY());}
			
			//calculates actual angle to turn
			double theta =  (Math.atan2(xDest - getXr(), yDest - getYr()) * 180 / Math.PI) - getThetar();
			//calculates magnitude to travel
			double distance  = Math.sqrt(Math.pow((y-getYr()), 2) + Math.pow((x-getXr()),2));
			//finds minimum angle to turn (ie: it's easier to turn +90 deg instead of -270)
			
			Avoid.disable();
			if(theta <= -180)
				turnTo(theta + 360);
			else if(theta > 180)
				turnTo(theta - 360);
			else turnTo(theta);
			
			Avoid.enable();
			goForward(distance);
			
			navigating = false;
		}}).start();
	}
	
	public void goForward(double distance){
		
		// drive forward 
		Lab3.LEFT_MOTOR.setSpeed(FORWARD_SPEED);
		Lab3.RIGHT_MOTOR.setSpeed(FORWARD_SPEED);
		
		//for isNavigatingMethod	
		Lab3.LEFT_MOTOR.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), true);
		Lab3.RIGHT_MOTOR.rotate(convertDistance(Lab3.WHEEL_RADIUS, distance), false);}
	
	public void turnTo (double theta){
		// turn degrees clockwise
		Lab3.LEFT_MOTOR.setSpeed(ROTATE_SPEED);
		Lab3.RIGHT_MOTOR.setSpeed(ROTATE_SPEED);
		//calculates angel to turn to and rotates
		Lab3.LEFT_MOTOR.rotate(convertAngle(Lab3.WHEEL_RADIUS, Lab3.WHEEL_BASE, theta), true);
		Lab3.RIGHT_MOTOR.rotate(-convertAngle(Lab3.WHEEL_RADIUS, Lab3.WHEEL_BASE, theta), false);}
	
	/**
	 * Returns true if the robot is navigating
	 * 
	 * @return boolean indicating if the robot is traveling
	 */
	public boolean isNavigating(){
		return this.navigating;}
	
	public void setNavigating(boolean navigating){
		this.navigating = navigating;}
	
	/**
	 * Returns degrees to turn servos in order to rotate robot by that amount
	 * 
	 * Uses basic math to convert and absolute angle to degrees to turn.
	 * 
	 * @param Radius of wheel
	 * @param Width of wheel base
	 * @param Absolute angle to turn to
	 * 
	 * @return Degrees the servo should turn
	 */
	private static int convertAngle(double radius, double width, double angle) {
		//(width * angle / radius ) / (2)
		return convertDistance(radius, Math.PI * width * angle / 360.0);}
	
	/**
	 * Moves robot linerly a certain distance
	 * 
	 * @param Radius of lego wheel
	 * @param Distance to travel
	 * 
	 * @return degrees to turn servos in order to move forward by that amount
	 */
	private static int convertDistance(double radius, double distance) {
		// ( D / R) * (360 / 2PI)
		return (int) ((180.0 * distance) / (Math.PI * radius));}
	
	public double getThetar() {return thetar;}
	public double getXr() {return xr;}
	public double getYr() {return yr;}
	public void setThetar(double thetar) {this.thetar = thetar;}
	public void setXr(double xr) {this.xr = xr;}
	public void setYr(double yr) {this.yr = yr;}

	public double getXDest() {return this.xDest;}
	public double getYDest() {return this.yDest;}
}
