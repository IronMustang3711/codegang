/*
package frc.robot.subsystems;

import edu.wpi.first.wpilibj.I2C;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ColorSubsystem extends SubsystemBase{ // Jason if you see this, I'm not sure if I'm anywhere close to doing this right, but at least I tried
    I2C sensor;
    //private ByteBuffer buf = new ByteBuffer.allocate(2); this doesnt work for some reason

    public void ColorSensor(I2C.Port port){
        sensor = new I2C(port, 0x12); //Need I2C address
        sensor.write(0x00, 0b00000011);
    }

    int readRegister(int address){
        sensor.read(address, 2, buf); //everytime buf is used, the previous issue needs to be resolved
        buf.order(ByteOrder.LITTLE_ENDIAN);
        return buf.getShort(0);
    }

    public int red() {
        return readRegister(0x16); //The register here may be incorrect
    }

    public int green() {
        return readRegister(0x18);
    }

    public int blue() {
        return readRegister(0x1A);
    }

    public int clear() {
        return readRegister(0x14);
    }

    public int prox() {
        return readRegister(0x1C);
    }

}
*/