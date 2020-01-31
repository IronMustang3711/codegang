package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ShooterSubsystem extends SubsystemBase{
    private WPI_TalonSRX shooter1Controller = new WPI_TalonSRX(05); 
    private WPI_TalonSRX shooter2Controller = new WPI_TalonSRX(27); 

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