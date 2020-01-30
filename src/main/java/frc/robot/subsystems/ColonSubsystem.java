package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ColonSubsystem extends SubsystemBase {

    WPI_TalonSRX controller1 = new WPI_TalonSRX(0);
    WPI_TalonSRX controller2 = new WPI_TalonSRX(0);

    public ColonSubsystem(){
        addChild("first",controller1);
        addChild("second",controller2);
    }
}
