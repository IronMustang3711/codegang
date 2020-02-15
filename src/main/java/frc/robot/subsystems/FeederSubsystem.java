package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.ResetSensors;
import frc.robot.stuff.SensorReset;
import frc.robot.stuff.TalonFaultsReporter;

import java.util.List;

import static frc.robot.Constants.TalonConstants;

public class FeederSubsystem extends SubsystemBase implements SensorReset {
  static final boolean ENABLE_SHUFFLEBOARD = true;


  public WPI_TalonSRX getController1() {
    return controller1;
  }

  public WPI_TalonSRX getController2() {
    return controller2;
  }

  static class JamDetector {
    private double statorCurrent;
    private double outputPercent;
    private int velocity;
    Runnable onJam;

    JamDetector(TalonSRX talon) {
      this.talon = talon;
    }

    enum JamStatus {
      NOT_JAMMED {
        @Override
        void handle(JamDetector detector) {
          if (detector.isJam()) {
            detector.maybejam_start = System.currentTimeMillis();
            detector.transition(MAYBE_JAMMED);
          }
        }
      },
      MAYBE_JAMMED {
        @Override
        void handle(JamDetector detector) {
          if (!detector.isJam()) {
            detector.transition(NOT_JAMMED);
          } else if ((System.currentTimeMillis() - detector.maybejam_start) - 250 > 0) {
            detector.transition(JAMMED);
          }
        }
      },
      JAMMED {
        @Override
        void handle(JamDetector detector) {
          if (!detector.isJam()) detector.transition(NOT_JAMMED);
          else if (detector.onJam != null) detector.onJam.run();
        }
      };

      abstract void handle(JamDetector detector);
    }

    void transition(JamStatus status) {
      this.status = status;
    }

    JamStatus status = JamStatus.NOT_JAMMED;
    final TalonSRX talon;

    long maybejam_start = -1;

    boolean isJam() {
      return statorCurrent > 3.0 && (outputPercent * 120) * 0.7 < velocity;
    }

    void run() {
      statorCurrent = talon.getStatorCurrent();
      outputPercent = talon.getMotorOutputPercent();
      velocity = talon.getSelectedSensorVelocity();
      status.handle(this);
    }


  }

  private final WPI_TalonSRX controller1 = new WPI_TalonSRX(1);
  private final WPI_TalonSRX controller2 = new WPI_TalonSRX(4);

  private final JamDetector jam1 = new JamDetector(controller1);
  private final JamDetector jam2 = new JamDetector(controller2);

  public FeederSubsystem() {
    addChild("feeder1", controller1);
    addChild("feeder2", controller2);

    for (var talon : List.of(controller1, controller2)) {
      talon.setSafetyEnabled(false);
      talon.setExpiration(0.5);

      TalonFaultsReporter.instrument(talon);

      talon.configFactoryDefault();

      talon.configNeutralDeadband(0.04);
      talon.configNominalOutputForward(0.0); //0.15
      talon.configNominalOutputReverse(0.0); //-0.15

      talon.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder,
                                         TalonConstants.PRIMARY_PID,
                                         TalonConstants.DEFAULT_TIMEOUT);
      talon.setSelectedSensorPosition(0, TalonConstants.PRIMARY_PID,
                                      TalonConstants.DEFAULT_TIMEOUT);
    }
    controller1.setSensorPhase(false);
    controller2.setSensorPhase(false);

    controller1.setInverted(true);
    //controller2.follow(controller1);

    controller2.setInverted(true);
    // controller2.setInverted(InvertType.FollowMaster);


    jam1.onJam = () -> {
      DriverStation.reportWarning("jam 1", false);
      var currentCommand = getCurrentCommand();
      if (currentCommand != null) currentCommand.cancel();
    };
    jam2.onJam = () -> {
      DriverStation.reportWarning("jam 2", false);
      var currentCommand = getCurrentCommand();
      if (currentCommand != null) currentCommand.cancel();
    };

  }

  Runnable shuffleboardUpdate = ENABLE_SHUFFLEBOARD ? new Runnable() {
    class FeederStuff {
      FeederStuff(ShuffleboardLayout container) {
        position = container.add("position", 0.0).getEntry();
        velocity = container.add("velocity", 0.0).getEntry();
        current = container.add("current", 0.0).getEntry();
      }

      NetworkTableEntry position;
      NetworkTableEntry velocity;
      NetworkTableEntry current;
    }

    ShuffleboardTab tab = Shuffleboard.getTab(FeederSubsystem.class.getSimpleName());
    FeederStuff feeder1Stuff = new FeederStuff(tab.getLayout("feeder1", BuiltInLayouts.kList));
    FeederStuff feeder2Stuff = new FeederStuff(tab.getLayout("feeder2", BuiltInLayouts.kList));

    {
      tab.add(new ResetSensors<>(FeederSubsystem.this));
    }

    @Override
    public void run() {
      feeder1Stuff.position.setDouble(getFirstEncoderPosition());
      feeder1Stuff.velocity.setDouble(getFirstEncoderVelocity());
      feeder1Stuff.current.setDouble(controller1.getStatorCurrent());

      feeder2Stuff.position.setDouble(getSecondEncoderPosition());
      feeder2Stuff.velocity.setDouble(getSecondEncoderVelocity());
      feeder2Stuff.current.setDouble(controller2.getStatorCurrent());

    }
  } : () -> {};


  public double getFirstEncoderPosition() {
    return getController1().getSelectedSensorPosition();
  }

  public double getSecondEncoderPosition() {
    return getController2().getSelectedSensorPosition();
  }

  /**
   * @return sensor velocity (in raw sensor units) per 100ms.
   */
  public double getFirstEncoderVelocity() {
    return getController1().getSelectedSensorVelocity(TalonConstants.PRIMARY_PID);
  }

  public double getSecondEncoderVelocity() {
    return getController2().getSelectedSensorVelocity(TalonConstants.PRIMARY_PID);
  }

  @Override
  public void periodic() {
    jam1.run();
    jam2.run();
    shuffleboardUpdate.run();
  }

  public void setMotorOutputs(double first) {
    setMotorOutputs(first, first);
  }

  public void setMotorOutputs(double first, double second) {
    getController1().set(first);
    getController2().set(second);
  }

  public boolean isEnabled() {
    return getController1().getMotorOutputVoltage() > 0.0 && getController2().getMotorOutputVoltage() > 0.0;
  }

  @Override
  public void resetSensors() {
    for (var c : List.of(getController1(), getController2())) {
      ErrorCode errorCode = c.getSensorCollection().setQuadraturePosition(0, TalonConstants.DEFAULT_TIMEOUT);
      if (errorCode != ErrorCode.OK) {
        DriverStation.reportError("Problem reseting " + c.getName() + " in subsystem " + getSubsystem(), false);
      }
    }
  }
}
