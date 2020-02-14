package frc.robot.stuff;

import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

public class ShuffleboardPID implements Sendable {
  @FunctionalInterface
  public interface Observer {
    void valueChanged(Param param, double oldVal, double newVal);
  }

  Observer observer;

  double p, i, d, setpoint;

  public enum Param {
    P, I, D, SETPOINT;
  }


  public ShuffleboardPID(double p, double i, double d, double setpoint) {
    this.p = p;
    this.i = i;
    this.d = d;
    this.setpoint = setpoint;
  }

  public void initSendable(SendableBuilder builder) {
    builder.setSmartDashboardType("PIDController");
    builder.addDoubleProperty("p", this::getP, this::setP);
    builder.addDoubleProperty("i", this::getI, this::setI);
    builder.addDoubleProperty("d", this::getD, this::setD);
    builder.addDoubleProperty("setpoint", this::getSetpoint, this::setSetpoint);
  }

  void valueUpdated(Param p, double oldValue, double newValue) {
    if (observer != null) observer.valueChanged(p, oldValue, newValue);
  }

  public double getP() {
    return p;
  }

  public void setP(double p) {
    if (this.p == p) return;
    valueUpdated(Param.P, this.p, this.p = p);
  }


  public double getI() {
    return i;
  }

  public void setI(double i) {
    if (this.i == i) return;
    valueUpdated(Param.I, this.i, this.i = i);
  }

  public double getD() {
    return d;
  }

  public void setD(double d) {
    if (this.d == d) return;
    valueUpdated(Param.D, this.d, this.d = d);
  }

  public double getSetpoint() {
    return setpoint;
  }

  public void setSetpoint(double setpoint) {
    if (this.setpoint == setpoint) return;
    valueUpdated(Param.SETPOINT, this.setpoint, this.setpoint = setpoint);
  }
}
