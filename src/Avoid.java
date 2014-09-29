import lejos.nxt.UltrasonicSensor;

/*********
 * Group 5
 * @author Scott Cooper	- 260503452
 * @author Liqing Ding - 260457392
 * 
 * The purpose of the Avoid class is
 * to watch for blocks in the path of the robot.
 * 
 * When a block is seen, the current actions are stopped to
 * handle the block avoidance. The block is avoided using
 * the PType controller from Lab1, adapted to suit our
 * needs in this lab. Once past the block, movement to the
 * original destination is resumed.
 */
public class Avoid extends Thread{
	
	// Minimum distance to a block
	public static final int MIN_DISTANCE = 15;
	
	// Member variables
	private UltrasonicSensor us;
	private Driver driver;
	private Odometer os;
	
	// Whether or not block avoidance is enabled
	private static boolean enabled;
	static{enabled = true;}
	
	/**********
	 * Create a new Avoidance using the following paramaters
	 * 
	 * @param us The ultrasonic sensor to use for avoidance
	 * @param odometer The odometer that is measuring the position
	 * @param driver The navigator that controls moving to a waypoint
	 */
	public Avoid(UltrasonicSensor us, Odometer odometer, Driver driver){
		this.us = us;
		this.driver = driver;
		this.os = odometer;}
	
	/**
	 * Called when the Avoid thread is started
	 * When it detects an object, the robot moves to avoid the block, 
	 * and resumes traveling towards the original waypoint
	 */
	@Override
	public void run(){
		while(true){
			//starts process if sensor detects a block
			if(us.getDistance() < MIN_DISTANCE && Avoid.enabled){
				//stops motors, just in case
				double destX, destY;

				Lab3.RIGHT_MOTOR.stop();
				Lab3.LEFT_MOTOR.stop();
				synchronized(driver){					
					//stores locations it is driving to, in case of obstacle avoidance
					destX = driver.getXDest();				
					destY = driver.getYDest();
					
					driver.setNavigating(true);
					avoidBlock();}
					//starts to avoid block
					
				//after it is done avoiding the block, it starts traveling to the previous destination
				driver.travel(destX, destY);
			}
			try { Thread.sleep(10); } catch(Exception e){}
		}
	}
	
	/********
	 * Avoid a block using wallfollowing
	 * 
	 * The robot turns 90 degrees, and follows the wall until it sees a
	 * turn, after which it moves forward to get past the block
	 */
	public void avoidBlock(){
		// TODO: Test whether the robot avoids & recognizes better with the US always at 45 degrees
		double theta = os.getTheta();
		driver.turnTo(90.);
		
		// Rotate the ultrasonic sensor back to wall following position
		Lab3.US_MOTOR.rotate(-45);	
		
		/* Create the ultrasonic controller, and follow the wall until we're
		 * back at the original angle
		 */
		UltrasonicController uc = new UltrasonicController();
		while(Math.abs(theta - os.getTheta()) > .01){
			uc.processUSData(us.getDistance());}
		Lab3.LEFT_MOTOR.stop();
		Lab3.RIGHT_MOTOR.stop();
		// Rotate the ultrasonic sensor back to traveling position
		Lab3.US_MOTOR.rotate(45);
		os.setTheta(os.getTheta() - .06);
		// Move past the block
		driver.goForward(15);
		}
	
	/*************************
	 * Enabled block detection and avoidance
	 */
	public static void enable(){
		enabled = true;}
	
	/************************
	 * Disable block detection and avoidance
	 */
	public static void disable(){
		enabled =false;}
}
