package frc.robot.subsystems;

import java.util.List;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.stuff.TalonFaultsReporter;

public class ShooterSubsystem extends SubsystemBase {
  private WPI_TalonSRX lowerController = new WPI_TalonSRX(5);
  private WPI_TalonSRX upperController = new WPI_TalonSRX(27);

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

    addChild("lowerShooter", lowerController);
    addChild("upperShooter", upperController);
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
    double[] positions = {getLowerEncoderPosition(), getUpperEncoderPosition()};
    SmartDashboard.putNumber("lowerShooter_position", positions[0]);
    SmartDashboard.putNumber("upperShooter_position", positions[1]);
    SmartDashboard.putNumberArray("shooter_position", positions);

    double[] vels = {getLowerEncoderVelocity(), getUpperEncoderVelocity()};
    SmartDashboard.putNumber("lowerShooter_velocity", vels[0]);
    SmartDashboard.putNumber("upperShooter_velocity", vels[1]);
    SmartDashboard.putNumberArray("shooter_velocity", vels);


  }

  public void enableShooter(boolean enable) {
//    if (enable) lowerController.set(1.0);
//    else lowerController.set(0.0);
    if (enable) {
      lowerController.set(1.0);
      upperController.set(1.0);
    } else {
      lowerController.set(0.0);
      upperController.set(0.0);
    }

  }

  public boolean isEnabled() {
    return lowerController.get() != 0.0;
  }
}