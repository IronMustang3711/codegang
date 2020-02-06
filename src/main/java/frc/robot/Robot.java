/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.PerpetualCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
 // private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;
  private long lastUpdateTime;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();
    lastUpdateTime = System.nanoTime();
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    long start = System.nanoTime();
    CommandScheduler.getInstance().run();
    long stop = System.nanoTime();

    double run_millis = (stop - start) / 1e9;
    SmartDashboard.putNumber("runLoopTime(ms)", run_millis);

    double periodic_millis = (start - lastUpdateTime) / 1e9;
    SmartDashboard.putNumber("periodicTime(ms)", periodic_millis);
    lastUpdateTime = start;

  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   */
  @Override
  public void disabledInit() {
    Shuffleboard.stopRecording();
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    Shuffleboard.startRecording();
    CommandScheduler.getInstance().enable();

//    m_autonomousCommand = m_robotContainer.getAutonomousCommand();
//
//    // schedule the autonomous command (example)
//    if (m_autonomousCommand != null) {
//      m_autonomousCommand.schedule();
//    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    Shuffleboard.startRecording();
    CommandScheduler.getInstance().enable();

    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
//    if (m_autonomousCommand != null) {
//      m_autonomousCommand.cancel();
//    }
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
    m_robotContainer.testInit(); //TODO: this was never called on monday
    CommandScheduler.getInstance().cancelAll();
    CommandScheduler.getInstance().run();
    CommandScheduler.getInstance().disable();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
//    var scheduler = CommandScheduler.getInstance();
//    scheduler.cancelAll();
  }
}
