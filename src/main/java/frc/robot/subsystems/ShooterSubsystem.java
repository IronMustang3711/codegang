package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.commands.ResetSensors;
import frc.robot.commands.ShooterVelocityControl;
import frc.robot.stuff.SensorReset;
import frc.robot.stuff.TalonFaultsReporter;

import java.util.List;

public class ShooterSubsystem extends SubsystemBase implements SensorReset {
  public final WPI_TalonSRX lowerController = new WPI_TalonSRX(5); // upper
  public WPI_TalonSRX upperController = new WPI_TalonSRX(27); //lower
  double out = 0.0;

  NetworkTableEntry[] positions;
  NetworkTableEntry[] vels;
  NetworkTableEntry[] outs;
  NetworkTableEntry enableTelem;
  public NetworkTableEntry lowerSetpoint;
  public NetworkTableEntry upperSetpoint;


  double getOutput() {
    return out;
  }

  public ShooterSubsystem() {
    for (var talon : List.of(lowerController, upperController)) {
      talon.setSafetyEnabled(false);
      talon.setExpiration(0.5);
      talon.configFactoryDefault();

      talon.configNeutralDeadband(0.04);
      talon.configNominalOutputForward(0.0); //0.15
      talon.configNominalOutputReverse(0.0); //-0.15

      talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,
                                         Constants.TalonConstants.PRIMARY_PID,
                                         Constants.TalonConstants.DEFAULT_TIMEOUT);
      talon.setSelectedSensorPosition(0, Constants.TalonConstants.PRIMARY_PID,
                                      Constants.TalonConstants.DEFAULT_TIMEOUT);

      TalonFaultsReporter.instrument(talon);
    }

    upperController.follow(lowerController);
    upperController.setInverted(InvertType.FollowMaster);
    lowerController.setInverted(true);
    upperController.setInverted(true);
    lowerController.setSensorPhase(true);
    upperController.setSensorPhase(true);

    addChild("Shooter1(lower)", lowerController);
    addChild("Shooter2(upper)", upperController);

    setupShuffleboard();
  }

  private void setupShuffleboard() {
    outs = new NetworkTableEntry[2];
    vels = new NetworkTableEntry[2];
    positions = new NetworkTableEntry[2];

    var tab = Shuffleboard.getTab(ShooterSubsystem.class.getSimpleName());
    lowerSetpoint = tab.add("lowerSetpoint",20000).getEntry();
    upperSetpoint = tab.add("upperSetpoint",16000).getEntry();
    enableTelem = tab.add("enable telem", true)
                     .withWidget(BuiltInWidgets.kToggleButton)
                     .getEntry();

    tab.addNumber("motor_out", this::getOutput);
    tab.add(new ResetSensors<>(this));
    tab.add(new ShooterVelocityControl(this));

    var velsContainer = tab.getLayout("velocity", BuiltInLayouts.kList);
    vels[0] = velsContainer.add("lower(1)", 0.0).getEntry();
    vels[1] = velsContainer.add("upper(2)", 0.0).getEntry();

    var controllersContainer = tab.getLayout("controllers", BuiltInLayouts.kList);
    controllersContainer.add(lowerController);
    controllersContainer.add(upperController);

    var outsContainer = tab.getLayout("outputs", BuiltInLayouts.kList);
    outs[0] = outsContainer.add("lower", 0.0).getEntry();
    outs[1] = outsContainer.add("upper", 0.0).getEntry();

    var posContainer = tab.getLayout("positions", BuiltInLayouts.kList);
    positions[0] = posContainer.add("lower", 0.0).getEntry();
    positions[1] = posContainer.add("upper", 0.0).getEntry();

  }

  public double getLowerEncoderPosition() {
    return lowerController.getSelectedSensorPosition();
  }

  public double getLowerEncoderVelocity() {
    return lowerController.getSelectedSensorVelocity();
  }

  public double getUpperEncoderPosition() {
    return upperController.getSelectedSensorPosition();
  }

  public double getUpperEncoderVelocity() {
    return upperController.getSelectedSensorVelocity();
  }

  @Override
  public void periodic() {
    if (enableTelem != null && enableTelem.getBoolean(true)) {
      vels[0].setDouble(getUpperEncoderVelocity());
      vels[1].setDouble(getLowerEncoderVelocity());
      outs[0].setDouble(lowerController.getMotorOutputPercent());
      outs[1].setDouble(upperController.getMotorOutputPercent());
      positions[0].setDouble(getUpperEncoderPosition());
      positions[1].setDouble(getLowerEncoderPosition());
    }
  }

  public void runShooter(double amt) {
    lowerController.set(ControlMode.Velocity, 20000);
    upperController.set(ControlMode.Velocity, 16000);
    out = amt;
  }

  public boolean isEnabled() {
    return lowerController.get() != 0.0;
  }

  public boolean isSpunUp() {
    return upperController.getSelectedSensorVelocity() >= 17500;
  }

  @Override
  public void resetSensors() {
    for (var c : List.of(lowerController, upperController)) {
      ErrorCode errorCode = c.getSensorCollection().setQuadraturePosition(0, Constants.TalonConstants.DEFAULT_TIMEOUT);
      if (errorCode != ErrorCode.OK) {
        DriverStation.reportError("Problem reseting " + c.getName() + " in subsystem " + getSubsystem(), false);
      }
    }
  }
}