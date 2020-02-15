package frc.robot.stuff;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.InstantCommand;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.util.stream.Stream;

public class Danger {

  public static void install() {
    ShuffleboardTab tab = Shuffleboard.getTab(Danger.class.getSimpleName());
    tab.add("stop complaining about joysticks being unplugged",
            new InstantCommand(() -> {
              try {
                Field nextMessageTime = DriverStation.class.getDeclaredField("m_nextMessageTime");
                nextMessageTime.setAccessible(true);
                nextMessageTime.setDouble(DriverStation.getInstance(), Double.MAX_VALUE);
              } catch (NoSuchFieldException | IllegalAccessException | InaccessibleObjectException e) {
                e.fillInStackTrace();
                var ccw = new CharArrayWriter(120);
                e.printStackTrace(new PrintWriter(ccw));
                DriverStation.reportError(new String(ccw.toCharArray()), e.getStackTrace());

              }
            }));

    tab.add("kill", new InstantCommand(() -> {
      System.exit(1);
    }));

    tab.add("clear networkTables", new InstantCommand(() -> {
      deleteTableRecursive(NetworkTableInstance.getDefault().getTable("/"));
    }));
  }

  static void deleteTableRecursive(NetworkTable table) {
    if (table == null) return;

    //delete child tables
    table.getSubTables().stream().map(table::getSubTable).forEach(Danger::deleteTableRecursive);

    //delete entries
    table.getKeys().stream().map(table::getEntry).forEach(NetworkTableEntry::delete);

    //delete anything else (subtables?)
    Stream.concat(table.getSubTables().stream(), table.getKeys().stream())
          .forEach(table::delete);

  }

}
