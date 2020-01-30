/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.commands.DriveWithJoystick;
import frc.robot.subsystems.*;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a "declarative" paradigm, very little robot logic should
 * actually be handled in the {@link Robot} periodic methods (other than the
 * scheduler calls). Instead, the structure of the robot (including subsystems,
 * commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  Joystick joy = new Joystick(0);
  // The robot's subsystems and commands are defined here...
  private final ChassisSubsystem chassis = new ChassisSubsystem();
  private final HookSubsystem hook = new HookSubsystem();
  private final IntakeSubsystem intake = new IntakeSubsystem();
  private final ColonSubsystem colon = new ColonSubsystem();
  private final PizzaWheelSubsytem pizzaWheel = new PizzaWheelSubsytem();

  private final DriveWithJoystick driveWithJoystick = new DriveWithJoystick(chassis, joy);


  public RobotContainer() {
    // Configure the button bindings
    configureButtonBindings();

    chassis.setDefaultCommand(driveWithJoystick);
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be
   * created by instantiating a {@link GenericHID} or one of its subclasses
   * ({@link edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then
   * passing it to a {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    new JoystickButton(joy, 11).whileHeld(() -> hook.setSetpoint(hook.getMeasurement() + 10));
    new JoystickButton(joy, 12).whileHeld(() -> hook.setSetpoint(hook.getMeasurement() - 10));
    new JoystickButton(joy, 2).whileHeld(() -> intake.enableIntake(true))
        .whenReleased(() -> intake.enableIntake(false));
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    return driveWithJoystick;
  }
}
