package frc.robot.subsystems;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.commands.ResetSensors;
import frc.robot.stuff.SensorReset;

public class PizzaWheelSubsytem extends SubsystemBase implements SensorReset {

  WPI_TalonSRX turner = new WPI_TalonSRX(6);
  Talon angler = new Talon(0);
  ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);

  public PizzaWheelSubsytem() {
    setName("PizzaWheel");
    addChild("turner", turner);
    addChild("angler", angler);

    turner.setInverted(false);
    turner.setSensorPhase(true);

    angler.setInverted(true);

    setupShuffleboardTab();

  }

  void setupShuffleboardTab() {
    ShuffleboardTab tab = Shuffleboard.getTab(PizzaWheelSubsytem.class.getSimpleName());
    tab.add(turner);
    tab.add(angler);

    tab.addNumber("pizza encoder", () -> (double) turner.getSelectedSensorPosition());
    tab.addDoubleArray("RGB", () -> new double[]{colorSensor.getRed(), colorSensor.getGreen(), colorSensor.getBlue()});
    tab.addNumber("proximity", () -> colorSensor.getProximity());
    tab.add(new ResetSensors<>(this));

  }

  public void periodic() {

  }

  @Override
  public void resetSensors() {
    ErrorCode errorCode = turner.getSensorCollection()
                                .setQuadraturePosition(0, Constants.TalonConstants.DEFAULT_TIMEOUT);
    if (errorCode != ErrorCode.OK) {
      DriverStation.reportError("failure reseting Pizzawheel(turn) encoder: " + errorCode, false);
    }
  }
}
