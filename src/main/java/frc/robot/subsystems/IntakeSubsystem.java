package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.stuff.TalonFaultsReporter;

public class IntakeSubsystem extends SubsystemBase {
  private WPI_TalonSRX controller = new WPI_TalonSRX(15);
  DigitalInput photoEye;

  public IntakeSubsystem() {
    addChild("intakeController", controller);
    TalonFaultsReporter.instrument(controller);
    controller.configFactoryDefault();
    controller.setSafetyEnabled(false);
    photoEye = new DigitalInput(0);
    setupShuffleboard();
  }

  public boolean photoeyeBlocked() {
    return photoEye.get();
  }

  private void setupShuffleboard() {
    var tab = Shuffleboard.getTab(IntakeSubsystem.class.getSimpleName());
    tab.add(controller);
    tab.addNumber("controller_current", controller::getStatorCurrent);
    tab.addNumber("position", this::getEncoderPosition);
    tab.addNumber("velocity", this::getEncoderVelocity);
    tab.addBoolean("photoeye blocked", this::photoeyeBlocked);
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
}