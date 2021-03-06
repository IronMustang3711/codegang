package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.commands.ResetSensors;
import frc.robot.stuff.SensorReset;
import frc.robot.stuff.TalonFaultsReporter;

import java.util.List;

public class ChassisSubsystem extends SubsystemBase implements SensorReset {

  private WPI_TalonSRX leftFront = new WPI_TalonSRX(3);
  private WPI_TalonSRX leftRear = new WPI_TalonSRX(10);
  private WPI_TalonSRX rightFront = new WPI_TalonSRX(13);
  private WPI_TalonSRX rightRear = new WPI_TalonSRX(11);
  // private DifferentialDrive drive;

  final List<WPI_TalonSRX> talons = List.of(leftFront, leftRear, rightFront, rightRear);

  PigeonIMU pigeon;

  public ChassisSubsystem() {


    //drive = new DifferentialDrive(leftFront, rightFront);
//    drive = new DifferentialDrive(new SpeedControllerGroup(leftFront, leftRear),
//                                  new SpeedControllerGroup(rightFront, rightRear));


    pigeon = new PigeonIMU(rightRear);

    addChild("left1", leftFront);
    addChild("left2", leftRear);
    addChild("right1", rightFront);
    addChild("right2", rightRear);


//    drive.setExpiration(0.5);
//    drive.setMaxOutput(1.0);
//
//    drive.setRightSideInverted(false);

    for (var talon : talons) {

      talon.setSafetyEnabled(true);
      talon.setExpiration(0.5);
      talon.configFactoryDefault();

      talon.configOpenloopRamp(0.0);
      talon.setNeutralMode(NeutralMode.Brake);
      talon.configNeutralDeadband(0.04);
      talon.configNominalOutputForward(0.0); //0.15
      talon.configNominalOutputReverse(0.0); //-0.15

      TalonFaultsReporter.instrument(talon);
    }

    leftFront.set(ControlMode.PercentOutput, 0.0);
    rightFront.set(ControlMode.PercentOutput, 0.0);


    rightRear.follow(rightFront);
    leftRear.follow(leftFront);

    //TODO: see if these lines do anything
    leftFront.set(ControlMode.PercentOutput, 0.0);
    rightFront.set(ControlMode.PercentOutput, 0.0);

    rightFront.setInverted(false);
    leftFront.setInverted(true);

    // rightRear.setInverted(InvertType.FollowMaster);
    // leftRear.setInverted(InvertType.FollowMaster);
    rightRear.setInverted(false);
    leftRear.setInverted(true);

    rightFront.setSensorPhase(true);
    leftFront.setSensorPhase(true);

    leftFront.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,
                                           Constants.TalonConstants.PRIMARY_PID,
                                           Constants.TalonConstants.DEFAULT_TIMEOUT);

    leftFront.setSelectedSensorPosition(0, Constants.TalonConstants.PRIMARY_PID,
                                        Constants.TalonConstants.DEFAULT_TIMEOUT);

