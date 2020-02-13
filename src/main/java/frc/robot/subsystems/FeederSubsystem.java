package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.ResetSensors;
import frc.robot.stuff.SensorReset;
import frc.robot.stuff.TalonFaultsReporter;

import java.util.List;

import static frc.robot.Constants.TalonConstants;

public class FeederSubsystem extends SubsystemBase implements SensorReset {

  private final JamDetector jam1;
  private final JamDetector jam2;

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

  public final WPI_TalonSRX controller1 = new WPI_TalonSRX(1);
  public final WPI_TalonSRX controller2 = new WPI_TalonSRX(4);

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

    jam1 = new JamDetector(controller1);
    jam2 = new JamDetector(controller2);

    jam1.onJam = () -> {
      DriverStation.reportWarning("jam 1", false);
      getCurrentCommand().cancel();
    };
    jam2.onJam = () -> {
      DriverStation.reportWarning("jam 2", false);
      getCurrentCommand().cancel();
    };

    setupShuffleboard();
  }

  private void setupShuffleboard() {
    var tab = Shuffleboard.getTab(FeederSubsystem.class.getSimpleName());
    tab.add(controller1);
    tab.add(controller2);
    tab.addNumber("feeder1_pos", this::getFirstEncoderPosition);
    tab.addNumber("feeder2_pos", this::getSecondEncoderPosition);
    tab.addNumber("feeder1_vel", this::getFirstEncoderVelocity);
    tab.addNumber("feeder2_vel", this::getSecondEncoderVelocity);
    tab.addNumber("feeder1_current", controller1::getStatorCurrent);
    tab.addNumber("feeder2_current", controller2::getStatorCurrent);
    tab.add(new ResetSensors<>(this));

  }

  public double getFirstEncoderPosition() {
    return controller1.getSelectedSensorPosition();
  }

  public double getSecondEncoderPosition() {
    return controller2.getSelectedSensorPosition();
  }

  /**
   * @return sensor velocity (in raw sensor units) per 100ms.
   */
  public double getFirstEncoderVelocity() {
    return controller1.getSelectedSensorVelocity(TalonConstants.PRIMARY_PID);
  }

  public double getSecondEncoderVelocity() {
    return controller2.getSelectedSensorVelocity(TalonConstants.PRIMARY_PID);
  }

  @Override
  public void periodic() {
    jam1.run();
    jam2.run();
  }

  public void setMotorOutputs(double first) {
    setMotorOutputs(first, first);
  }

  public void setMotorOutputs(double first, double second) {
    controller1.set(first);
    controller2.set(second);
  }

  public boolean isEnabled() {
    return controller1.getMotorOutputVoltage() > 0.0 && controller2.getMotorOutputVoltage() > 0.0;
  }

  @Override
  public void resetSensors() {
    for (var c : List.of(controller1, controller2)) {
      ErrorCode errorCode = c.getSensorCollection().setQuadraturePosition(0, TalonConstants.DEFAULT_TIMEOUT);
      if (errorCode != ErrorCode.OK) {
        DriverStation.reportError("Problem reseting " + c.getName() + " in subsystem " + getSubsystem(), false);
      }
    }
  }
}
