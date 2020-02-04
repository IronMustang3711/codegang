package frc.robot.commands;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableRegistry;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ColonSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class CommandsForTesting {
  public static class RunIntake extends CommandBase {
    private final IntakeSubsystem intake;

    public RunIntake(IntakeSubsystem intake) {
      this.intake = intake;
      setName(RunIntake.class.getSimpleName());
      addRequirements(intake);
      setSubsystem(intake.getSubsystem());
    }

    @Override
    public void execute() {
      intake.enableIntake(true);
    }

    @Override
    public void end(boolean interrupted) {
      intake.enableIntake(false);
    }
  }

  public static class RunColon extends CommandBase {
    private final ColonSubsystem colon;

    public RunColon(ColonSubsystem colon) {
      this.colon = colon;
      setName(RunColon.class.getSimpleName());
      addRequirements(colon);
      setSubsystem(colon.getSubsystem());
    }

    @Override
    public void execute() {
      colon.enableColon(true);
    }

    @Override
    public void end(boolean interrupted) {
      colon.enableColon(false);
    }
  }

  public static class RunShooter extends CommandBase {
    private final ShooterSubsystem shooter;

    RunShooter(ShooterSubsystem shooter) {
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
  public final RunColon colonRunner;
  public final RunShooter shooterRunner;

  public CommandsForTesting(IntakeSubsystem intakeSubsystem, ColonSubsystem colonSubsystem, ShooterSubsystem shooterSubsystem) {
    intakeRunner = new RunIntake(intakeSubsystem);
    colonRunner = new RunColon(colonSubsystem);
    shooterRunner = new RunShooter(shooterSubsystem);
    // setupShuffleboardTab();
    // addToLiveWindow();
  }

  void setupShuffleboardTab() {
    var tab = Shuffleboard.getTab("commands");
    tab.add(intakeRunner);
    tab.add(colonRunner);
    tab.add(shooterRunner);
  }

  void addToLiveWindow() {
    SendableRegistry.addLW(intakeRunner, intakeRunner.getSubsystem(), intakeRunner.getName());
    SendableRegistry.addLW(colonRunner, colonRunner.getSubsystem(), colonRunner.getName());
    SendableRegistry.addLW(shooterRunner, shooterRunner.getSubsystem(), shooterRunner.getName());
  }

}
