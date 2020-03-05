package frc.robot.commands;

import frc.robot.subsystems.ChassisSubsystem;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class DriveWithJoystick extends CommandBase {
  private final ChassisSubsystem chassis;
  private Joystick joy;
  private static final double DEFAULT_FWD = 0.8;
  private static final double DEFAULT_ROT = 0.9;
  private double fwdMultiplier = DEFAULT_FWD;
  private double rotateMultiplier = DEFAULT_ROT;

  public void enableMaxSpeed(boolean enable) {
    if (enable) {
      fwdMultiplier = 1.0;
      rotateMultiplier = 1.0;
    } else {
      fwdMultiplier = DEFAULT_FWD;
      rotateMultiplier = DEFAULT_ROT;
    }
  }

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
    double rot = joy.getTwist() * -1.0; 
    double fwdOut = -fwdMultiplier * fwd;
    double rotOut = -rotateMultiplier * rot;
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
