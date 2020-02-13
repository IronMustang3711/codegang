package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.PizzaWheelSubsytem;

public class PizzaWheelAngler extends CommandBase {
  final PizzaWheelSubsytem pizzaWheel;
  final int position;

  public PizzaWheelAngler(PizzaWheelSubsytem pizzaWheel, int position) {
    this.pizzaWheel = pizzaWheel;
    this.position = position;
    addRequirements(pizzaWheel);
  }

  @Override
  public void initialize() {

  }

  @Override
  public void execute() {


  }

  @Override
  public void end(boolean interrupted) {

  }
}
