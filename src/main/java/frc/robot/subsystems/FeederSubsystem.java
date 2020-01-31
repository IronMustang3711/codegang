package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class FeederSubsystem extends SubsystemBase
{
    private WPI_TalonSRX feeder1Controller = new WPI_TalonSRX(12);
    private WPI_TalonSRX feeder2Controller = new WPI_TalonSRX(04);

    public void enableFeeder(boolean enable)
    {
        if(enable)
        {
            feeder1Controller.setVoltage(10.0);
            feeder2Controller.setVoltage(10.0);
        }
        else    
        {
            feeder1Controller.setVoltage(0.0);
            feeder2Controller.setVoltage(0.0);
        }
    }

    public boolean isEnabled()
    {
        return feeder1Controller.getMotorOutputVoltage() > 0.0 && feeder2Controller.getMotorOutputVoltage() > 0.0;
    }


}