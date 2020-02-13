package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ShooterSubsystem;

public class ShooterVelocityControl extends CommandBase {
  final ShooterSubsystem shooter;
  private final NetworkTableEntry error1;
  private final NetworkTableEntry output1;
  private final NetworkTableEntry error2;
  private final NetworkTableEntry output2;

  public ShooterVelocityControl(ShooterSubsystem shooter) {
    this.shooter = shooter;
    addRequirements(shooter);


    shooter.setpoint.setDefaultDouble(1000.0);

    var tab = Shuffleboard.getTab(ShooterSubsystem.class.getSimpleName());
    var errors = tab.getLayout("error", BuiltInLayouts.kList);
    error1 = errors.add("error1", 0.0).getEntry();
    error2 = errors.add("error2", 0.0).getEntry();

    var outs = tab.getLayout("output%");
    output1 = outs.add("output1", 0.0).getEntry();
    output2 = outs.add("output2", 0.0).getEntry();


  }

  @Override
  public void initialize() {
    shooter.controller1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    shooter.controller1.configVoltageCompSaturation(10.0);
    shooter.controller1.config_kF(0, 0.04);
    shooter.controller1.config_kP(0, 0.01);
    shooter.controller1.configAllowableClosedloopError(0, 100);

    shooter.controller2.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    shooter.controller2.configVoltageCompSaturation(10.0);
    shooter.controller2.config_kF(0, 0.04);
    shooter.controller2.config_kP(0, 0.01);
    shooter.controller2.configAllowableClosedloopError(0, 100);
  }

  @Override
  public void execute() {
    double sp = shooter.setpoint.getDouble(0.0);

    shooter.controller1.set(ControlMode.Velocity, sp);
    shooter.controller2.set(ControlMode.Velocity, sp);

    error1.setDouble(shooter.controller1.getClosedLoopError(0));
    output1.setDouble(shooter.controller1.getMotorOutputPercent());
    error2.setDouble(shooter.controller2.getClosedLoopError(0));
    output2.setDouble(shooter.controller2.getMotorOutputPercent());

  }

  @Override
  public void end(boolean interrupted) {
    shooter.controller1.configVoltageCompSaturation(12.0);
    shooter.controller2.configVoltageCompSaturation(12.0);

  }
}
