package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.HookSubsystem;

public class RunHookPercentOutput extends CommandBase {
  final HookSubsystem hookSubsystem;
  final double ouput;

  public RunHookPercentOutput(HookSubsystem hookSubsystem, double ouput) {
    this.hookSubsystem = hookSubsystem;
    this.ouput = ouput;
    addRequirements(hookSubsystem);
  }

  @Override
  public void execute() {
    hookSubsystem.setOutput(ouput);
  }

  @Override
  public void end(boolean interrupted) {
    hookSubsystem.setOutput(0.0);
  }
}
