package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.commands.ResetSensors;
import frc.robot.stuff.InfeedPhotoeyeObserver;
import frc.robot.stuff.SensorReset;
import frc.robot.stuff.TalonFaultsReporter;

public class IntakeSubsystem extends SubsystemBase implements SensorReset {
  private WPI_TalonSRX controller = new WPI_TalonSRX(15);
  DigitalInput photoEye;
  boolean photoeyeBlocked = false;

  public InfeedPhotoeyeObserver photoeyeObserver;

  public IntakeSubsystem() {
    addChild("intakeController", controller);
    TalonFaultsReporter.instrument(controller);
    controller.configFactoryDefault();
    controller.setSafetyEnabled(false);
    photoEye = new DigitalInput(0);
    setupShuffleboard();
  }

  @Override
  public void periodic() {
    var oldVal = photoeyeBlocked;
    var newVal = photoeyeBlocked = photoEye.get();
    if (photoeyeObserver != null) {
      if (oldVal && !newVal)
        photoeyeObserver.onPhotoeyeUnblocked();

      else if (!oldVal && newVal)
        photoeyeObserver.onPhotoeyeBlocked();
    }
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
    tab.add(new ResetSensors<>(this));

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

  // facilitates the vomit button
  public void reverse(boolean enable) {
    if (enable)
      set(-1.0);
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
      //TODO ef we get here, try again?
    }
  }
}