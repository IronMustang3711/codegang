package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ColonSubsystem extends SubsystemBase {

    WPI_TalonSRX controller1 = new WPI_TalonSRX(12);
    WPI_TalonSRX controller2 = new WPI_TalonSRX(4);

    public ColonSubsystem(){
        addChild("first",controller1);
        addChild("second",controller2);
    }

    public void enableColon(boolean enable)
    {
        if(enable)
        {
            controller1.setVoltage(10.0);
            controller2.setVoltage(10.0);
        }
        else    
        {
            controller1.setVoltage(0.0);
            controller2.setVoltage(0.0);
        }
    }

    public boolean isEnabled()
    {
        return controller1.getMotorOutputVoltage() > 0.0 && controller2.getMotorOutputVoltage() > 0.0;
    }
}
