/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import java.util.List;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.TalonFaultsReporter;

public class ChassisSubsystem extends SubsystemBase {
  private WPI_TalonSRX leftFront;
  private WPI_TalonSRX leftRear;
  private WPI_TalonSRX rightFront;
  private WPI_TalonSRX rightRear;
  private DifferentialDrive drive;

  AHRS ahrs;
  ADXRS450_Gyro gyro; //TODO: remove?

  public ChassisSubsystem() {
    //setName("Chassis");
    leftFront = new WPI_TalonSRX(3);
    leftRear = new WPI_TalonSRX(10);

    rightFront = new WPI_TalonSRX(13);
    rightRear = new WPI_TalonSRX(11);
    drive = new DifferentialDrive(leftFront, rightFront);
    //drive = new DifferentialDrive(new SpeedControllerGroup(leftFront, leftRear), new SpeedControllerGroup(rightFront, rightRear));

    ahrs = new AHRS(SPI.Port.kMXP);
    gyro = new ADXRS450_Gyro();

    // addChild("left1",leftFront);
     addChild("left2",leftRear);
    // addChild("right1",rightFront);
     addChild("right2",rightRear);
    addChild("drive",drive); //TODO: remove the above lines if controllers get added twice
    addChild("navx/ahrs",ahrs);
    addChild("gyro",gyro);

    drive.setExpiration(0.5);
    drive.setMaxOutput(1.0);

    drive.setRightSideInverted(false);

    for(var talon : List.of(leftFront,leftRear,rightFront,rightRear)){

      talon.setSafetyEnabled(true);
      talon.setExpiration(0.5);
      talon.configFactoryDefault();

      talon.configOpenloopRamp(0.0);
      talon.setNeutralMode(NeutralMode.Brake);
      talon.configNeutralDeadband(0.04);
      talon.configNominalOutputForward(0.0); //0.15
      talon.configNominalOutputReverse(0.0); //-0.15
      
      TalonFaultsReporter.instrument(talon);
    }
    
    rightRear.follow(rightFront);
    leftRear.follow(leftFront);

    rightFront.setInverted(true);
    leftFront.setInverted(false);

    rightRear.setInverted(InvertType.FollowMaster);
    leftRear.setInverted(InvertType.FollowMaster);

    rightFront.setSensorPhase(true);
    leftFront.setSensorPhase(true);

  }
  @Override
  public void periodic() {
  }


  public void arcadeDrive (double forward, double rotation){
    drive.arcadeDrive(forward, rotation);
  }
}
