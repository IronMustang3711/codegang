package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.*;
import frc.robot.stuff.InfeedPhotoeyeObserver;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;

public class AutoFeed implements InfeedPhotoeyeObserver {

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
    Command infeedAndFeedworks = new ParallelCommandGroup(infeedHalfSpeed, feedworksCmd).withTimeout(0.8);
    if (prevInfeedCommand != null)
      infeedAndFeedworks = infeedAndFeedworks.andThen(new ScheduleCommand(prevInfeedCommand));

    return infeedAndFeedworks;
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
}


