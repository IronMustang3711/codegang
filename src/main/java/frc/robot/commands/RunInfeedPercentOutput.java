package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IntakeSubsystem;

public class RunInfeedPercentOutput extends CommandBase {
  final IntakeSubsystem intake;
  private double amt;

  public RunInfeedPercentOutput(IntakeSubsystem intake, double amt) {
    this.intake = intake;
    this.amt = amt;
    addRequirements(intake);
  }

  @Override
  public void execute() {
    intake.set(amt);
  }

  @Override
  public void end(boolean interrupted) {
    intake.set(0.0);
  }
}
