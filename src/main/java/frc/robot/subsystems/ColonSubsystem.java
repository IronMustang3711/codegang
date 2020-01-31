package frc.robot.subsystems;

import java.util.List;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.stuff.TalonFaultsReporter;

public class ColonSubsystem extends SubsystemBase {

    WPI_TalonSRX controller1 = new WPI_TalonSRX(1);
    WPI_TalonSRX controller2 = new WPI_TalonSRX(4);

    public ColonSubsystem(){
        addChild("colon1",controller1);
        addChild("colon2",controller2);

       for(var talon : List.of(controller1,controller2)){
        talon.setSafetyEnabled(true);
        talon.setExpiration(0.5);
        talon.configFactoryDefault();
        TalonFaultsReporter.instrument(talon);
    }

    controller1.setInverted(true);
    controller2.follow(controller1);

    controller2.setInverted(true);
   // controller2.setInverted(InvertType.FollowMaster);
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
