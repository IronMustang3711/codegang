package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.FeederSubsystem;

public class RunFeedworksPercentOutput extends CommandBase {
  final FeederSubsystem feedworks;
  final double pctOut1;
  final double pctOut2;

  public RunFeedworksPercentOutput(FeederSubsystem feedworks, double pctOut) {
    this(feedworks, pctOut, pctOut);
  }

  public RunFeedworksPercentOutput(FeederSubsystem feedworks, double pctOut1, double pctOut2) {
    this.feedworks = feedworks;
    this.pctOut1 = pctOut1;
    this.pctOut2 = pctOut2;
    addRequirements(feedworks);
  }

  @Override
  public void execute() {
    feedworks.setMotorOutputs(pctOut1, pctOut2);
  }

  @Override
  public void end(boolean interrupted) {
    feedworks.controller1.neutralOutput();
    feedworks.controller2.neutralOutput();
  }
}
