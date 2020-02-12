package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ShooterSubsystem;

import java.util.function.DoubleSupplier;

public class RunShooterPercentOutput extends CommandBase {
  final ShooterSubsystem shooter;
  final DoubleSupplier outputSupplier;

  public RunShooterPercentOutput(ShooterSubsystem shooter, DoubleSupplier outputSupplier) {
    this.shooter = shooter;
    this.outputSupplier = outputSupplier;
    addRequirements(shooter);
  }

  @Override
  public void execute() {
    shooter.runShooter(outputSupplier.getAsDouble());
  }

  @Override
  public void end(boolean interrupted) {
    shooter.runShooter(0.0);
  }
}
