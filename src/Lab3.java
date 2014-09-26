/*
 * Lab3.java
 * 
 * Called on start; Controls what the robot does.
 */
import lejos.nxt.*;

public class Lab3 {
	
	
	/* Wheel based and wheel radius */
	public static final double 
		WHEEL_RADIUS = 2.14,
		WHEEL_BASE = 15.7;
	
	public static final NXTRegulatedMotor
		LEFT_MOTOR = Motor.A,
		US_MOTOR = Motor.B,
		RIGHT_MOTOR = Motor.C;
	
	public static final int
		AVOID = Button.ID_LEFT,
		TO_LOC = Button.ID_RIGHT;
	
	// List of locations to travel to in order
	private static final double[][] locList = {
		{60,30},
		{30,30},
		{30,60},
		{60,0}};
	
	// List of locations for avoidance test
	private static final double[][] avoidLocList = {
		{0, 60}, 
		{60, 0}};
	
	public static void main(String[] args) {
		int buttonChoice = 0;
		// some objects that need to be instantiated
		
		Odometer odometer = new Odometer();
		Driver navigator = new Driver(odometer);
		Avoid avoid = new Avoid(new UltrasonicSensor(SensorPort.S2), odometer, navigator);
		
		OdometerDisplay.printMainMenu();
		
		while (buttonChoice != AVOID && buttonChoice != TO_LOC) buttonChoice = Button.waitForAnyPress();

		try { Thread.sleep(1000);
		} catch (InterruptedException e) {}
		
		int i = 0;
		switch (buttonChoice){
		case AVOID:
			LCD.clear();
			//starts the avoid thread
			avoid.start();
			
			//starts odometer thread
			odometer.start();

			while (i < avoidLocList.length){
				boolean travel = false;
				synchronized(navigator){
					travel = navigator.isNavigating();}
				if (travel){
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {}
				}else{
					navigator.travel(avoidLocList[i][0], avoidLocList[i][1]);
					i++;
				}	
			}
			
			break;
		case TO_LOC:
			odometer.start();
			while (i < locList.length){
				synchronized(navigator){
				if (navigator.isNavigating()){
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {}
				}else{
					navigator.travel(locList[i][0], locList[i][1]);
					i++;
				}	}
			}
			break;
		default:
			LCD.clear();
			LCD.drawString("Invalid Option", 0, 0);
			System.exit(-1);}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		
		System.exit(0);
	}
}