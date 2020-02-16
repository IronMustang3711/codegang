package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
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
  public final WPI_TalonSRX controller1 = new WPI_TalonSRX(5); // upper
  public WPI_TalonSRX controller2 = new WPI_TalonSRX(27); //lower
  double out = 0.0;

  NetworkTableEntry[] positions;
  NetworkTableEntry[] vels;
  NetworkTableEntry[] outs;
  NetworkTableEntry enableTelem;
  public NetworkTableEntry setpoint;


  double getOutput() {
    return out;
  }

  public ShooterSubsystem() {
    for (var talon : List.of(controller1, controller2)) {
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

    controller2.follow(controller1);
    controller2.setInverted(InvertType.FollowMaster);
    controller1.setInverted(true);
    controller2.setInverted(true);
    controller1.setSensorPhase(true);
    controller2.setSensorPhase(true);

    addChild("Shooter1(upper)", controller1);
    addChild("Shooter2(lower)", controller2);

    setupShuffleboard();
  }

  private void setupShuffleboard() {
    outs = new NetworkTableEntry[2];
    vels = new NetworkTableEntry[2];
    positions = new NetworkTableEntry[2];

    var tab = Shuffleboard.getTab(ShooterSubsystem.class.getSimpleName());
    setpoint = tab.add("setpoint",18000).getEntry();
    enableTelem = tab.add("enable telem", true)
                     .withWidget(BuiltInWidgets.kToggleButton)
                     .getEntry();

    tab.addNumber("motor_out", this::getOutput);
    tab.add(new ResetSensors<>(this));
    tab.add(new ShooterVelocityControl(this));

    var velsContainer = tab.getLayout("velocity", BuiltInLayouts.kList);
    vels[0] = velsContainer.add("upper(1)", 0.0).getEntry();
    vels[1] = velsContainer.add("lower(2)", 0.0).getEntry();

    var controllersContainer = tab.getLayout("controllers", BuiltInLayouts.kList);
    controllersContainer.add(controller1);
    controllersContainer.add(controller2);

    var outsContainer = tab.getLayout("outputs", BuiltInLayouts.kList);
    outs[0] = outsContainer.add("upper", 0.0).getEntry();
    outs[1] = outsContainer.add("lower", 0.0).getEntry();

    var posContainer = tab.getLayout("positions", BuiltInLayouts.kList);
    positions[0] = posContainer.add("upper", 0.0).getEntry();
    positions[1] = posContainer.add("lower", 0.0).getEntry();

  }

  public double getLowerEncoderPosition() {
    return controller1.getSelectedSensorPosition();
  }

  public double getLowerEncoderVelocity() {
    return controller1.getSelectedSensorVelocity();
  }

  public double getUpperEncoderPosition() {
    return controller2.getSelectedSensorPosition();
  }

  public double getUpperEncoderVelocity() {
    return controller2.getSelectedSensorVelocity();
  }

  @Override
  public void periodic() {
    if (enableTelem != null && enableTelem.getBoolean(true)) {
      vels[0].setDouble(getUpperEncoderVelocity());
      vels[1].setDouble(getLowerEncoderVelocity());
      outs[0].setDouble(controller1.getMotorOutputPercent());
      outs[1].setDouble(controller2.getMotorOutputPercent());
      positions[0].setDouble(getUpperEncoderPosition());
      positions[1].setDouble(getLowerEncoderPosition());
    }
  }

  public void runShooter(double amt) {
    controller1.set(amt);
    controller2.set(amt);
    out = amt;
  }

  public boolean isEnabled() {
    return controller1.get() != 0.0;
  }

  @Override
  public void resetSensors() {
    for (var c : List.of(controller1, controller2)) {
      ErrorCode errorCode = c.getSensorCollection().setQuadraturePosition(0, Constants.TalonConstants.DEFAULT_TIMEOUT);
      if (errorCode != ErrorCode.OK) {
        DriverStation.reportError("Problem reseting " + c.getName() + " in subsystem " + getSubsystem(), false);
      }
    }
  }
}