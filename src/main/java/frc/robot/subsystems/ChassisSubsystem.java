/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ChassisSubsystem extends SubsystemBase {
  private WPI_TalonSRX left1;
  private WPI_TalonSRX left2;
  private WPI_TalonSRX right1;
  private WPI_TalonSRX right2;
  private DifferentialDrive drive;

  AHRS ahrs;
  ADXRS450_Gyro gyro; //TODO: remove?

  public ChassisSubsystem() {
    //setName("Chassis");
    left1 = new WPI_TalonSRX(3);
    left2 = new WPI_TalonSRX(10);

    right1 = new WPI_TalonSRX(13);
    right2 = new WPI_TalonSRX(11);
    drive = new DifferentialDrive(new SpeedControllerGroup(left1, left2), new SpeedControllerGroup(right1, right2));;

    ahrs = new AHRS(SPI.Port.kMXP);
    gyro = new ADXRS450_Gyro();

    addChild("left1",left1);
    addChild("left2",left2);
    addChild("right1",right1);
    addChild("right2",right2);
    addChild("diff drive",drive); //TODO: remove the above lines if controllers get added twice
    addChild("navx/ahrs",ahrs);
    addChild("gyro",gyro);


  }
  @Override
  public void periodic() {
  }


  public void arcadeDrive (double forward, double rotation){
    drive.arcadeDrive(forward, rotation);
  }
}
