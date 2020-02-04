package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.ColorSensorV3;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class PizzaWheelSubsytem extends SubsystemBase {

  WPI_TalonSRX turner = new WPI_TalonSRX(0);
  WPI_TalonSRX angler = new WPI_TalonSRX(86);
  ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kMXP);

  public PizzaWheelSubsytem() {
    setName("PizzaWheel");
    addChild("turner", turner);
    addChild("angler", angler);
  }

  public void periodic() {
    SmartDashboard.putNumberArray("RBG",
                                  new double[]{colorSensor.getRed(), colorSensor.getBlue(), colorSensor.getGreen()});
    SmartDashboard.putNumber("Proximity", colorSensor.getProximity());

  }
}
