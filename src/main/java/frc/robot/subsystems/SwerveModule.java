package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.SwerveModuleState;

/**
 * A "swerve module", as described by a Direction motor, a Direction encoder, and a Drive motor.
 * The preset configuration and tuning is designed for the Andymark Swerve & Steer module,
 * with the CIM drive motor and PG71 (775) steering motor, and PG encoder.
 * 
 * Handles the feedback loop for the direction encoder, and setting the speed of the wheel.
 * 
 * @author Ryan McGee
 * @version 2_13_2021
 */
public class SwerveModule {

    private static final double TURN_MOTOR_GEAR_RATIO = (1.0 / 71.0) * (48.0 / 40.0); // 71 to 1 reduction, 40 to 48 increase
    private static final int ENCODER_PULSES_PER_REV = 7;

    private TalonSRX turnMotor, driveMotor;
    private Encoder turnEncoder;

    // PID object allows for fine control over the swerve module's angle.
    private PIDController turnPID = new PIDController(.075, 0, 0);

    /**
     * Create the swerve module object.
     * 
     * @param turnMotor Motor that handles what direction the module is facing
     * @param driveMotor Motor that handles the speed of the wheel
     * @param turnEncoder Encoder located on the directional motor
     */
    public SwerveModule(TalonSRX turnMotor, TalonSRX driveMotor, Encoder turnEncoder)
    {
        this.turnMotor = turnMotor;
        this.driveMotor = driveMotor;
        this.turnEncoder = turnEncoder;

        // Set the encoder to output in degrees
        this.turnEncoder.setDistancePerPulse(360.0 * TURN_MOTOR_GEAR_RATIO / ENCODER_PULSES_PER_REV);

        // This enables the PID loop to "wrap" the input to the closest angle
        // e.g. if the wheel has rotated twice since starting, inputting 90 degrees will not
        // spin backwards twice
        this.turnPID.enableContinuousInput(-180, 180);
    }

    /**
     * Control the speed of the drive motor / wheel
     * @param speed Percentage, from -1.0 -> 1.0
     */
    public void setSpeed(double speed)
    {
         driveMotor.set(ControlMode.PercentOutput, speed);
    }

    /**
     * Control what angle the wheel is facing
     * @param angleDegrees Angle, where 0 is facing forward and positive is clockwise
     */
    public void setDirection(double angleDegrees)
    {
        double out = turnPID.calculate(turnEncoder.getDistance(), angleDegrees);

         turnMotor.set(ControlMode.PercentOutput, out);
    }

    /**
     * Set all aspects of the module at once. This includes wheel speed and direction.
     * 
     * @param state The desired "state" of the swerve module
     */
    public void set(SwerveModuleState state)
    {
        // Optimize the new position based on the state of the encoder.
        // This will allow the module to rotate a shorter distance and "go backwards" instead of making large rotations.
        // e.g. if the current position is 0 degrees, and we want to go 180 degrees, the module should not have to turn.
        Rotation2d encoderState = new Rotation2d(Math.toRadians(turnEncoder.getDistance()));
        SwerveModuleState optimized = SwerveModuleState.optimize(state, encoderState);

        this.setDirection(optimized.angle.getDegrees());
        this.setSpeed(optimized.speedMetersPerSecond);
    }
}
