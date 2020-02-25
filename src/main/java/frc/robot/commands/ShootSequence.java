package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.*;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class ShootSequence {
  /**
   * shoot sequence:
   * <p>
   * 1. Start spinning up the shooter.
   * 2. disable infeed & feedworks until shooter is at speed.
   * 3. When Shooter is ready, start feedworks immediately and intake after delay.
   * <p>
   * TODO: this should be changed so that the feedworks pauses whenever the shooter is below target velocity
   * TODO: this command doesnt have a 'finished' condition. For Now, just use a timeout to end it
   * NOTE: feedworks output % can be set per-motor if necessary
   */
  public static Command createShootSequence(ShooterSubsystem shooter, IntakeSubsystem intake, FeederSubsystem feedworks) {

    var runShooter = new ShooterVelocityControl(shooter);

    // Stop infeed & feedworks. These commands are active for 1 loop.
    var disableIntake = new InstantCommand(() -> intake.enableIntake(false), intake);
    var disableFeedworks = new InstantCommand(() -> feedworks.setMotorOutputs(0.0), feedworks);
    var stopBallControl = new ParallelCommandGroup(disableIntake, disableFeedworks);

    //TODO: This should be replaced with a command that monitors shooter speed and finishes when is at the correct velocity.
    var theCommandThatWillEndWhenTheShooterIsReady = new WaitUntilCommand(1.0);

    var runFeedWorksForShooting = new RunFeedworksPercentOutput(feedworks, 1.0);
    //TODO: i'm not sure if the delay is necessary.
    var runInfeedForShootingAfterDelay = new WaitCommand(0.3).andThen(new RunInfeedPercentOutput(intake, 0.5));
    var theCommandToRunWhenTheShooterIsReady = runFeedWorksForShooting.alongWith(runInfeedForShootingAfterDelay);

    var ballControlSequence = stopBallControl.andThen(theCommandThatWillEndWhenTheShooterIsReady)
                                             .andThen(theCommandToRunWhenTheShooterIsReady);

    //start shooter & ball control sequece
    var theFinalCommand = runShooter.alongWith(ballControlSequence);
    theFinalCommand.setName("shoot_sequence");

    return theFinalCommand;
  }
  /*original structure:

  new ParallelCommandGroup(
      // new RunShooterPercentOutput(shooter, this::shooterOutput),
      new ShooterVelocityControl(shooter),
      new SequentialCommandGroup(
        new ParallelCommandGroup(
          new InstantCommand(() -> {
            intake.enableIntake(false);
            feedworks.setMotorOutputs(0.0);
          }, intake, feedworks),
          new WaitCommand(1.0)),
        new ParallelCommandGroup(new RunFeedworksPercentOutput(feedworks, 1.0),
                                 new WaitCommand(0.3).andThen(new RunInfeedPercentOutput(intake, 0.5)))));
   */
}
