/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ChassisSubsystem extends SubsystemBase {
  private WPI_TalonSRX left1;
  private WPI_TalonSRX left2;
  private WPI_TalonSRX right1;
  private WPI_TalonSRX right2;
  private DifferentialDrive drive;
  /**
   * Creates a new ExampleSubsystem.
   */
  public ChassisSubsystem() {
    left1 = new WPI_TalonSRX(3);
    left2 = new WPI_TalonSRX(10);
    right1 = new WPI_TalonSRX(13);
    right2 = new WPI_TalonSRX(11);
    drive = new DifferentialDrive(new SpeedControllerGroup(left1, left2), new SpeedControllerGroup(right1, right2));;
  }
  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
  public void arcadeDrive (double forward, double rotation){
    drive.arcadeDrive(forward, rotation);
  }
}
