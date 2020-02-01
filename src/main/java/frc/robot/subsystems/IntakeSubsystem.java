package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.stuff.TalonFaultsReporter;

public class IntakeSubsystem extends SubsystemBase {
  private WPI_TalonSRX controller = new WPI_TalonSRX(15);

  public IntakeSubsystem() {
    addChild("intakeController", controller);
    controller.configFactoryDefault();
    TalonFaultsReporter.instrument(controller);
  }

  public void enableIntake(boolean enable) {
    if (enable)
      controller.set(1.0);
    else
      controller.set(0.0);
  }

  public boolean isEnabled() {
    return controller.get() != 0.0;
  }
}