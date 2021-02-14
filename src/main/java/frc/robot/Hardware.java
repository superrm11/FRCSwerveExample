package frc.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.subsystems.SwerveDrive;
import frc.robot.subsystems.SwerveModule;

public class Hardware {

    // ================  CONTROLLERS ===============

    public static XboxController controller = new XboxController(0);
    
    // ================ DRIVETRAIN ================

    // Encoders
    public static Encoder leftFrontTurnEncoder = new Encoder(0, 1);
    public static Encoder rightFrontTurnEncoder = new Encoder(2, 3);
    public static Encoder rightRearTurnEncoder = new Encoder(4, 5);
    public static Encoder leftRearTurnEncoder = new Encoder(6, 7);

    // Drive Motors - 4 CIMs
    public static TalonSRX leftFrontDrive = new TalonSRX(0);
    public static TalonSRX rightFrontDrive = new TalonSRX(1);
    public static TalonSRX rightRearDrive = new TalonSRX(2);
    public static TalonSRX leftRearDrive = new TalonSRX(3);

    // Turn Motors
    public static TalonSRX leftFrontTurn = new TalonSRX(4);
    public static TalonSRX rightFrontTurn = new TalonSRX(5);
    public static TalonSRX rightRearTurn = new TalonSRX(6);
    public static TalonSRX leftRearTurn = new TalonSRX(7);

    // Swerve Module subsystems
    public static SwerveModule leftFrontModule = new SwerveModule(leftFrontTurn, leftFrontDrive, leftFrontTurnEncoder);
    public static SwerveModule rightFrontModule = new SwerveModule(rightFrontTurn, rightFrontDrive, rightFrontTurnEncoder);
    public static SwerveModule rightRearModule = new SwerveModule(rightRearTurn, rightRearDrive, rightRearTurnEncoder);
    public static SwerveModule leftRearModule = new SwerveModule(leftRearTurn, leftRearDrive, leftRearTurnEncoder);

    // Swerve Drivetrain subsystem
    public static SwerveDrive driveSystem = new SwerveDrive(leftFrontModule, rightFrontModule, rightRearModule, leftRearModule);

}
