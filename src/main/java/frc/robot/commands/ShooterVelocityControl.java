package frc.robot.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
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
  private final NetworkTableEntry setpoint;

  public ShooterVelocityControl(ShooterSubsystem shooter) {
    this.shooter = shooter;
    addRequirements(shooter);


    var tab = Shuffleboard.getTab(ShooterSubsystem.class.getSimpleName());
    var errors = tab.getLayout("error", BuiltInLayouts.kList);
    error1 = errors.add("error1", 0.0).getEntry();
    error2 = errors.add("error2", 0.0).getEntry();

    var outs = tab.getLayout("output%", BuiltInLayouts.kList);
    output1 = outs.add("output1", 0.0).getEntry();
    output2 = outs.add("output2", 0.0).getEntry();

    setpoint = tab.add("setpoint", 0.0).getEntry();


  }

  @Override
  public void initialize() {
    shooter.controller1.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    shooter.controller1.configVoltageCompSaturation(10.0);
    shooter.controller1.config_kF(0, 0.049);
    shooter.controller1.config_kP(0, 0.015);
    //shooter.controller1.config_kD(0,0.02);
    shooter.controller1.configAllowableClosedloopError(0, 0);
    shooter.controller1.setNeutralMode(NeutralMode.Coast);
    shooter.controller1.configPeakOutputReverse(0.0);
    shooter.controller1.enableVoltageCompensation(true);

    shooter.controller2.configSelectedFeedbackSensor(FeedbackDevice.QuadEncoder);
    shooter.controller2.configVoltageCompSaturation(10.0);
    shooter.controller2.config_kF(0, 0.049);
    shooter.controller2.config_kP(0, 0.1);
    shooter.controller1.configAllowableClosedloopError(0, 0);
    shooter.controller1.setNeutralMode(NeutralMode.Coast);
    shooter.controller1.configPeakOutputReverse(0.0);
    shooter.controller1.enableVoltageCompensation(true);
  }

  @Override
  public void execute() {
    double sp = setpoint.getDouble(0.0);

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
    shooter.controller1.set(ControlMode.Disabled, 0.0);
    shooter.controller2.set(ControlMode.Disabled, 0.0);

  }
}
