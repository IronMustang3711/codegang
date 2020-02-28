package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterSubsystem;

public class ShootSequence {

    public static Command createShootSequence(IntakeSubsystem intake,FeederSubsystem feedworks,ShooterSubsystem shooter){
        var runShooter = new ShooterVelocityControl(shooter);
    
        var disableIntake = new InstantCommand(() -> intake.enableIntake(false), intake);
        var disableFeeder = new InstantCommand(() -> feedworks.setMotorOutputs(0.0), feedworks);
        var disableBoth = disableIntake.alongWith(disableFeeder);
        
        var waitForShooter = new WaitCommand(1.0);
        var runFeeder = new RunFeedworksPercentOutput(feedworks, 1.0);
        var runIntake = new RunInfeedPercentOutput(intake, 0.5);
        var runBoth = runFeeder.alongWith(new WaitCommand(0.3).andThen(runIntake));
        var feedControl = disableBoth.andThen(waitForShooter).andThen(runBoth);
        var fullCommand = runShooter.alongWith(feedControl);
        fullCommand.setName("Shoot");
        return fullCommand;
    }
}