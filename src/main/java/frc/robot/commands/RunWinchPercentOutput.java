package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.WinchSubsystem;

public class RunWinchPercentOutput extends CommandBase {
  final WinchSubsystem winchSubsystem;
  final double output;

  public RunWinchPercentOutput(WinchSubsystem winchSubsystem, double output) {
    this.winchSubsystem = winchSubsystem;
    this.output = output;
    addRequirements(winchSubsystem);
  }

  @Override
  public void execute() {
    winchSubsystem.setOutput(output);

  }

  @Override
  public void end(boolean interrupted) {
    winchSubsystem.setOutput(0.0);
  }
}
