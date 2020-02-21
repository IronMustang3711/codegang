package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.stuff.InfeedPhotoeyeObserver;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;

public class AutoFeed extends CommandBase implements InfeedPhotoeyeObserver {

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
    if (prevInfeedCommand != null)
      infeedAndFeedworks = infeedAndFeedworks.andThen(new ScheduleCommand(prevInfeedCommand));

    return infeedAndFeedworks;
  }

  Command createAutoFeedCommandSequence2() {
    var slowInfeed = new RunInfeedPercentOutput(infeedSubsystem, 0.4).withTimeout(0.8);
    var feeder1 = RunFeedworksPercentOutput.runFirstFeeder(feedworks, 0.5).withTimeout(0.8);
    var feeder2 = RunFeedworksPercentOutput.runSecondFeeder(feedworks, 0.6).withTimeout(0.4);
    Command cmd = slowInfeed.alongWith(feeder1).alongWith(feeder2);
    if (prevInfeedCommand != null) cmd = cmd.andThen(new ScheduleCommand(prevInfeedCommand));
    return cmd;
  }

  @Override
  public void onPhotoeyeBlocked() {
    prevInfeedCommand = infeedSubsystem.getCurrentCommand();
    if (prevInfeedCommand != null) prevInfeedCommand.cancel();
    autoFeedCommand = createAutoFeedCommandSequence();
    autoFeedCommand.schedule();
  }

  @Override
  public void onPhotoeyeUnblocked() {
    assert (autoFeedCommand != null);
    double elapsed = CommandScheduler.getInstance().timeSinceScheduled(autoFeedCommand);
    if (elapsed != -1.0)
      DriverStation.reportWarning("photoeye unblocked " + elapsed + "s after autofeed start", false);
  }

  boolean photoeyeBlocked = false;

  @Override
  public void execute() {
    var prev = photoeyeBlocked;
    var cur = photoeyeBlocked = infeedSubsystem.photoeyeBlocked();
    if (!prev && cur) {
      onPhotoeyeBlocked();
    } else if (prev && !cur) {
      onPhotoeyeUnblocked();
    }
  }

  @Override
  public boolean runsWhenDisabled() {
    return true; //todo: necessary?
  }
}