    rightFront.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,
                                            Constants.TalonConstants.PRIMARY_PID,
                                            Constants.TalonConstants.DEFAULT_TIMEOUT);

    rightFront.setSelectedSensorPosition(0, Constants.TalonConstants.PRIMARY_PID,
                                         Constants.TalonConstants.DEFAULT_TIMEOUT);
    leftFront.setSensorPhase(true);
    rightFront.setSensorPhase(true);


   // setupShuffleboard();
  }

  private void setupShuffleboard() {
    var tab = Shuffleboard.getTab(ChassisSubsystem.class.getSimpleName());
    tab.addNumber("left_encoder", this::getLeftEncoderPosition);
    tab.addNumber("right_encoder", this::getRightEncoderPosition);
    tab.addNumber("left_vel", this::getLeftEncoderVelocity);
    tab.addNumber("right_vel", this::getRightEncoderVelocity);
    // tab.addDoubleArray("vels", () -> new double[]{getLeftEncoderVelocity(), getRightEncoderVelocity()});
    tab.addNumber("left1_current", leftFront::getStatorCurrent);
    tab.addNumber("left2_current", leftRear::getStatorCurrent);
    tab.addNumber("right1_current", rightFront::getStatorCurrent);
    tab.addNumber("right2_current", rightRear::getStatorCurrent);
//    tab.addDoubleArray("currents", () -> new double[]{leftFront.getStatorCurrent(), leftRear.getStatorCurrent(),
//                                                      rightFront.getStatorCurrent(), rightRear.getStatorCurrent()});
    tab.add(leftFront);
    tab.add(leftRear);
    tab.add(rightFront);
    tab.add(rightRear);


    tab.addNumber("pigeonHeading", pigeon::getFusedHeading);
    // tab.addDoubleArray("headings", () -> new double[]{ahrs.getAngle(), gyro.getAngle()});
    tab.add(new ResetSensors<>(this));

  }

  public double getLeftEncoderPosition() {
    return leftFront.getSelectedSensorPosition();
  }

  public double getLeftEncoderVelocity() {
    return leftFront.getSelectedSensorVelocity();
  }

  public double getRightEncoderPosition() {
    return rightFront.getSelectedSensorPosition();
  }

  public double getRightEncoderVelocity() {
    return rightFront.getSelectedSensorVelocity();
  }

  @Override
  public void periodic() {
    // SmartDashboard.putNumber("left1Output", leftFront.getMotorOutputPercent());
    // SmartDashboard.putNumber("left2Output", leftRear.getMotorOutputPercent());

    // SmartDashboard.putNumber("right1Output", rightFront.getMotorOutputPercent());
    // SmartDashboard.putNumber("right2Output", rightRear.getMotorOutputPercent());

    // double[] positions = {getLeftEncoderPosition(), getRightEncoderPosition()};
    // SmartDashboard.putNumber("leftPosition", positions[0]);
    // SmartDashboard.putNumber("rightPosition", positions[1]);

    // double[] vels = {getLeftEncoderVelocity(), getRightEncoderVelocity()};
    // SmartDashboard.putNumber("leftVel", vels[0]);
    // SmartDashboard.putNumber("rightVel", vels[1]);
    // SmartDashboard.putNumberArray("vels", vels);
  }


  public void arcadeDrive(double forward, double rotation) {
    arcadeDrive2(forward, rotation);
  }

  @SuppressWarnings("ManualMinMaxCalculation")
  private static double clamp(double value) {
    return value > 1.0 ? 1.0 : value < -1.0 ? -1.0 : value;
  }

  //this came from edu.wpi.first.wpilibj.drive.DifferentialDrive
  private void arcadeDrive2(double fwd, double rotate) {
    //TODO: try...
    /*
     *  Arcade Drive Example:
     *		_talonLeft.set(ControlMode.PercentOutput, joyForward, DemandType.ArbitraryFeedForward, +joyTurn);
     *		_talonRght.set(ControlMode.PercentOutput, joyForward, DemandType.ArbitraryFeedForward, -joyTurn);
     */

    fwd = clamp(fwd);

    rotate = clamp(rotate);

    // Square the inputs (while preserving the sign) to increase fine control
    // while permitting full power.
    // if (squareInputs) {
    fwd = Math.copySign(fwd * fwd, fwd);
    rotate = Math.copySign(rotate * rotate, rotate);
    //  }

    double leftMotorOutput;
    double rightMotorOutput;

    double maxInput = Math.copySign(Math.max(Math.abs(fwd), Math.abs(rotate)), fwd);

    if (fwd >= 0.0) {
      // First quadrant, else second quadrant
      if (rotate >= 0.0) {
        leftMotorOutput = maxInput;
        rightMotorOutput = fwd - rotate;
      } else {
        leftMotorOutput = fwd + rotate;
        rightMotorOutput = maxInput;
      }
    } else {
      // Third quadrant, else fourth quadrant
      if (rotate >= 0.0) {
        leftMotorOutput = fwd + rotate;
        rightMotorOutput = maxInput;
      } else {
        leftMotorOutput = maxInput;
        rightMotorOutput = fwd - rotate;
      }
    }
    leftMotorOutput = clamp(leftMotorOutput);
    rightMotorOutput = clamp(rightMotorOutput);

    leftFront.set(leftMotorOutput);
    rightFront.set(rightMotorOutput);

    //TODO: remove once talon following is working
    leftRear.set(leftMotorOutput);
    rightRear.set(rightMotorOutput);
  }

  @Override
  public void resetSensors() {
    for (var c : List.of(leftFront, rightFront)) {
      ErrorCode errorCode = c.getSensorCollection().setQuadraturePosition(0, Constants.TalonConstants.DEFAULT_TIMEOUT);
      if (errorCode != ErrorCode.OK) {
        DriverStation.reportError("Problem reseting " + c.getName() + " in subsystem " + getSubsystem(), false);
      }
    }

    pigeon.setFusedHeading(0.0, 50);

  }
}
