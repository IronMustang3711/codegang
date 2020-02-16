package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.PIDSubsystem;
import frc.robot.Constants.TalonConstants;
import frc.robot.commands.HookPosition;
import frc.robot.commands.HookPositionWithMagic;
import frc.robot.commands.ResetSensors;
import frc.robot.stuff.SensorReset;

public class HookSubsystem extends PIDSubsystem implements SensorReset {
  static final boolean ENABLE_SHUFFLEBOARD = true;

  //todo: change in config
  static final double kP = 1.0;
  static final double kI = 0.0;
  static final double kD = 0.0;
  static final int POSITION_SLOT = 0;
  static final int MM_SLOT = 1;
  TalonControl ctrl = TalonControl.PCT_OUT;
  private WPI_TalonSRX hookController = new WPI_TalonSRX(12); ;

  public HookSubsystem() {
    super(new PIDController(kP, kI, kD));
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

    //config.peakOutputForward = 0.5;
    //config.peakOutputReverse = 0.3;

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

  void control(TalonControl c, double demand) {
    if (c != ctrl) {
      ctrl = c;
      c.init(this);
    } else {
      c.apply(this, demand);
    }
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
    shuffleboardUpdate.run();
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

  Runnable shuffleboardUpdate = ENABLE_SHUFFLEBOARD ? new Runnable() {

    NetworkTableEntry ntPos;
    NetworkTableEntry ntVel;
    NetworkTableEntry ntCurr;
    NetworkTableEntry ntOut;

    NetworkTableEntry ntErr;
    NetworkTableEntry ntSp;

    NetworkTableEntry setpoint;
    HookPosition hookPosition;
    HookPositionWithMagic hookPositionWithMagic;

    {
      var tab = Shuffleboard.getTab(HookSubsystem.class.getSimpleName());
      // tab.add(getController());
      // tab.add(hookController);

      ShuffleboardLayout stuff = tab.getLayout("stuff", BuiltInLayouts.kList);
      ntPos = stuff.add("position", 0.0).getEntry();
      ntVel = stuff.add("velocity", 0.0).getEntry();
      ntOut = stuff.add("output%", 0.0).getEntry();
      ntCurr = stuff.add("current", 0.0).getEntry();

      var closedLoopStuff = tab.getLayout("closed loop", BuiltInLayouts.kList);
      ntErr = closedLoopStuff.add("error", 0.0).getEntry();
      ntSp = closedLoopStuff.add("setpoint", 0.0).getEntry();

      setpoint = tab.add("setpoint", 0.0).getEntry();

      tab.add(new ResetSensors<>(HookSubsystem.this));

      ShuffleboardLayout posctrl = tab.getLayout("position control", BuiltInLayouts.kList);
      posctrl.add(hookPosition = new HookPosition(HookSubsystem.this, setpoint.getDouble(getEncoderPosition())));
      posctrl.add(hookPositionWithMagic = new HookPositionWithMagic(HookSubsystem.this,
                                                                    setpoint.getDouble(getEncoderPosition())));

    }

    public void run() {
      ntPos.setDouble(getEncoderPosition());
      ntVel.setDouble(getEncoderVelocity());
      ntOut.setDouble(hookController.getMotorOutputPercent());
      ntCurr.setDouble(hookController.getStatorCurrent());

      if (TalonControl.POSITION.equals(ctrl) || TalonControl.MM_POSITION.equals(ctrl)) {
        ntErr.setDouble(hookController.getClosedLoopError());
        ntSp.setDouble(hookController.getClosedLoopTarget());
      }
      var sp = setpoint.getDouble(getEncoderPosition());
      hookPosition.setpoint = sp;
      hookPositionWithMagic.setpoint = sp;
    }
  }
                                  : () -> {};
}
