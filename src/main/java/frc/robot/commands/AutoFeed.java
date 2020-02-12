package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.StartEndCommand;
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

    var infeedHalfSpeed = new RunInfeedPercentOutput(infeedSubsystem, 0.5);
    var feedworksCmd = new RunFeedworksPercentOutput(feedworks, 0.5, 1.0);
    Command infeedAndFeedworks = new ParallelCommandGroup(infeedHalfSpeed, feedworksCmd).withTimeout(0.5);
    if (prevInfeedCommand != null) infeedAndFeedworks = infeedAndFeedworks.andThen(prevInfeedCommand);
  
   return infeedAndFeedworks.alongWith(new StartEndCommand(()->{
      System.out.println("start");
    }
    ,
    ()->{
      System.out.println("end");
    }));
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
    // if(prevInfeedCommand != null) prevInfeedCommand.schedule();
  }


}


