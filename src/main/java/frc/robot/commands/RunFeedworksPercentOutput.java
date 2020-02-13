package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.subsystems.FeederSubsystem;

import java.util.function.Function;

public class RunFeedworksPercentOutput extends CommandBase {
  public static Command runFirstFeeder(FeederSubsystem feedworks, double out) {
    return new RunSingleFeederPercentOutput(feedworks, FeederSubsystem::getController1, out);
  }

  public static Command runSecondFeeder(FeederSubsystem feedworks, double out) {
    return new RunSingleFeederPercentOutput(feedworks, FeederSubsystem::getController2, out);
  }

  public static Command runFeedworks(FeederSubsystem feedworks, double feeder1, double feeder2) {
    return new RunFeedworksPercentOutput2(feedworks, feeder1, feeder2);
  }

  static class RunSingleFeederPercentOutput extends CommandBase {
    double pctOut;
    WPI_TalonSRX talon;

    RunSingleFeederPercentOutput(FeederSubsystem feeder,
                                 Function<FeederSubsystem, WPI_TalonSRX> talonSRXFunction,
                                 double pctOut) {
      addRequirements(feeder);
      talon = talonSRXFunction.apply(feeder);
      this.pctOut = pctOut;
    }

    @Override
    public void execute() {
      talon.set(pctOut);
    }

    @Override
    public void end(boolean interrupted) {
      talon.set(0.0);
    }
  }

  static class RunFeedworksPercentOutput2 extends ParallelCommandGroup {
    RunFeedworksPercentOutput2(FeederSubsystem feedworks, double amt) {
      this(feedworks, amt, amt);
    }

    RunFeedworksPercentOutput2(FeederSubsystem feedworks, double feeder1, double feeder2) {
      super(runFirstFeeder(feedworks, feeder1),
            runSecondFeeder(feedworks, feeder2));
    }
  }

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
    feedworks.getController1().neutralOutput();
    feedworks.getController2().neutralOutput();
  }
}
