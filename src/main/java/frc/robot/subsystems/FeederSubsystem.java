package frc.robot.subsystems;

import java.util.List;
import java.util.stream.Stream;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.commands.ResetSensors;
import frc.robot.stuff.SensorReset;
import frc.robot.stuff.TalonFaultsReporter;

import static frc.robot.Constants.*;

public class FeederSubsystem extends SubsystemBase implements SensorReset {

  public final WPI_TalonSRX controller1 = new WPI_TalonSRX(1);
  public final WPI_TalonSRX controller2 = new WPI_TalonSRX(4);

  public FeederSubsystem() {
    addChild("feeder1", controller1);
    addChild("feeder2", controller2);

    for (var talon : List.of(controller1, controller2)) {
      talon.setSafetyEnabled(false);
      talon.setExpiration(0.5);

      TalonFaultsReporter.instrument(talon);

      talon.configFactoryDefault();

      talon.configNeutralDeadband(0.04);
      talon.configNominalOutputForward(0.0); //0.15
      talon.configNominalOutputReverse(0.0); //-0.15

      talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,
                                         TalonConstants.PRIMARY_PID,
                                         TalonConstants.DEFAULT_TIMEOUT);
      talon.setSelectedSensorPosition(0, TalonConstants.PRIMARY_PID,
                                      TalonConstants.DEFAULT_TIMEOUT);
    }
    controller1.setSensorPhase(false);
    controller2.setSensorPhase(false);

    controller1.setInverted(true);
    //controller2.follow(controller1);

    controller2.setInverted(true);
    // controller2.setInverted(InvertType.FollowMaster);
    setupShuffleboard();
  }

  private void setupShuffleboard() {
    var tab = Shuffleboard.getTab(FeederSubsystem.class.getSimpleName());
    tab.add(controller1);
    tab.add(controller2);
    tab.addNumber("feeder1_pos", this::getFirstEncoderPosition);
    tab.addNumber("feeder2_pos", this::getSecondEncoderPosition);
    tab.addNumber("feeder1_vel", this::getFirstEncoderVelocity);
    tab.addNumber("feeder2_vel", this::getSecondEncoderVelocity);
    tab.addNumber("feeder1_current", controller1::getStatorCurrent);
    tab.addNumber("feeder2_current", controller2::getStatorCurrent);
    tab.add(new ResetSensors<>(this));

  }

  public double getFirstEncoderPosition() {
    return controller1.getSelectedSensorPosition();
  }

  public double getSecondEncoderPosition() {
    return controller2.getSelectedSensorPosition();
  }

  /**
   * @return sensor velocity (in raw sensor units) per 100ms.
   */
  public double getFirstEncoderVelocity() {
    return controller1.getSelectedSensorVelocity(TalonConstants.PRIMARY_PID);
  }

  public double getSecondEncoderVelocity() {
    return controller2.getSelectedSensorVelocity(TalonConstants.PRIMARY_PID);
  }

  @Override
  public void periodic() {
    // SmartDashboard.putNumber("colon1_position", getFirstEncoderPosition());
    // SmartDashboard.putNumber("colon2_position", getSecondEncoderPosition());
    // SmartDashboard.putNumberArray("colon_positions",
    //                               new double[]{getFirstEncoderPosition(), getSecondEncoderPosition()});
    // SmartDashboard.putNumber("colon1_velocity", getFirstEncoderVelocity());
    // SmartDashboard.putNumber("colon2_velocity", getSecondEncoderVelocity());
    // SmartDashboard.putNumberArray("colon_velocities(sp?)",
    //                               new double[]{getFirstEncoderVelocity(), getSecondEncoderVelocity()});

  }

  public void enable(boolean enable) {
    if (enable) {
      controller1.set(1.0);
      controller2.set(1.0);
    } else {
      controller1.set(0);
      controller2.set(0);
    }
  }
  
  // facilitates the vomit button
  public void reverse(boolean enable) {
    if(enable) {
      controller1.set(-1.0);
      controller2.set(-1.0);
    } else {
      controller1.set(0.0);
      controller2.set(0.0);
    }
  }

  public boolean isEnabled() {
    return controller1.getMotorOutputVoltage() > 0.0 && controller2.getMotorOutputVoltage() > 0.0;
  }

  @Override
  public void resetSensors() {
    for (var c : List.of(controller1, controller2)) {
      ErrorCode errorCode = c.getSensorCollection().setQuadraturePosition(0, TalonConstants.DEFAULT_TIMEOUT);
      if (errorCode != ErrorCode.OK) {
        DriverStation.reportError("Problem reseting " + c.getName() + " in subsystem " + getSubsystem(), false);
      }
    }
  }
}
