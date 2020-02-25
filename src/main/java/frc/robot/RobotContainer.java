package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.commands.*;
import frc.robot.stuff.Danger;
import frc.robot.subsystems.*;

import java.util.List;

public class RobotContainer {
  Joystick joy = new Joystick(0);
  PowerDistributionPanel pdp = new PowerDistributionPanel();

  private final ChassisSubsystem chassis = new ChassisSubsystem();
  private final HookSubsystem hook = new HookSubsystem();
  private final IntakeSubsystem intake = new IntakeSubsystem();
  private final ShooterSubsystem shooter = new ShooterSubsystem();
  private final WinchSubsystem winch = new WinchSubsystem();
  private final FeederSubsystem feedworks = new FeederSubsystem();
  private final PizzaWheelSubsytem pizzaWheel = new PizzaWheelSubsytem(() -> pdp.getCurrent(5));

  final List<SubsystemBase> subsystems = List.of(chassis, hook, intake, shooter, winch, feedworks, pizzaWheel);

  private final DriveWithJoystick driveWithJoystick = new DriveWithJoystick(chassis, joy);

  double shooterOutput() {
    return -joy.getThrottle() * 0.25 + 0.75;
  }

  CommandBase feedworksSequencer;

  AutoFeed autoFeed = new AutoFeed(intake, feedworks);

  public RobotContainer() {
    configureButtonBindings();
    chassis.setDefaultCommand(driveWithJoystick);


    autoFeed.schedule(false);
    // intake.photoeyeObserver = autoFeed;


    feedworksSequencer = new WaitUntilCommand(intake::photoeye1Blocked)
                           .andThen(new RunInfeedPercentOutput(intake, 0.5)
                                      .alongWith(new RunFeedworksPercentOutput(feedworks, 1.0))
                                      .withTimeout(0.5));


    SmartDashboard.putData("feedworks thing", feedworksSequencer);
    SmartDashboard.putData(pdp);
    SmartDashboard.putData(CommandScheduler.getInstance());
    Danger.install();

//    CommandScheduler.getInstance().onCommandInitialize(c->DriverStation.reportWarning("cmd init: "+c, false));
//    CommandScheduler.getInstance().onCommandExecute(c->DriverStation.reportWarning("cmd exec: "+c, false));


    //setupCamera();
  }

  UsbCamera cam;

  private void setupCamera() {
    cam = CameraServer.getInstance().startAutomaticCapture();
    cam.setResolution(640, 480);
    cam.setFPS(30);
  }

  private void configureButtonBindings() {
    new JoystickButton(joy, 10).toggleWhenActive(new RunInfeedPercentOutput(intake, 1.0));

    new JoystickButton(joy, 2).whileHeld(new RunFeedworksPercentOutput(feedworks, 1.0));

    Command prevInfeedCommand;
    var shootSequence = new ParallelCommandGroup(
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

    shootSequence.setName("shoot_sequence");
    
    new JoystickButton(joy, 1).whileHeld(shootSequence);
    new JoystickButton(joy, 4).whileHeld(new RunHookPercentOutput(hook, -0.5));
    //new JoystickButton(joy, 4).whileHeld(
    //   new StartEndCommand(
    // )
    new JoystickButton(joy, 6).whileHeld(new RunHookPercentOutput(hook, 0.5));
    new JoystickButton(joy, 7).whileHeld(winch::winchForward).whenReleased(winch::winchDisable);
    new JoystickButton(joy, 8).whileHeld(winch::winchReverse).whenReleased(winch::winchDisable);
    new POVButton(joy, 0).whenPressed(
      new StartEndCommand(() -> pizzaWheel.anglerUp(), pizzaWheel::anglerNeutral, pizzaWheel)
      .withTimeout(1.0)
      .withInterrupt(() -> pizzaWheel.isAnglerBlocked()));
    new POVButton(joy, 180).whenPressed(
      new StartEndCommand(() -> pizzaWheel.anglerDown(), pizzaWheel::anglerNeutral, pizzaWheel)
    .withTimeout(1.0)
    .withInterrupt(() -> pizzaWheel.isAnglerBlocked()));
    new JoystickButton(joy, 5).whileHeld(new StartEndCommand(pizzaWheel::runSpinner, pizzaWheel::stopSpinner, pizzaWheel));

    new JoystickButton(joy, 9).whileHeld(new RunInfeedPercentOutput(intake, -1.0)
                                           .alongWith(new RunFeedworksPercentOutput(feedworks, -1.0)));

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    var shootSequence = new ParallelCommandGroup(
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

    var shootNScoot = new SequentialCommandGroup(
      shootSequence.withTimeout(3.0),
      new WaitCommand(0.5),
      new RunChassis(chassis).withTimeout(1.0));
      return shootNScoot;
  }

  public void testInit() {
    Runnable init = () -> chassis.arcadeDrive(0, 0);
    Runnable end = () -> { };

    var cmd = new StartEndCommand(init, end, chassis);
    cmd.schedule();

  }
}
