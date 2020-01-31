package frc.robot.subsystems;

import java.util.List;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.TalonFaultsReporter;

public class ShooterSubsystem extends SubsystemBase{
    private WPI_TalonSRX shooter1Controller = new WPI_TalonSRX(05); 
    private WPI_TalonSRX shooter2Controller = new WPI_TalonSRX(27); 

  //  PigeonIMU pigeonIMU = new PigeonIMU(shooter2Controller);
    
    
    public ShooterSubsystem(){
        for(var talon : List.of(shooter1Controller,shooter2Controller)){
            talon.setSafetyEnabled(true);
            talon.setExpiration(0.5);
            talon.configFactoryDefault();
            TalonFaultsReporter.instrument(talon);
        }
        shooter2Controller.follow(shooter1Controller);
        shooter2Controller.setInverted(InvertType.FollowMaster);
        shooter1Controller.setInverted(true);
        shooter2Controller.setInverted(true);

        addChild("shooter1",shooter1Controller);
        addChild("shooter2",shooter2Controller);
        //addChild("pigeon",pigeonIMU);
    }

    @Override
    public void periodic() {
      // SmartDashboard.putNumber("pigeon fused heading", pigeonIMU.getFusedHeading());
    }

    public void enableShooter(boolean enable)
    {
        if(enable)
        {
            shooter1Controller.setVoltage(10.0);
            shooter2Controller.setVoltage(10.0);
        }
        else
        {
            shooter1Controller.setVoltage(0.0);
            shooter2Controller.setVoltage(0.0);
        }

    }

    public boolean isEnabled()
    {
        return shooter1Controller.getMotorOutputVoltage() > 0.0;
    }
}