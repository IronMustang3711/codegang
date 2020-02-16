package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ChassisSubsystem;

public class RunChassis extends CommandBase{
    final ChassisSubsystem chassis;

    public RunChassis(ChassisSubsystem chassis){
        this.chassis = chassis;
    }
    @Override
    public void execute() {
        chassis.arcadeDrive(0.5, 0.0);
    }
    @Override
    public void end(boolean interrupted) {
        chassis.arcadeDrive(0.0, 0.0);
    }
}