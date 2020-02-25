package frc.robot.commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.stuff.InfeedPhotoeyeObserver;
import frc.robot.stuff.ShooterPhotoeyeObserver;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;

public class AutoFeed extends CommandBase implements InfeedPhotoeyeObserver, ShooterPhotoeyeObserver {

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
    var infeedHalfSpeed2 = new RunInfeedPercentOutput(infeedSubsystem, 0.4);
    var feedworksCmd = new RunFeedworksPercentOutput(feedworks, 0.5, 0.4);
    Command infeedAndFeedworks = new ParallelCommandGroup(infeedHalfSpeed, feedworksCmd).withTimeout(1.0);
    if (prevInfeedCommand != null)
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

  Command createAutoFeedCommandSequence2() {
    var slowInfeed = new RunInfeedPercentOutput(infeedSubsystem, 0.4).withTimeout(0.8);
    var feeder1 = RunFeedworksPercentOutput.runFirstFeeder(feedworks, 0.5).withTimeout(0.8);
    var feeder2 = RunFeedworksPercentOutput.runSecondFeeder(feedworks, 0.6).withTimeout(0.4);
    Command cmd = slowInfeed.alongWith(feeder1).alongWith(feeder2);
    if (prevInfeedCommand != null)
      cmd = cmd.andThen(new ScheduleCommand(prevInfeedCommand));
    return cmd;
  }

  Command createAutoFeedCommandSequence3() {
    var slowInfeed = new RunInfeedPercentOutput(infeedSubsystem, 1.0);
    BooleanSupplier y = () -> photoeye1Blocked = true;
    
    Command cmd2 = slowInfeed.withInterrupt(y);
    return cmd2;
  }

  @Override
  public void onPhotoeye1Blocked() {
    prevInfeedCommand = infeedSubsystem.getCurrentCommand();
    if (prevInfeedCommand != null)
      prevInfeedCommand.cancel();
    autoFeedCommand = createAutoFeedCommandSequence();
    autoFeedCommand.schedule();
  }

  @Override
  public void onPhotoeye1Unblocked() {
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
  //boolean photoeye2Blocked = false;

  @Override
  public void execute() {
    var prev = photoeye1Blocked;
    var cur = photoeye1Blocked = infeedSubsystem.photoeye1Blocked();
    if (!prev && cur) {
      onPhotoeye1Blocked();
    } else if (prev && !cur) {
      onPhotoeye1Unblocked();
    }
    /*
    var prev2 = photoeye2Blocked;
    var cur2 = photoeye2Blocked = infeedSubsystem.photoeye2Blocked();
    if (!prev2 && cur2) {
      onPhotoeye2Blocked();
    } else if (prev2 && !cur2) {
      onPhotoeye2Unblocked();
    }
    */
  }

  @Override
  public boolean runsWhenDisabled() {
    return true; //todo: necessary?
  }
}


