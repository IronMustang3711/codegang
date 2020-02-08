package frc.robot.commands;

import edu.wpi.first.wpilibj.SlewRateLimiter;
import frc.robot.subsystems.ChassisSubsystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;


public class DriveWithJoystick extends CommandBase {
  private final ChassisSubsystem chassis;
  private Joystick joy;


  public DriveWithJoystick(ChassisSubsystem subsystem, Joystick joy) {
    this.chassis = subsystem;
    this.joy = joy;
    addRequirements(subsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    double fwd = joy.getY();
    double rot = joy.getTwist() * -1.0; //TODO: verify that -1 makes sense.
    double fwdOut = -0.8 * fwd;
    double rotOut = -0.9 * rot;
    chassis.arcadeDrive(fwdOut, rotOut);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    chassis.arcadeDrive(0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
