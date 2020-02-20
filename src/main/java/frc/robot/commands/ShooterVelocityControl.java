package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ShooterSubsystem;

public class ShooterVelocityControl extends CommandBase {
  final ShooterSubsystem shooter;

  public ShooterVelocityControl(ShooterSubsystem shooter) {
    this.shooter = shooter;
    addRequirements(shooter);
  }

  @Override
  public void initialize() {
    shooter.upperController.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    shooter.upperController.configVoltageCompSaturation(10.0);
    shooter.upperController.config_kF(0, 0.051);
    shooter.upperController.config_kP(0, 0.18);
    //shooter.controller1.config_kD(0,0.02);
    shooter.upperController.configAllowableClosedloopError(0, 0);
    shooter.upperController.setNeutralMode(NeutralMode.Coast);
    shooter.upperController.configPeakOutputReverse(0.0);
    shooter.upperController.enableVoltageCompensation(true);

    shooter.lowerController.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    shooter.lowerController.configVoltageCompSaturation(10.0);
    shooter.lowerController.config_kF(0, 0.049);
    shooter.lowerController.config_kP(0, 0.1);
    shooter.lowerController.configAllowableClosedloopError(0, 0);
    shooter.lowerController.setNeutralMode(NeutralMode.Coast);
    shooter.lowerController.configPeakOutputReverse(0.0);
    shooter.lowerController.enableVoltageCompensation(true);
  }

  @Override
  public void execute() {
    double sp1 = shooter.lowerSetpoint.getDouble(20000.0);
    double sp2 = shooter.upperSetpoint.getDouble(16000.0);

    shooter.upperController.set(ControlMode.Velocity, sp1);
    shooter.lowerController.set(ControlMode.Velocity, sp2);



  }

  @Override
  public void end(boolean interrupted) {
    shooter.upperController.configVoltageCompSaturation(12.0);
    shooter.lowerController.configVoltageCompSaturation(12.0);
    shooter.upperController.set(ControlMode.Disabled, 0.0);
    shooter.lowerController.set(ControlMode.Disabled, 0.0);

  }
}
