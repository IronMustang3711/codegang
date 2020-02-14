package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
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

  //todo: change in config
  static final double kP = 1.0;
  static final double kI = 0.0;
  static final double kD = 0.0;

  static final int POSITION_SLOT = 0;
  static final int MM_SLOT = 1;

  enum TalonControl {

    POSITION {
      @Override
      void init(HookSubsystem hook) {
        hook.hookController.selectProfileSlot(POSITION_SLOT, TalonConstants.PRIMARY_PID);
      }

      @Override
      void apply(HookSubsystem hook, double command) {
        hook.hookController.set(ControlMode.Position, command);
      }
    },
    MM_POSITION {
      @Override
      void init(HookSubsystem hook) {
        hook.hookController.selectProfileSlot(MM_SLOT, TalonConstants.PRIMARY_PID);
      }

      @Override
      void apply(HookSubsystem hook, double command) {
        hook.hookController.set(ControlMode.MotionMagic, command);
      }
    },
    PCT_OUT {
      @Override
      void init(HookSubsystem hook) {
      }

      @Override
      void apply(HookSubsystem hook, double command) {
        hook.hookController.set(ControlMode.PercentOutput, command);
      }
    },
    DISABLE {
      @Override
      void init(HookSubsystem hook) {

      }

      @Override
      void apply(HookSubsystem hook, double command) {
        hook.hookController.set(ControlMode.Disabled, 0.0);
      }
    };

    abstract void init(HookSubsystem hook);

    abstract void apply(HookSubsystem hook, double command);
  }

  TalonControl ctrl = TalonControl.PCT_OUT;

  void control(TalonControl c, double demand) {
    if (c != ctrl) {
      ctrl = c;
      c.init(this);
    } else {
      c.apply(this, demand);
    }
  }


  public HookSubsystem() {
    super(new PIDController(kP, kI, kD));
    hookController = new WPI_TalonSRX(12);
    hookController.setSafetyEnabled(false);
    addChild("pid", getController());
    addChild("controller", hookController);

    hookController.configFactoryDefault(TalonConstants.DEFAULT_TIMEOUT);
    hookController.setInverted(false);
    hookController.setSensorPhase(true);


    hookController.setSelectedSensorPosition(0, TalonConstants.PRIMARY_PID,
                                             TalonConstants.DEFAULT_TIMEOUT);


    var config = applyConfig(new TalonSRXConfiguration());
    hookController.configAllSettings(config, TalonConstants.DEFAULT_TIMEOUT);
    hookController.setNeutralMode(NeutralMode.Brake);

    hookController.selectProfileSlot(POSITION_SLOT, TalonConstants.PRIMARY_PID);
    hookController.enableVoltageCompensation(true);
    hookController.enableCurrentLimit(true);

    resetSensors();

    setupShuffleboard();

  }

  static TalonSRXConfiguration applyConfig(TalonSRXConfiguration config) {

    config.primaryPID.selectedFeedbackSensor = FeedbackDevice.QuadEncoder;

    config.remoteFilter0.remoteSensorSource = RemoteSensorSource.Off;
    config.remoteFilter1.remoteSensorSource = RemoteSensorSource.Off;
    config.forwardLimitSwitchSource = LimitSwitchSource.Deactivated;
    config.reverseLimitSwitchSource = LimitSwitchSource.Deactivated;

    config.forwardLimitSwitchNormal = LimitSwitchNormal.Disabled;
    config.reverseLimitSwitchNormal = LimitSwitchNormal.Disabled;

    config.forwardSoftLimitThreshold = 1600;
    config.reverseSoftLimitThreshold = -100;
    config.forwardSoftLimitEnable = true;
    config.reverseSoftLimitEnable = true;


    config.openloopRamp = 0.5;

    //TODO: configure these
    config.motionAcceleration = 100;
    config.motionCruiseVelocity = 100;

    config.motionCurveStrength = 8;

            /*
        https://phoenix-documentation.readthedocs.io/en/latest/ch13_MC.html#ramping
       The nominal outputs can be selected to ensure that any non-zero requested motor output gets promoted
       to a minimum output. For example, if the nominal forward is set to +0.10 (+10%), then any motor request
       within (0%, +10%) will be promoted to +10% assuming request is beyond the neutral dead band.
       This is useful for mechanisms that require a minimum output for movement,
       and can be used as a simpler alternative to the kI (integral) component of closed-looping in some circumstances.
         */
    config.nominalOutputForward = 0;
    config.nominalOutputReverse = 0;

    config.peakOutputForward = 0.5;
    config.peakOutputReverse = 0.3;

    config.voltageCompSaturation = 10.0;

    //must be enabled via enableCurrentLimit()
    config.peakCurrentLimit = 10; //amps
    config.peakCurrentDuration = 1000; //ms
    config.continuousCurrentLimit = 1;

    //position control slot
    config.slot0.kP = 0.5;
    config.slot0.allowableClosedloopError = 10;

    //motion magic position control slot
    config.slot1.kP = 0.5;
    config.slot1.kF = 0.5;
    config.slot1.allowableClosedloopError = 10;

    return config;
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
    control(TalonControl.PCT_OUT, amount);
    // hookController.set(amount);
  }

  public void setPosition(double setpoint) {
    control(TalonControl.POSITION, setpoint);
  }

  public void setPositionWithMagic(double setpoint) {
    control(TalonControl.MM_POSITION, setpoint);
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
