package frc.robot.subsystems;

import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;

/**
 * An object representing the entire drivetrain of a Swerve drive robot.
 * 
 * @author Ryan McGee
 * @version 2_13_2021
 */
public class SwerveDrive {

    // ROBOT CONFIGURATION

    // Distances between the middle of the wheel of the robot wheelbase 
    // Width = Left->Right, Length = Front->Back
    private static final double BOT_WIDTH = 1;
    private static final double BOT_LENGTH = 1;
    
    private SwerveModule leftFront, rightFront, rightRear, leftRear;
    private SwerveDriveKinematics kinematics;

    /**
     * Create the swervedrive object with four SwerveModule objects
     * Each module is made up (in software) of a turning motor, turning encoder, and drive motor.
     * 
     * @param leftFront The module on the front-port-side
     * @param rightFront The module on the front-starboard side
     * @param rightRear The module on the back-starboard side
     * @param leftRear The module on the back-port side 
     */
    public SwerveDrive(SwerveModule leftFront, SwerveModule rightFront, SwerveModule rightRear, SwerveModule leftRear)
    {
        this.leftFront = leftFront;
        this.rightFront = rightFront;
        this.rightRear = rightRear;
        this.leftRear = leftRear;

        // Calculate the distances between each wheel and the center of the robot.
        // This is used to calculate what angle the wheels should go for rotation.
        // (e.g. 45 degrees for a square wheelbase)
        Translation2d lf_dist = new Translation2d(BOT_LENGTH / 2.0, BOT_WIDTH / 2.0);
        Translation2d rf_dist = new Translation2d(BOT_LENGTH / 2.0, -BOT_WIDTH / 2.0);
        Translation2d rr_dist = new Translation2d(-BOT_LENGTH / 2.0, -BOT_WIDTH / 2.0);
        Translation2d lr_dist = new Translation2d(-BOT_LENGTH / 2.0, BOT_WIDTH / 2.0);

        // The order that each distance is passed in is the same as the output after calculating,
        // so make sure to remember it!
        kinematics = new SwerveDriveKinematics(lf_dist, rf_dist, rr_dist, lr_dist);
    }

    /**
     * Drive the robot using polar coordinates. Useful for autonomous driving:
     * e.g. drive_polar(45, 1, 0); to strafe at a right diagonal at full speed.
     * @param dir_deg Direction in degrees. 0 is forward, and positive is Counter-Clockwise.
     * @param mag How fast the robot should drive, in percentage: 0.0->1.0
     * @param rot How fast the robot should spin, in percentage: 0.0->1.0
     */
    public void drive_polar(double dir_deg, double mag, double rot)
    {
        // Convert polar to cartesion
        double leftY = mag * Math.sin(Math.toRadians(dir_deg));
        double leftX = mag * Math.cos(Math.toRadians(dir_deg));

        // Limit between -1.0 and 1.0
        leftY = (leftY > 1.0) ? 1.0 : (leftY < -1.0) ? -1.0 : leftY;
        leftX = (leftX > 1.0) ? 1.0 : (leftX < -1.0) ? -1.0 : leftX;
        rot = (rot > 1.0) ? 1.0 : (rot < -1.0) ? -1.0 : rot;

        // Calculate the wheel vectors and drive.
        this.drive(leftY, leftX, rot);
    }

    /**
     * Drive the robot using a controller. In this scheme, the left stick controls
     * translational movements, and the right stick X axis controls how much it spins.
     * 
     * This method contains all the calculations required for each wheel.
     * 
     * @param leftY Left thumbstick Y axis, -1.0->1.0
     * @param leftX Left thumbstick X axis, -1.0->1.0
     * @param rightX Right thumbstick X axis, -1.0->1.0
     */
    public void drive(double leftY, double leftX, double rightX)
    {
        if(Math.abs(leftY) < .1)
            leftY = 0.0;
        if(Math.abs(leftX) < .1)
            leftX = 0.0;
        if(Math.abs(rightX) < .1)
            rightX = 0.0;
        rightX = rightX *.2;

        // Usually there would be units here, but percentage should work too.
        ChassisSpeeds speeds = new ChassisSpeeds(leftY, leftX, rightX);

        // Calculate the angle / speed required for each swerve module using inverse kinmatics
        SwerveModuleState moduleStates[] = kinematics.toSwerveModuleStates(speeds);

        // Make sure this matches the order set in the constructor!
        leftFront.set(moduleStates[0]);
        rightFront.set(moduleStates[1]);
        rightRear.set(moduleStates[2]);
        leftRear.set(moduleStates[3]);
    }

}
