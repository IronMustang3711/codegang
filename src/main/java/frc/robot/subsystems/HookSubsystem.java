package frc.robot.subsystems;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj2.command.PIDSubsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class HookSubsystem extends PIDSubsystem {
    private WPI_TalonSRX hookController;
    static final double kP = 1.0;
    static final double kI = 0.0;
    static final double kD = 0.0;

    public HookSubsystem() {
        super(new PIDController(kP, kI, kD));
        hookController = new WPI_TalonSRX(7);
    }

    @Override
    protected void useOutput(double output, double setpoint) {
        hookController.setVoltage(output);

    }

    @Override
    public double getMeasurement() {
        return hookController.getSelectedSensorPosition();

    }

}