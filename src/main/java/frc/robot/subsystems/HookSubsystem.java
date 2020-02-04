package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.PIDSubsystem;
import frc.robot.Constants.TalonConstants;

public class HookSubsystem extends PIDSubsystem {
  private WPI_TalonSRX hookController;
  static final double kP = 1.0;
  static final double kI = 0.0;
  static final double kD = 0.0;

  public HookSubsystem() {
    super(new PIDController(kP, kI, kD));
    hookController = new WPI_TalonSRX(12);
    hookController.setSafetyEnabled(false);
    addChild("pid", getController());
    addChild("controller", hookController);

    hookController.configFactoryDefault(TalonConstants.DEFAULT_TIMEOUT);
    hookController.setInverted(false);
    hookController.setSensorPhase(true);

    hookController.configNeutralDeadband(0.04);
    hookController.configNominalOutputForward(0.0);
    hookController.configNominalOutputReverse(0.0);

    hookController.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,
                                                TalonConstants.PRIMARY_PID,
                                                TalonConstants.DEFAULT_TIMEOUT);
    hookController.setSelectedSensorPosition(0, TalonConstants.PRIMARY_PID,
                                             TalonConstants.DEFAULT_TIMEOUT);

  }

  public double getEncoderPosition() {
    return hookController.getSelectedSensorPosition();
  }

  public double getEncoderVelocity() {
    return hookController.getSelectedSensorVelocity();
  }

  @Override
  public void periodic() {
    super.periodic();
    SmartDashboard.putNumber("hook_position", getEncoderPosition());
    SmartDashboard.putNumber("hook_velocity", getEncoderVelocity());
  }

  @Override
  protected void useOutput(double output, double setpoint) {
    hookController.set(output);
  }

  @Override
  public double getMeasurement() {
    return hookController.getSelectedSensorPosition();
  }

}