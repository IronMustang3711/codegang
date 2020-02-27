package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;

public class AutoFeed extends CommandBase  {

  final IntakeSubsystem infeedSubsystem;
  final FeederSubsystem feedworks;
  Command prevInfeedCommand;

  Command autoFeedCommand;

  public AutoFeed(IntakeSubsystem infeedSubsystem, FeederSubsystem feedworks) {
    this.infeedSubsystem = infeedSubsystem;
    this.feedworks = feedworks;
  }

  Command createAutoFeedCommandSequence() {

    var infeedHalfSpeed = new RunInfeedPercentOutput(infeedSubsystem, 0.4);
    var feedworksCmd = new RunFeedworksPercentOutput(feedworks, 0.5, 0.4);
    Command infeedAndFeedworks = new ParallelCommandGroup(infeedHalfSpeed, feedworksCmd).withTimeout(1.0);
    //only restart the previous command if it was an instance of RunInfeedPercentOutput (e.g. not this or the shoot sequence)
    if (prevInfeedCommand != null && prevInfeedCommand instanceof RunInfeedPercentOutput)
      infeedAndFeedworks = infeedAndFeedworks.andThen(new ScheduleCommand(prevInfeedCommand));

    // Command reducedInfeed = infeedHalfSpeed2.withTimeout(0.5);
    // if (photoeye2Blocked) {
    //   return reducedInfeed;
    // } 
    // else if (!photoeye2Blocked) {
      return infeedAndFeedworks;
    // } 
    // else {
    //   return new RunInfeedPercentOutput(infeedSubsystem, 0.0);
    // }
  }

  // Command createAutoFeedCommandSequence2() {
  //   var slowInfeed = new RunInfeedPercentOutput(infeedSubsystem, 0.4).withTimeout(0.8);
  //   var feeder1 = RunFeedworksPercentOutput.runFirstFeeder(feedworks, 0.5).withTimeout(0.8);
  //   var feeder2 = RunFeedworksPercentOutput.runSecondFeeder(feedworks, 0.6).withTimeout(0.4);
  //   Command cmd = slowInfeed.alongWith(feeder1).alongWith(feeder2);
  //   if (prevInfeedCommand != null)
  //     cmd = cmd.andThen(new ScheduleCommand(prevInfeedCommand));
  //   return cmd;
  // }

  // Command createAutoFeedCommandSequence3() {
  //   var slowInfeed = new RunInfeedPercentOutput(infeedSubsystem, 1.0);
  //   BooleanSupplier y = () -> photoeye1Blocked = true;
    
  //   Command cmd2 = slowInfeed.withInterrupt(y);
  //   return cmd2;
  // }


  public void infeedPhotoEye1Blocked() {
    prevInfeedCommand = infeedSubsystem.getCurrentCommand();
    if (prevInfeedCommand != null)
      prevInfeedCommand.cancel();
    autoFeedCommand = createAutoFeedCommandSequence();
    autoFeedCommand.schedule();
  }

  public void infeedphotoeye1Unblocked() {
    assert (autoFeedCommand != null);
    double elapsed = CommandScheduler.getInstance().timeSinceScheduled(autoFeedCommand);
    if (elapsed != -1.0)
      DriverStation.reportWarning("photoeye 1 unblocked " + elapsed + "s after autofeed start", false);
  }

 /*
  @Override
  public void onPhotoeye2Blocked() {
    prevInfeedCommand = infeedSubsystem.getCurrentCommand();
     if (prevInfeedCommand != null)
     //This bad boy right here is causing the feeder to stop during the shoot sequence
     //TODO: this is likely because the prevInfeedCommand is this command.
     //TODO: restarting the previous command is maybe a bad idea.
     //The goal of restarting the previous command was to restart the infeed if was previously enabled
     //It may be simpler to just remove this and rely on the driver to restart it.
       prevInfeedCommand.cancel();
    autoFeedCommand = createAutoFeedCommandSequence3();
    autoFeedCommand.schedule();
  }

  @Override
  public void onPhotoeye2Unblocked() {
    assert (autoFeedCommand != null);
    double elapsed = CommandScheduler.getInstance().timeSinceScheduled(autoFeedCommand);
    if (elapsed != -1.0)
      DriverStation.reportWarning("photoeye 2 unblocked " + elapsed + "s after autofeed start", false);
  }
 */

  boolean photoeye1Blocked = false;
  boolean photoeye2Blocked = false;

  @Override
  public void execute() {
    var prevPhotoeye1 = photoeye1Blocked;
    var currPhotoEye1 = photoeye1Blocked = infeedSubsystem.photoeye1Blocked();
    if (!prevPhotoeye1 && currPhotoEye1) {
      infeedPhotoEye1Blocked();
    } else if (prevPhotoeye1 && !currPhotoEye1) {
      infeedphotoeye1Unblocked();
    }

    var prevPhotoeye2 = photoeye2Blocked;
    var currPhotoEye2 = photoeye2Blocked = infeedSubsystem.photoeye2Blocked();
    if (!prevPhotoeye2 && currPhotoEye2) {
      infeedPhotoEye2Blocked();
    } else if (prevPhotoeye1 && !currPhotoEye1) {
      infeedPhotoEye2Unblocked();
    }

  }

  //TODO: if necessary, put code that should be run when the second photoeye changes state here
  private void infeedPhotoEye2Unblocked() {
  }

  private void infeedPhotoEye2Blocked() {
    //1.) dont run feeder
    //2.) run intake until photoeye 1 blocked.
    //3.) Queue is full. stop everything.
  }

  @Override
  public boolean runsWhenDisabled() {
    return true; //todo: necessary?
  }
}


