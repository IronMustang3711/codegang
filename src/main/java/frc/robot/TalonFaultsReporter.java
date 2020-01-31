package frc.robot;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class TalonFaultsReporter implements Runnable {

    private Faults faults = new Faults();

    private WPI_TalonSRX talon;

     TalonFaultsReporter(WPI_TalonSRX talon) {
        this.talon = talon;
        StickyFaults stickyFaults = new StickyFaults();
        talon.getStickyFaults(stickyFaults);
        if (stickyFaults.hasAnyFault())
            DriverStation.reportWarning("talon[" + talon.getName() + "] stickyFaults = " + stickyFaults, false);

       
    }

    @Override
    public void run() {
        talon.getFaults(faults);
        if (faults.hasAnyFault())
            DriverStation.reportWarning("talon[" + talon.getName() + "] faults = " + faults, false);

    }

	public static void instrument(WPI_TalonSRX talon) {
        CommandScheduler.getInstance().addButton(new TalonFaultsReporter(talon));
	}

}