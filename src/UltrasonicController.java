import lejos.nxt.Motor;
import lejos.nxt.NXTRegulatedMotor;

/******************************************************************************
 * Group 5
 * @author Scott Cooper	- 260503452
 * @author Liqing Ding - 260457392
 * 
 * @requirement Sensor must be positioned at a 45° angle
 * @requirement Robot must be on the left of the wall
 * @requirement Ultrasonic Sensor is plugged into port S1
 * @requirement The left and right motors are plugged into A and C respectively
 * 
 * Abstraction of an UltrasonicController, which elides the details of
 * how to turn left and right based on the error to the extending classes.
 *****************************************************************************/

public class UltrasonicController {
	private static final int
		// Maximum number of gaps before turning
		FILTER_OUT = 20,
		//Minimum speed a wheel will rotate at
		MIN_SPEED = 150,
		// Maximum speed a wheel will rotate at
		MAX_SPEED = 350,
		
		MOTOR_STRAIGHT = 200,
		// Factor to multiply error by to determine correct speed
		SCALING_FACTOR = 10,
		
		BAND_WIDTH = 10;
	
	
	protected final NXTRegulatedMotor 
		leftMotor = Motor.A, 
		rightMotor = Motor.C; 

	private int distance, gapCount;
			
	/******
	 * Create a new UltrasonicController with given bandCenter and bandWidth.
	 * This values must be passed from the extending class.
	 * @param bandCenter The Ideal distance from the wall (cm)
	 * @param bandWidth The allowable threshold of error in distance from the wall (cm)
	 */
	public UltrasonicController(){
		gapCount = 0;
		distance = 0;
		straight();
	}
	/*****
	 * Method to processes and act based on current distance from wall
	 * @param distance Distance from the wall
	 */
	public void processUSData(int distance) {
		this.distance = distance;
		//difference between ideal distance and real distance (y - r)
		int error = (Avoid.MIN_DISTANCE - this.distance);
		//Too far away from wall
		if (error < -BAND_WIDTH){
			// increment filterControl and if found turn, go left
			if (++gapCount > FILTER_OUT)
				turnLeft(Math.abs(error));}
		//Too Close to wall
		else if (error > BAND_WIDTH){
			gapCount = 0;
			turnRight(Math.abs(error));}
		// Within allowable threshold
		else {
			gapCount = 0;
			straight();
		}
	}
	
	/*****************************************************
	 * Sets the robot to go straight at speed MOTOR_STRAIGHT
	 * Called if robot is traveling between BANDCENTER +- BANDWITH
	 */
	public void straight(){
		leftMotor.setSpeed(MOTOR_STRAIGHT);		
		rightMotor.setSpeed(MOTOR_STRAIGHT); 
		leftMotor.forward();
		rightMotor.forward();
	}	
	
	protected void turnLeft(int error){
		leftMotor.setSpeed(Math.max((MOTOR_STRAIGHT - SCALING_FACTOR * error) , MIN_SPEED));
		rightMotor.setSpeed(Math.min((MOTOR_STRAIGHT + SCALING_FACTOR * error), MAX_SPEED));
		leftMotor.forward();
		rightMotor.forward();}
	
	/*****************************************************
	 * Increases the speed of the leftMotor and reduces the speed of the rightMotor
	 * Method is called if the robot is traveling less than BANDCENTER - BANDWITH
	 * 
	 * @param error : the absolute value of (BANDCENTER - this.distance)
	 * 
	 * The speed of the left motor is: 	200 + error °/s
	 * The speed of the right motor is:	-(200 - error) °/s
	 */
	protected void turnRight(int error){	
		leftMotor.setSpeed(MOTOR_STRAIGHT + error);
		rightMotor.setSpeed(MOTOR_STRAIGHT - error);
		leftMotor.forward();
		rightMotor.backward();
	}
	
	/***
	 * Get the current distance
	 * @return The current distance from the wall (cm)
	 */
	public int readUSDistance(){return this.distance;}
}
