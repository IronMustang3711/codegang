package frc.robot.subsystems;

import java.util.Objects;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.commands.ResetSensors;
import frc.robot.stuff.SensorReset;
import frc.robot.stuff.TalonFaultsReporter;

public class IntakeSubsystem extends SubsystemBase implements SensorReset {
  private WPI_TalonSRX controller = new WPI_TalonSRX(15);
  DigitalInput photoEye;
  //TODO: perhaps it might be better to put this in the FeederSubsystem?
  DigitalInput photoEye2;


  public IntakeSubsystem() {
    addChild("intakeController", controller);
    TalonFaultsReporter.instrument(controller);
    controller.configFactoryDefault();
    controller.setSafetyEnabled(false);
    photoEye = new DigitalInput(0);
    photoEye2 = new DigitalInput(1);
    setupShuffleboard();
  }

  @Override
  public void periodic() {

  }



  public boolean photoeye1Blocked() {
    return photoEye.get();
  }

  public boolean photoeye2Blocked() {
    return photoEye2.get();
  }

  private void setupShuffleboard() {
    var tab = Shuffleboard.getTab(IntakeSubsystem.class.getSimpleName());
    tab.add(controller);
    tab.addNumber("controller_current", controller::getStatorCurrent);
    tab.addNumber("position", this::getEncoderPosition);
    tab.addNumber("velocity", this::getEncoderVelocity);
    tab.addBoolean("photoeye 1 blocked", this::photoeye1Blocked);
    tab.addBoolean("photoeye 2 blocked", this::photoeye2Blocked);
    tab.add(new ResetSensors<>(this));
    tab.addString("current command", ()-> Objects.toString(getCurrentCommand()));

  }

  private double getEncoderVelocity() {
    return controller.getSelectedSensorVelocity();
  }

  double getEncoderPosition() {
    return controller.getSelectedSensorPosition();
  }

  public void enableIntake(boolean enable) {
    if (enable)
      set(1.0);
    else
      set(0.0);
  }

  public void set(double output) {
    controller.set(output);
  }

  public boolean isEnabled() {
    return controller.get() != 0.0;
  }

  @Override
  public void resetSensors() {
    //todo: this was not used in previous code. why?
    controller.getSensorCollection().setQuadraturePosition(0, Constants.TalonConstants.DEFAULT_TIMEOUT);

    ErrorCode errorCode = controller.setSelectedSensorPosition(0);
    if (errorCode != ErrorCode.OK) {
      DriverStation.reportError("error resetting encoders for subsystem " + IntakeSubsystem.class.getSimpleName(),
                                false);
      //TODO if we get here, try again?
    }
  }
}