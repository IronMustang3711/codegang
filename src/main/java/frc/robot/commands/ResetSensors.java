package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.stuff.SensorReset;

public class ResetSensors<T extends SubsystemBase & SensorReset> extends CommandBase {
  final T resettableSubsystem;

  public ResetSensors(T resettableSubsystem) {
    this.resettableSubsystem = resettableSubsystem;
    setName("SensorReset[" + resettableSubsystem.getName() + "]");
    addRequirements(resettableSubsystem);
  }

  @Override
  public void initialize() {
    resettableSubsystem.resetSensors();
  }

  @Override
  public boolean isFinished() {
    return true;
  }
}
