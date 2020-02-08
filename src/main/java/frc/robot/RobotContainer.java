package frc.robot;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.*;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import frc.robot.commands.CommandsForTesting;
import frc.robot.commands.DriveWithJoystick;
import frc.robot.subsystems.*;

import static frc.robot.commands.CommandsForTesting.*;

import java.util.function.DoubleSupplier;

public class RobotContainer {
  Joystick joy = new Joystick(0);

  private final ChassisSubsystem chassis = new ChassisSubsystem();
  private final HookSubsystem hook = new HookSubsystem();
  private final IntakeSubsystem intake = new IntakeSubsystem();
  private final ShooterSubsystem shooter = new ShooterSubsystem();
  private final WinchSubsystem winch = new WinchSubsystem();
  private final FeederSubsystem feedworks = new FeederSubsystem();
  private final PizzaWheelSubsytem pizzaWheel = new PizzaWheelSubsytem();

  private final DriveWithJoystick driveWithJoystick = new DriveWithJoystick(chassis, joy);

  private final CommandsForTesting testingCommands = new CommandsForTesting(intake, feedworks, shooter,this::shooterOutput);

  double shooterOutput(){
    return -joy.getThrottle() * 0.25 + 0.5;
  }
  public RobotContainer() {
    configureButtonBindings();
    chassis.setDefaultCommand(driveWithJoystick);

    setupCamera();

//    CommandScheduler.getInstance().onCommandInitialize(c -> {
//      DriverStation.reportWarning("INIT: " + c.getName(), false);
//    });
//    CommandScheduler.getInstance().onCommandFinish(c -> {
//      DriverStation.reportWarning("FINISH: " + c.getName(), false);
//    });
    // CommandScheduler.getInstance().onCommandInterrupt(c -> {
    //   DriverStation.reportWarning("INTERRUPT: " + c.getName(), false);
    // });
  }

  UsbCamera cam;
  private void setupCamera(){
    cam = CameraServer.getInstance().startAutomaticCapture();
    cam.setResolution(640, 480);
    cam.setFPS(30);
  }

  private void configureButtonBindings() {
    new JoystickButton(joy, 10).toggleWhenActive(testingCommands.intakeRunner);
    new JoystickButton(joy, 2).whileHeld(testingCommands.colonRunner);
    var shootSequence =new ParallelCommandGroup(
      new RunShooter(shooter,this::shooterOutput),
      new SequentialCommandGroup(
        new ParallelCommandGroup(
          new InstantCommand(() -> {
            intake.enableIntake(false);
            feedworks.enable(false);
          }, intake, feedworks),
          new WaitCommand(1.0)),
        new ParallelCommandGroup(new RunFeeder(feedworks),
                                 new WaitCommand(0.3).andThen(new RunIntake(intake, 0.5)))));



    new JoystickButton(joy, 1).whileHeld(shootSequence);
    new JoystickButton(joy, 4).whileHeld(new StartEndCommand(() -> hook.setOutput(-0.25), () -> hook.setOutput(0.0), hook));
    new JoystickButton(joy, 6).whileHeld(new StartEndCommand(() -> hook.setOutput(0.25), () -> hook.setOutput(0.0), hook));
    new JoystickButton(joy, 7).whileHeld(winch::winchForward).whenReleased(() -> winch.winchDisable());
    new JoystickButton(joy, 8).whileHeld(winch::winchReverse).whenReleased(winch::winchDisable);

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
}
