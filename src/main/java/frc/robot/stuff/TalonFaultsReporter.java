package frc.robot.stuff;

import com.ctre.phoenix.motorcontrol.Faults;
import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import java.util.ArrayList;

public class TalonFaultsReporter implements Runnable {

  private Faults faults = new Faults();

  private WPI_TalonSRX talon;

  TalonFaultsReporter(WPI_TalonSRX talon) {
    this.talon = talon;
    StickyFaults stickyFaults = new StickyFaults();
    talon.getStickyFaults(stickyFaults);
    if (stickyFaults.hasAnyFault())
      DriverStation.reportWarning(
        "(" + talon.getDescription() + ") sticky faults!->" + stickyFaultsString(stickyFaults),
        false);

  }

  static String stickyFaultsString(StickyFaults faults) {
    var faultMessages = new ArrayList<String>();

    if (faults.ForwardLimitSwitch) faultMessages.add("forward limit switch");
    if (faults.ForwardSoftLimit) faultMessages.add("forward soft limit");
    if (faults.HardwareESDReset) faultMessages.add("hardware esd reset");
    if (faults.RemoteLossOfSignal) faultMessages.add("remote loss of signal");
    if (faults.ResetDuringEn) faultMessages.add("reset while robot enabled");
    if (faults.ReverseLimitSwitch) faultMessages.add("reverse limit switch");
    if (faults.ReverseSoftLimit) faultMessages.add("reverse soft limit");
    if (faults.SensorOutOfPhase) faultMessages.add("sensor out of phase");
    if (faults.SensorOverflow) faultMessages.add("sensor overflow");
    if (faults.UnderVoltage) faultMessages.add("sensor underflow");

    return "[" + String.join(",", faultMessages) + "]";
  }

  static String faultsString(Faults faults) {
    var faultMesages = new ArrayList<String>();

    if (faults.UnderVoltage) faultMesages.add("under voltage");
    if (faults.ForwardLimitSwitch) faultMesages.add("forward limit switch");
    if (faults.ReverseLimitSwitch) faultMesages.add("reverse limit switch");
    if (faults.ForwardSoftLimit) faultMesages.add("forward soft limit");
    if (faults.ReverseSoftLimit) faultMesages.add("reverse soft limit");
    if (faults.HardwareFailure) faultMesages.add("hardware failure");
    if (faults.ResetDuringEn) faultMesages.add("ResetDuringEn");
    if (faults.SensorOverflow) faultMesages.add("sensor overflow");
    if (faults.SensorOutOfPhase) faultMesages.add("sensor out of phase");
    if (faults.HardwareESDReset) faultMesages.add("hardware esd reset");
    if (faults.RemoteLossOfSignal) faultMesages.add("remote loss of signal");
    if (faults.APIError) faultMesages.add("api error");

    return "[" + String.join(",", faultMesages) + "]";
  }

  @Override
  public void run() {
    talon.getFaults(faults);
    if (faults.hasAnyFault())
      DriverStation.reportWarning("(" + talon.getDescription() + ") faults->" + faultsString(faults), false);

  }

  public static void instrument(WPI_TalonSRX talon) {
    CommandScheduler.getInstance().addButton(new TalonFaultsReporter(talon));
  }

}