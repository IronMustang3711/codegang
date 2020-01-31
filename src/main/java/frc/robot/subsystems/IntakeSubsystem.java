package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.TalonFaultsReporter;

public class IntakeSubsystem extends SubsystemBase {
    private WPI_TalonSRX controller = new WPI_TalonSRX(15);

    public IntakeSubsystem(){
        addChild("intakeController",controller);
        controller.configFactoryDefault();
        TalonFaultsReporter.instrument(controller);
    }

    public void enableIntake(boolean enable) {
        // if (enable)
        //     controller.setVoltage(10.0);
        // else
        //     controller.setVoltage(0.0);
    }

    public boolean isEnabled() {
        return controller.getMotorOutputPercent() > 0.0;
    }
}