package nz.ac.massey.a3;

/* Class for placing an object (surface or virtual camera) into the world scene

   We will need to convert from local to/from world coordinates. We need both the
   forward and inverse transformations. Encapsulate them here.

   Note: "local" can also mean camera coordinates

 */

public class Placement {
    public Matrix4 tLW;    // Transform local to world
    public Matrix4 tWL;    // Transform world to local

    Placement() {
        tLW = Matrix4.createIdentity();
        tWL = Matrix4.createIdentity();
    }

    // Transform point in local to point in world
    public Point4 toWorld(Point4 p) {
        return tLW.times(p);
    }

    // Transform point in world to point in local
    public Point4 toLocal(Point4 p) {
        return tWL.times(p);
    }

    /*
    Factory function to place get the placement of a surface in the world scene. So far it just
    uses the default identity matrices.

    Complete the implementation of this method to set up tLW, tWL
     */
    public static Placement placeModel(Point4 pD, Point4 pA, Point4 pS) {
        //D - position, A - rotations, S - size

        // Local to world transforms
        Matrix4 S  = Matrix4.createScale(pS.x, pS.y, pS.z); //scale
        Matrix4 Rx = Matrix4.createRotationX(pA.x); //rotation about X
        Matrix4 Ry = Matrix4.createRotationY(pA.y); //rotation about Y
        Matrix4 Rz = Matrix4.createRotationZ(pA.z); //rotation about Z
        Matrix4 R  = Rz.times(Ry).times(Rx);
        //Combine rotations Z Y X
        Matrix4 T  = Matrix4.createDisplacement(pD.x, pD.y, pD.z);

        Placement p = new Placement();

        //local to world, scale, rotate,  translate
        p.tLW = T.times(R).times(S);

        // inverse world to local
        Matrix4 Si  = Matrix4.createScale(1.0 / pS.x, 1.0 / pS.y, 1.0 / pS.z);
        Matrix4 Rxi = Matrix4.createRotationX(-pA.x);
        Matrix4 Ryi = Matrix4.createRotationY(-pA.y);
        Matrix4 Rzi = Matrix4.createRotationZ(-pA.z);

        // combine inverses in opposite order to R
        Matrix4 Ri  = Rxi.times(Ryi).times(Rzi);

        // Inverse translation
        Matrix4 Ti  = Matrix4.createDisplacement(-pD.x, -pD.y, -pD.z);
        //world to local
        p.tWL = Si.times(Ri).times(Ti);
        return p;
    }


    /*
    The remaining methods are for placing a camera into the scene. There
    is no need to change these methods.
     */
    private static Matrix4 lookAt(Point4 pCamera, Point4 xaxis, Point4 yaxis, Point4 zaxis) {
        Matrix4 R = new Matrix4(
                xaxis.x, xaxis.y, xaxis.z, 0,
                yaxis.x, yaxis.y, yaxis.z, 0,
                zaxis.x, zaxis.y, zaxis.z, 0,
                0, 0, 0, 1);
        Matrix4 T = Matrix4.createDisplacement(-pCamera.x, -pCamera.y, -pCamera.z);
        return R.times(T);
    }

    private static Matrix4 lookBack(Point4 pCamera, Point4 xaxis, Point4 yaxis, Point4 zaxis) {
        Matrix4 R = new Matrix4(
                xaxis.x, yaxis.x, zaxis.x, 0,
                xaxis.y, yaxis.y, zaxis.y, 0,
                xaxis.z, yaxis.z, zaxis.z, 0,
                0, 0, 0, 1);
        Matrix4 T = Matrix4.createDisplacement(pCamera.x, pCamera.y, pCamera.z);
        return T.times(R);
    }

    public static Placement placeCamera(Point4 pCamera, Point4 pTarget, Point4 vUp) {
        Point4 zaxis = pTarget.minus(pCamera);
        Point4 xaxis = Point4.cross(vUp, zaxis);
        Point4 yaxis = Point4.cross(zaxis, xaxis);
        xaxis.normalize();
        yaxis.normalize();
        zaxis.normalize();
        Placement p = new Placement();
        p.tLW = lookBack(pCamera, xaxis, yaxis, zaxis);
        p.tWL = lookAt(pCamera, xaxis, yaxis, zaxis);
        return p;
    }
}
