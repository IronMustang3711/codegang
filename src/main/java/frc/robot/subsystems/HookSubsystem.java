package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.PIDSubsystem;
import frc.robot.Constants.TalonConstants;
import frc.robot.commands.ResetSensors;
import frc.robot.stuff.SensorReset;

public class HookSubsystem extends PIDSubsystem implements SensorReset {
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
    hookController.selectProfileSlot(TalonConstants.PRIMARY_SLOT, TalonConstants.PRIMARY_PID);
    hookController.setSelectedSensorPosition(0, TalonConstants.PRIMARY_PID,
                                             TalonConstants.DEFAULT_TIMEOUT);

    hookController.configVoltageCompSaturation(10.0, TalonConstants.DEFAULT_TIMEOUT);
    hookController.configPeakOutputForward(0.5, TalonConstants.DEFAULT_TIMEOUT);
    hookController.configPeakOutputReverse(0.3, TalonConstants.DEFAULT_TIMEOUT);

    setupShuffleboard();

  }

  private void setupShuffleboard() {
    var tab = Shuffleboard.getTab(HookSubsystem.class.getSimpleName());
    tab.add(getController());
    tab.add(hookController);
    tab.addNumber("hookPosition", this::getEncoderPosition);
    tab.addNumber("hookVelocity", this::getEncoderVelocity);
    tab.addNumber("hookCurrent", hookController::getStatorCurrent);
    tab.add(new ResetSensors<>(this));

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
  }

  @Override
  protected void useOutput(double output, double setpoint) {
    hookController.set(output);
  }

  public void setOutput(double amount) {
    hookController.set(amount);
  }

  @Override
  public double getMeasurement() {
    return hookController.getSelectedSensorPosition();
  }

  @Override
  public void resetSensors() {
    ErrorCode errorCode = hookController.getSensorCollection().setQuadraturePosition(0, TalonConstants.DEFAULT_TIMEOUT);
    if (errorCode != ErrorCode.OK) {
      DriverStation.reportError("Problem reseting " + hookController.getName() + " in subsystem " + getSubsystem(),
                                false);
    }
  }
}
