package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.*;
import frc.robot.subsystems.*;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.List;

public class RobotContainer {
  Joystick joy = new Joystick(0);

  private final ChassisSubsystem chassis = new ChassisSubsystem();
  private final HookSubsystem hook = new HookSubsystem();
  private final IntakeSubsystem intake = new IntakeSubsystem();
  private final ShooterSubsystem shooter = new ShooterSubsystem();
  private final WinchSubsystem winch = new WinchSubsystem();
  private final FeederSubsystem feedworks = new FeederSubsystem();
  private final PizzaWheelSubsytem pizzaWheel = new PizzaWheelSubsytem();

  final List<SubsystemBase> subsystems = List.of(chassis, hook, intake, shooter, winch, feedworks, pizzaWheel);

  private final DriveWithJoystick driveWithJoystick = new DriveWithJoystick(chassis, joy);

//  private final CommandsForTesting testingCommands = new CommandsForTesting(intake, feedworks, shooter,
//                                                                            this::shooterOutput);

  double shooterOutput() {
    return -joy.getThrottle() * 0.25 + 0.5;
  }

  CommandBase feedworksSequencer;

  AutoFeed autoFeed = new AutoFeed(intake, feedworks);

  public RobotContainer() {
    configureButtonBindings();
    chassis.setDefaultCommand(driveWithJoystick);

    intake.photoeyeObserver = autoFeed;


    var runInfeedHalfSpeed = new RunCommand(() -> intake.set(0.5), intake);
    var runFeedworks = new RunFeedworksPercentOutput(feedworks, 1.0);

//    var stopBoth = new RunCommand(() -> {
//      intake.enableIntake(false);
//      feedworks.enable(false);
//    });

    var runFor1Sec = runInfeedHalfSpeed.alongWith(runFeedworks).withTimeout(0.5);
    feedworksSequencer = new WaitUntilCommand(intake::photoeyeBlocked)
                           .andThen(runFor1Sec);


    SmartDashboard.putData("feedworks thing", feedworksSequencer);

    CommandScheduler.getInstance().onCommandInitialize(c->DriverStation.reportWarning("cmd init: "+c, false));
    CommandScheduler.getInstance().onCommandExecute(c->DriverStation.reportWarning("cmd exec: "+c, false));


    //setupCamera();
    stopComplainingAboutJoysticsBeingUnplugged();
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
    var shootSequence = new ParallelCommandGroup(
      new RunShooterPercentOutput(shooter, this::shooterOutput),
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
    new JoystickButton(joy, 4).whileHeld(new RunHookPercentOutput(hook, -0.25));
    new JoystickButton(joy, 6).whileHeld(new RunHookPercentOutput(hook, 0.25));
    new JoystickButton(joy, 7).whileHeld(winch::winchForward).whenReleased(winch::winchDisable);
    new JoystickButton(joy, 8).whileHeld(winch::winchReverse).whenReleased(winch::winchDisable);

    new JoystickButton(joy, 9).whileHeld(new RunInfeedPercentOutput(intake, -1.0)
                                           .alongWith(new RunFeedworksPercentOutput(feedworks, -1.0)));

  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return driveWithJoystick;
  }

  public void testInit() {
    Runnable init = () -> chassis.arcadeDrive(0, 0);
    Runnable end = () -> { };

    var cmd = new StartEndCommand(init, end, chassis);
    cmd.schedule();

  }

  static void stopComplainingAboutJoysticsBeingUnplugged() {
    try {
      Field nextMessageTime = DriverStation.class.getDeclaredField("m_nextMessageTime");
      nextMessageTime.setAccessible(true);
      nextMessageTime.setDouble(DriverStation.getInstance(), Double.MAX_VALUE);
    } catch (NoSuchFieldException | IllegalAccessException | InaccessibleObjectException e) {
      e.fillInStackTrace();
      var ccw = new CharArrayWriter(120);
      e.printStackTrace(new PrintWriter(ccw));
      DriverStation.reportError(new String(ccw.toCharArray()), e.getStackTrace());

    }
  }
}
