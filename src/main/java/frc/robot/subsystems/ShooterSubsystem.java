package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ShooterSubsystem extends SubsystemBase{
    private WPI_TalonSRX shooter1Controller = new WPI_TalonSRX(05); 
    private WPI_TalonSRX shooter2Controller = new WPI_TalonSRX(27); 

    PigeonIMU pigeonIMU = new PigeonIMU(shooter2Controller);
    
    
    public ShooterSubsystem(){
        addChild("shooter1",shooter1Controller);
        addChild("shooter2",shooter2Controller);
        //addChild("pigeon",pigeonIMU);
    }

    @Override
    public void periodic() {
       SmartDashboard.putNumber("pigeon fused heading", pigeonIMU.getFusedHeading());
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