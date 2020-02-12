package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class WinchSubsystem extends SubsystemBase {
    WPI_TalonSRX winch = new WPI_TalonSRX(02);

    public WinchSubsystem() {
        addChild("controller", winch);
    }

    public void winchForward() {

        winch.set(1.0);
    }

    public void winchReverse() {
        winch.set(-0.4);
    }

    public void winchDisable() {
        winch.setVoltage(0.0);

    }

    public boolean isEnabled() {
        return winch.getMotorOutputVoltage() > 0.0;
    }

}