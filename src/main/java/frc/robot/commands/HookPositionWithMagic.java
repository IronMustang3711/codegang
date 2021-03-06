package frc.robot.commands;

import frc.robot.subsystems.HookSubsystem;

public class HookPositionWithMagic extends HookPosition {
  public HookPositionWithMagic(HookSubsystem hook, double setpoint) {
    super(hook, setpoint);
  }

  @Override
  public void execute() {
    hook.setPositionWithMagic(setpoint);
  }
}
