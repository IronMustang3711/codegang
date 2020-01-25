package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IntakeSubsystem extends SubsystemBase {
    private WPI_TalonSRX controller = new WPI_TalonSRX(8);

    public void enableIntake(boolean enable) {
        if (enable)
            controller.setVoltage(10.0);
        else
            controller.setVoltage(0.0);
    }

    public boolean isEnabled() {
        return controller.getMotorOutputPercent() > 0.0;
    }
}