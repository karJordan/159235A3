/*
Start up package for 159.235 Assignment 3 (Semester 2, 2022)
 */
package nz.ac.massey.a3;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Main {
    static double ambLight = .45;



    public static ArrayList<ThreeDSurface> makeBox(
            double x, double y, double z,
            Point4 center, Point4 angles,
            SurfaceColour sc, Material mat) {

        ArrayList<ThreeDSurface> faces = new ArrayList<>();
        double hx = x * 0.5;
        double hy = y * 0.5;
        double hz = z * 0.5;
        SurfaceGeometry quad = new Square();

        Placement worldP = Placement.placeModel(center, angles, Point4.createPoint(1, 1, 1));

        // Local offsets for each face
        Point4[] p = {
                Point4.createPoint( hx,  0,  0),  // right
                Point4.createPoint(-hx,  0,  0),  // left
                Point4.createPoint( 0,  hy,  0),  // front
                Point4.createPoint( 0, -hy,  0),  // back
                Point4.createPoint( 0,  0,  hz),  // top
                Point4.createPoint( 0,  0, -hz)   // bottom
        };

        // Local rotations
        Point4[] a = {
                Point4.createPoint(0, Math.toRadians(90), 0),    // right
                Point4.createPoint(0, Math.toRadians(-90), 0),   // left
                Point4.createPoint(Math.toRadians(-90), 0, 0),   // front
                Point4.createPoint(Math.toRadians(90), 0, 0),    // back
                Point4.createPoint(0, 0, 0),                     // top
                Point4.createPoint(Math.toRadians(180), 0, 0)    // bottom
        };

        // Correct per-face scales (matching face size)
        Point4[] s = {
                Point4.createPoint(z, y, 1),   // right
                Point4.createPoint(z, y, 1),   // left
                Point4.createPoint(x, z, 1),   // front
                Point4.createPoint(x, z, 1),   // back
                Point4.createPoint(x, y, 1),   // top
                Point4.createPoint(x, y, 1)    // bottom
        };

        for (int i = 0; i < 6; i++) {
            Placement localP = Placement.placeModel(p[i], a[i], s[i]);
            Placement faceP = new Placement();
            faceP.tLW = worldP.tLW.times(localP.tLW);
            faceP.tWL = localP.tWL.times(worldP.tWL);

            faces.add(new ThreeDSurface(quad, mat, sc, faceP));
        }

        return faces;
    }


    /* Draw black lines at the centre of the image. Call this to verify the camera
    pointing direction
     */
    public static void putAxes(BufferedImage image) {
        int npixx = image.getWidth();
        int npixy = image.getHeight();
        int L = (int) (Math.floor(0.0625 * npixx));
        for (int k = 0; k < L; ++k) {
            int rgb = 0;
            image.setRGB(k + (npixx - 1) / 2, (npixy - 1) / 2, rgb);
            image.setRGB((npixx - 1) / 2, k + (npixy - 1) / 2, rgb);
        }
    }

    /* Generate a test planar surface with a uniform colour
        pD : x,y,z position in scene
        pA : rotation angles around x, y, z-axes
        pS : scaling in x,y,z
     */
    public static ThreeDSurface testSurface(Point4 pD, Point4 pA, Point4 pS) {
        return new ThreeDSurface(
                new Square(),
                new Material(0.0, 0.5, 10),
                new UniformColour(Color.GRAY),
                Placement.placeModel(pD, pA, pS)
        );
    }


    public static void main(String[] args) {

        // Position of light source in world coordinates
        //Point4 pLightW = Point4.createPoint(50, 50, 100);
        Point4 pLightW = Point4.createPoint(40, -120, 120);

        // Get a camera with field of view 15 degrees in y
        double fovy = Math.toRadians(15);
        int npixx = 801;
        int npixy = 801;
        Camera camera = Camera.standardCamera(fovy, npixx, npixy);

        // Position and orientation of the camera in the world scene
        //Point4 pCam = Point4.createPoint(500, -500, 50);
        Point4 pCam = Point4.createPoint(300, -600, 100);
        Point4 pTarg = Point4.createPoint(20, 30, 20);
        Point4 vUp = Point4.createVector(0, 0, -1);
        camera.rePoint(pCam, pTarg, vUp);

        // Get a scene graph that manages the list of surfaces to be rendered
        SceneGraph scene = new SceneGraph();


        //Add a planar square surface at the origin
        //Point4 pD = Point4.createPoint(0,0,0);
        //Point4 pA = Point4.createPoint(0,0,0);
        //Point4 pS = Point4.createPoint(10,10,1);
        // scene.add(testSurface(pD, pA, pS));



        //Beachball
        ThreeDSurface beachball = new ThreeDSurface(
                new Sphere(10.0),
                new Material(ambLight, 0.45, 100),
                //new UniformColour(Color.BLUE),
                new TextureColour(new TextureMap("beachball.jpg")),//apply beachball.jpg to sphere
                Placement.placeModel(
                        Point4.createPoint(35, -8, 10),     // position
                        Point4.createPoint(0, 0, 0),     // rotation (radians)
                        Point4.createPoint(1, 1, 1)      // scale
                )
        );

        scene.add(beachball);

        //Shiny blue ball`
        ThreeDSurface ball = new ThreeDSurface(
                new Sphere(20.0),
                new Material(.20, .3, 25),
                new UniformColour(Color.BLUE),
                //new TextureColour(new TextureMap("beachball.jpg")),//apply beachball.jpg to sphere
                Placement.placeModel(
                        Point4.createPoint(20, -82, 20),     // position
                        Point4.createPoint(0, 0, 0),     // rotation (radians)
                        Point4.createPoint(1, 1, 1)      // scale
                )
        );
        scene.add(ball);


        // ground
        ThreeDSurface ground = new ThreeDSurface(
                new Square(),                         // z=0 in local coords
                new Material(ambLight, 0.5, 10),
                new TextureColour(new TextureMap("concrete.jpg")),
                //new UniformColour(Color.GREEN),
                Placement.placeModel(
                        Point4.createPoint(225, -225, 0),     // move to y = -3
                        Point4.createPoint(Math.toRadians(0), 0, 0), // rotate so normal points -Y
                        Point4.createPoint(500, 500, 1)     // make it big
                )
        );
        scene.add(ground);

        ThreeDSurface sky = new ThreeDSurface(
                new Square(),                         // z=0 in local coords
                new Material(ambLight+1, 0.5, 10),
                //new UniformColour(Color.GREEN),
                new TextureColour(new TextureMap("clouds.jpg")),
                Placement.placeModel(
                        Point4.createPoint(-500, 500, 50),     // move to y = -3
                        Point4.createPoint(Math.toRadians(90), 0, Math.toRadians(45)), // rotate so normal points -Y
                        Point4.createPoint(1000, 1000, 1)     // make it big
                )
        );
        scene.add(sky);

        ThreeDSurface futuredoor = new ThreeDSurface(
                new Square(),                         // z=0 in local coords
                new Material(ambLight+.3, 0.5, 10),
                //new UniformColour(Color.GREEN),
                new TextureColour(new TextureMap("scifi.jpg")),
                Placement.placeModel(
                        Point4.createPoint(50, 30, 25),     // move to y = -3
                        Point4.createPoint(0, Math.toRadians(90), Math.toRadians(90)), // rotate so normal points -Y
                        Point4.createPoint(50, 50, 1)     // make it big
                )
        );
        scene.add(futuredoor);

        ThreeDSurface carpet = new ThreeDSurface(
                new Square(),                         // z=0 in local coords
                new Material(ambLight+.3, 0.5, 10),
                //new UniformColour(Color.GREEN),
                new TextureColour(new TextureMap("carpet.jpg")),
                Placement.placeModel(
                        Point4.createPoint(50, 0, .01),     // move to y = -3
                        Point4.createPoint(0, 0, 0), // rotate so normal points -Y
                        Point4.createPoint(50, 100, 1)     // make it big
                )
        );
        carpet.uvScaleU = 1.0;
        carpet.uvScaleV = 1.0;
        carpet.uvRotate = Math.toRadians(90);
        scene.add(carpet);

        //box left
        ArrayList<ThreeDSurface> box = makeBox(50, 50, 100,
                Point4.createPoint(0, 50, 50),
                Point4.createPoint(0,0,0),
                new TextureColour(new TextureMap("bricks.jpg")),
                new Material(ambLight, 0.5, 20));
        for (ThreeDSurface s: box){
            s.uvScaleU = 2.5;
            s.uvScaleV = 1.15;
        }
        scene.add(box);

        //box top
        ArrayList<ThreeDSurface> box2 = makeBox(50, 50, 50,
                Point4.createPoint(50, 50, 75),
                Point4.createPoint(0,0,0),
                new TextureColour(new TextureMap("bricks.jpg")),
                new Material(ambLight, 0.5, 20));
        for (ThreeDSurface s: box2){
            s.uvScaleU = 2.5;
            s.uvScaleV = 2.15;
            //s.uvRotate = Math.toRadians(180);
        }
        scene.add(box2);

        //box roof
        ArrayList<ThreeDSurface> roof = makeBox(155, 5, 50,
                Point4.createPoint(50, 50-10, 115),
                Point4.createPoint(Math.toRadians(-48),0,0),
                new TextureColour(new TextureMap("wood.jpg")),
                //new UniformColour(Color.red),
                //new TextureColour(new TextureMap("bricks.jpg")),
                new Material(ambLight, 0.5, 20));
        for (ThreeDSurface s: box2){
            s.uvScaleU = 2.5;
            s.uvScaleV = 2.15;
            //s.uvRotate = Math.toRadians(180);
        }
        scene.add(roof);


        //box right
        ArrayList<ThreeDSurface> box3 = makeBox(50, 50, 100,
                Point4.createPoint(100, 50, 50),
                Point4.createPoint(0,0,0),
                new TextureColour(new TextureMap("bricks.jpg")),
                new Material(ambLight, 0.5, 20));

        for (ThreeDSurface s: box3){
            s.uvScaleU = 2.0;
            s.uvScaleV = 1.15;
        }
        scene.add(box3);


        //low wall left
        ArrayList<ThreeDSurface> leftwall = makeBox(10, 100, 40,
                Point4.createPoint(-20, -25, 20),
                Point4.createPoint(0,0,0),
                new TextureColour(new TextureMap("bricks.jpg")),
                new Material(ambLight, 0.5, 20));

        for (ThreeDSurface s: leftwall){
            s.uvScaleU = 1;
            s.uvScaleV = 3;
            s.uvRotate = Math.toRadians(90);
        }
        scene.add(leftwall);

        ThreeDSurface portrait = new ThreeDSurface(
                new Square(),                         // z=0 in local coords
                new Material(ambLight+.3, 0.5, 10),
                //new UniformColour(Color.GREEN),
                new TextureColour(new TextureMap("dachshund.jpg")),
                Placement.placeModel(
                        Point4.createPoint(50, 24, 75),     // move to y = -3
                        Point4.createPoint(Math.toRadians(90), 0, 0), // rotate so normal points -Y
                        Point4.createPoint(30, 30, 1)     // make it big
                )
        );
        portrait.uvRotate = Math.toRadians(180);
        scene.add(portrait);


        //box frametop
        ArrayList<ThreeDSurface> frametop = makeBox(40, 3, 5,
                Point4.createPoint(50, 25, 92),
                Point4.createPoint(0,0,0),
                new TextureColour(new TextureMap("wood.jpg")),
                new Material(ambLight, 0.5, 20));
        for (ThreeDSurface s: frametop){
            s.uvScaleU = 1.0;
            s.uvScaleV = 1.0;
            //s.uvRotate = Math.toRadians(180);
        }
        scene.add(frametop);

        //box framebot
        ArrayList<ThreeDSurface> framebot = makeBox(40, 3, 5,
                Point4.createPoint(50, 25, 58),
                Point4.createPoint(0,0,0),
                new TextureColour(new TextureMap("wood.jpg")),
                new Material(ambLight, 0.5, 20));
        for (ThreeDSurface s: framebot){
            s.uvScaleU = 1.0;
            s.uvScaleV = 1.0;
            //s.uvRotate = Math.toRadians(180);
        }
        scene.add(framebot);

        //box frameleft
        ArrayList<ThreeDSurface> frameleft = makeBox(5, 3, 30,
                Point4.createPoint(33, 25, 75),
                Point4.createPoint(0,0,0),
                new TextureColour(new TextureMap("wood.jpg")),
                new Material(ambLight, 0.5, 20));
        for (ThreeDSurface s: frameleft){
            s.uvScaleU = 1.0;
            s.uvScaleV = 2.0;
            s.uvRotate = Math.toRadians(90);
        }
        scene.add(frameleft);

        //box frameright
        ArrayList<ThreeDSurface> frameright = makeBox(5, 3, 30,
                Point4.createPoint(67, 25, 75),
                Point4.createPoint(0,0,0),
                new TextureColour(new TextureMap("wood.jpg")),
                new Material(ambLight, 0.5, 20));
        for (ThreeDSurface s: frameright){
            s.uvScaleU = 1.0;
            s.uvScaleV = 2.0;
            s.uvRotate = Math.toRadians(90);
        }
        scene.add(frameright);




        //box randombox rotated on x
        ArrayList<ThreeDSurface> boxrandom = makeBox(20,20,20,
                Point4.createPoint(90, -20, 10),
                Point4.createPoint(0,0,Math.toRadians(25)),
                new UniformColour(Color.GREEN),
                new Material(ambLight, 0.5, 20));
        scene.add(boxrandom);

        //another rotated box
        ArrayList<ThreeDSurface> redbox = makeBox(20,20,20,
                Point4.createPoint(90, -20, 30),
                Point4.createPoint(0,0,Math.toRadians(50)),
                new UniformColour(Color.red),
                new Material(ambLight, 0.5, 20));
        scene.add(redbox);


        // Render the scene at the given camera and light source
        scene.render(camera, pLightW);

        // Uncomment if you want to verify the camera target point in the scene
        // putAxes(camera.image);

        // Display image in a JPanel/JFrame
        Display.show(camera.image);

        // Uncomment if you want to save your scene in an image file
        //Display.write(camera.image, "scene.png");

    }
}
