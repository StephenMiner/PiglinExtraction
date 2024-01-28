package piglinextraction.me.stephenminer;

import org.bukkit.util.Vector;

public class Rotation {

    public Vector rotateAroundAxisX(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public  Vector rotateAroundAxisY(Vector v, double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public Vector rotateAroundAxisZ(Vector v, double angle){
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        double x = v.getX()*cos - v.getX()*sin;
        double y = v.getY()*sin+v.getY()*cos;
        return v.setX(x).setY(y);
    }
}
