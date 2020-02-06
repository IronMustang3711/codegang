package frc.robot.commands;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class CommandsForTesting {
  public static class RunIntake extends CommandBase {
    private final IntakeSubsystem intake;
    private final double output;

    public RunIntake(IntakeSubsystem intake, double output) {
      this.intake = intake;
      this.output = output;
      setName(RunIntake.class.getSimpleName());
      addRequirements(intake);
      setSubsystem(intake.getSubsystem());
    }

    public RunIntake(IntakeSubsystem intake) {
      this(intake, 1.0);
    }

    @Override
    public void execute() {
      intake.set(output);
    }

    @Override
    public void end(boolean interrupted) {
      intake.enableIntake(false);
    }
  }

  public static class RunFeeder extends CommandBase {
    private final FeederSubsystem colon;

    public RunFeeder(FeederSubsystem colon) {
      this.colon = colon;
      setName(RunFeeder.class.getSimpleName());
      addRequirements(colon);
      setSubsystem(colon.getSubsystem());
    }

    @Override
    public void execute() {
      colon.enable(true);
    }

    @Override
    public void end(boolean interrupted) {
      colon.enable(false);
    }
  }

  public static class RunShooter extends CommandBase {
    private final ShooterSubsystem shooter;

    public RunShooter(ShooterSubsystem shooter) {
      this.shooter = shooter;
      setName(RunShooter.class.getSimpleName());
      addRequirements(shooter);
      setSubsystem(shooter.getSubsystem());
    }

    @Override
    public void execute() {
      shooter.enableShooter(true);
    }

    @Override
    public void end(boolean interrupted) {
      shooter.enableShooter(false);
    }
  }

  public final RunIntake intakeRunner;
  public final RunFeeder colonRunner;
  public final RunShooter shooterRunner;

  public CommandsForTesting(IntakeSubsystem intakeSubsystem, FeederSubsystem colonSubsystem, ShooterSubsystem shooterSubsystem) {
    intakeRunner = new RunIntake(intakeSubsystem);
    colonRunner = new RunFeeder(colonSubsystem);
    shooterRunner = new RunShooter(shooterSubsystem);
  }
}
