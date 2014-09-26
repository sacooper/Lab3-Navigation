/*
 * Thread which has the robot avoid blocks
 * Whenver the sensor detects a block it calls the avoid method
 */
import lejos.nxt.*;
public class Avoid extends Thread{
	
	private final int MIN_DISTANCE = 10;
	
	private UltrasonicSensor us;
	private Driver driver;
	
	public Avoid(UltrasonicSensor us, Odometer odometer, Driver driver){
		this.us = us;
		this.driver = driver;}
	
	/**
	 * Called when Avoid thread is started
	 * When it detects an object, the avoidBlock scenario is used
	 */
	@Override
	public void run(){
		while(true){
			//starts process if sensor detects a block
			if(us.getDistance() < MIN_DISTANCE){
				//stops motors, just in case
				double destX, destY;
				synchronized(driver){
					Lab3.LEFT_MOTOR.stop();
					Lab3.RIGHT_MOTOR.stop();
					
					//stores locations it is driving to, in case of obstacle avoidance
					destX = driver.getXDest();				
					destY = driver.getYDest();
					
					driver.setNavigating(true);}
					//starts to avoid block
					avoidBlock();
					
				//after it is done avoiding the block, it starts traveling to the previous destination
				driver.travel(destX, destY);
			}
			try { Thread.sleep(10); } catch(Exception e){}
		}
	}
	
	/**
	 * Has the robot avoid blocks
	 * 
	 * Turns 90 deg
	 * Goes straight a specified distance
	 * Turns back to earlier position
	 * 
	 */
	public void avoidBlock(){
		//rotates 90 degrees clockwise
		driver.turnTo(90.0);
		
		//moves forwards
		driver.goForward(40);
		
		Lab3.US_MOTOR.rotate(-90);
		//recursively calls avoidBlock if it is still in sight
		if(us.getDistance() < MIN_DISTANCE){
			Lab3.US_MOTOR.rotate(90);
			avoidBlock();}
		else{
			//rotates 90 degrees counterclockwise
			Lab3.US_MOTOR.rotate(90);
			driver.turnTo(-90);
		}
	}
	

}
