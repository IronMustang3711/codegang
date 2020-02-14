package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.HookSubsystem;

public class HookPosition extends CommandBase {
  final HookSubsystem hook;
  public double setpoint;

  public HookPosition(HookSubsystem hook, double setpoint) {
    this.hook = hook;
    this.setpoint = setpoint;
    addRequirements(hook);
  }

  @Override
  public void initialize() {

  }

  @Override
  public void execute() {
    hook.setPosition(setpoint);

  }

  @Override
  public void end(boolean interrupted) {

  }

  @Override
  public boolean isFinished() {
    return false; //todo: check closed loop error here
  }
}
