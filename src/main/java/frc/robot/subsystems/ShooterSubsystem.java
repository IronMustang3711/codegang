package frc.robot.subsystems;

import java.util.List;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.stuff.SensorReset;
import frc.robot.stuff.TalonFaultsReporter;

public class ShooterSubsystem extends SubsystemBase implements SensorReset {
  private WPI_TalonSRX controller1 = new WPI_TalonSRX(5);
  private WPI_TalonSRX controller2 = new WPI_TalonSRX(27); //lower
  double out = 0.0;

  double getOutput() {
    return out;
  }

  public ShooterSubsystem() {
    for (var talon : List.of(controller1, controller2)) {
      talon.setSafetyEnabled(false);
      talon.setExpiration(0.5);
      talon.configFactoryDefault();

      talon.configNeutralDeadband(0.04);
      talon.configNominalOutputForward(0.0); //0.15
      talon.configNominalOutputReverse(0.0); //-0.15

      talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,
                                         Constants.TalonConstants.PRIMARY_PID,
                                         Constants.TalonConstants.DEFAULT_TIMEOUT);
      talon.setSelectedSensorPosition(0, Constants.TalonConstants.PRIMARY_PID,
                                      Constants.TalonConstants.DEFAULT_TIMEOUT);

      TalonFaultsReporter.instrument(talon);
    }

    controller2.follow(controller1);
    controller2.setInverted(InvertType.FollowMaster);
    controller1.setInverted(true);
    controller2.setInverted(true);
    controller1.setSensorPhase(true);
    controller2.setSensorPhase(true);

    addChild("Shooter1", controller1);
    addChild("Shooter2", controller2);

    setupShuffleboard();
  }

  private void setupShuffleboard() {
    var tab = Shuffleboard.getTab(ShooterSubsystem.class.getSimpleName());
    tab.add(controller1);
    tab.add(controller2);
    tab.addNumber("Shooter1_velocity", this::getLowerEncoderVelocity);
    tab.addNumber("Shooter2_velocity", this::getUpperEncoderVelocity);
    //tab.addDoubleArray("velocities", () -> new double[]{getLowerEncoderVelocity(), getUpperEncoderVelocity()});
    tab.addNumber("motor_out", this::getOutput);
  }

  public double getLowerEncoderPosition() {
    return controller1.getSelectedSensorPosition();
  }

  public double getLowerEncoderVelocity() {
    return controller1.getSelectedSensorVelocity();
  }

  public double getUpperEncoderPosition() {
    return controller2.getSelectedSensorPosition();
  }

  public double getUpperEncoderVelocity() {
    return controller2.getSelectedSensorVelocity();
  }

  @Override
  public void periodic() {
    // double[] positions = {getLowerEncoderPosition(), getUpperEncoderPosition()};
    // SmartDashboard.putNumber("lowerShooter_position", positions[0]);
    // SmartDashboard.putNumber("upperShooter_position", positions[1]);
    // SmartDashboard.putNumberArray("shooter_position", positions);

    // double[] vels = {getLowerEncoderVelocity(), getUpperEncoderVelocity()};
    // SmartDashboard.putNumber("lowerShooter_velocity", vels[0]);
    // SmartDashboard.putNumber("upperShooter_velocity", vels[1]);
    // SmartDashboard.putNumberArray("shooter_velocity", vels);


  }

  public void runShooter(double amt) {
    controller1.set(amt);
    controller2.set(amt);
    out = amt;
  }

  public void enableShooter(boolean enable) {
//    if (enable) lowerController.set(1.0);
//    else lowerController.set(0.0);
    if (enable) {
      controller1.set(1.0);
      controller2.set(1.0);
    } else {
      controller1.set(0.0);
      controller2.set(0.0);
    }

  }

  public boolean isEnabled() {
    return controller1.get() != 0.0;
  }

  @Override
  public void resetSensors() {
    for (var c : List.of(controller1, controller2)) {
      ErrorCode errorCode = c.getSensorCollection().setQuadraturePosition(0, Constants.TalonConstants.DEFAULT_TIMEOUT);
      if (errorCode != ErrorCode.OK) {
        DriverStation.reportError("Problem reseting " + c.getName() + " in subsystem " + getSubsystem(), false);
      }
    }
  }
}