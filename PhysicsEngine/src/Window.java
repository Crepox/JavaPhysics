
import java.awt.*;
import java.awt.image.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 * Write a description of class Window here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
class Window extends JFrame implements KeyListener {

    int[] keys = new int[256];
    JPanel screen = new JPanel();
    Engine w = new Engine(this);
    ArrayList<Circle> circleObjs = new ArrayList<Circle>();
    ArrayList<Wall> wallObjs = new ArrayList<Wall>();
    Circle player = new Circle(202, 50, 15);
    double speed = 0.5;
    double damping = 0.98;
    Image backBuffer;
    static int width = 800;
    static int height = 600;

    public static void main(String[] args) {
        Window f = new Window();
        f.setBounds(0, 0, width, height);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.init();
    }

    public void init() {
        add(screen);
        backBuffer = createImage(width, height);
        validate();
        addKeyListener(this);
        circleObjs.add(new Circle(100, 100, 10));
        circleObjs.add(new Circle(590, 200, 20));
        circleObjs.add(new Circle(510, 300, 50));
        wallObjs.add(new Wall(200, 100, 500, 150));
        wallObjs.add(new Wall(500, 150, 600, 10));
        w.startThread();
    }

    public void update(Graphics g) {
        g.drawImage(backBuffer, 0, 0, this);
    }

    public void paint(Graphics g) {
        update(g);
    }

    public void keyPressed(KeyEvent e) {
        keys[e.getKeyCode()] = 1;
    }

    public void keyReleased(KeyEvent e) {
        keys[e.getKeyCode()] = 0;
    }

    public void keyTyped(KeyEvent e) {
    }
}

class Engine implements Runnable {

    Window parent = null;

    public Engine(Window p) {
        this.parent = p;
    }

    public void startThread() {
        Thread d = new Thread(this);
        d.start();
    }

    public void run() {
        while (true) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
            }
            input();
            gameLoop();
            render();
        }
    }

    public void render() {
        Graphics parentGraphics = parent.screen.getGraphics();

        BufferedImage i = new BufferedImage(parent.getWidth(),
                parent.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics g = i.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, parent.getWidth(), parent.getHeight());
        g.setColor(Color.BLACK);

        for (Circle c : parent.circleObjs) {
            g.fillOval((int) (c.position.x - c.radius),
                    (int) (c.position.y - c.radius),
                    (int) (c.radius * 2),
                    (int) (c.radius * 2));
        }
        
        for (Wall w : parent.wallObjs) {
            g.drawLine((int)w.A.x, (int)w.A.y, (int)w.B.x, (int)w.B.y);
        }

        g.fillOval((int) (parent.player.position.x - parent.player.radius),
                (int) (parent.player.position.y - parent.player.radius),
                (int) (parent.player.radius * 2),
                (int) (parent.player.radius * 2));

        parentGraphics.drawImage(i, 0, 0, null);
    }

    public void gameLoop() {
        Circle player = parent.player;
        CollisionMath cm = new CollisionMath();
        
        player.velocity = Vector.add(player.velocity, new Vector(0, 0.4));
        player.position = Vector.add(player.position, player.velocity);
        //player.velocity = Vector.scale(player.velocity, parent.damping);
        
        ArrayList<Manifold> collisions = new ArrayList<Manifold>();
        
        for(Circle c : parent.circleObjs)
        {
            Manifold m = cm.circleCollision(player, c);
            if(m != null){
                collisions.add(m);
            }
        }
        for(Wall w : parent.wallObjs)
        {
            Manifold m = cm.wallCollision(player, w);
            if(m != null){
                collisions.add(m);
            }
        }
        
        if(!collisions.isEmpty()){
            Vector normalAvg = new Vector(0, 0);
            for(Manifold m : collisions){
                normalAvg = Vector.addScaled(normalAvg, m.normal,
                    Vector.dot(player.velocity, m.normal));
                player.position = Vector.addScaled(player.position,
                    m.normal, m.length);
            }
            
            normalAvg = Vector.normal(normalAvg);
            player.velocity = Vector.addScaled(player.velocity, normalAvg, 
                Vector.dot(player.velocity, normalAvg) * -2 * 0.85);
        }
    }

    public void input() {
        Circle player = parent.player;

        Vector rawVel = new Vector(0, 0);

        if (parent.keys[KeyEvent.VK_ESCAPE] == 1) {
            System.exit(0);
        }
        if (parent.keys[KeyEvent.VK_W] == 1) {
            rawVel.y = -1;
        }
        if (parent.keys[KeyEvent.VK_S] == 1) {
            rawVel.y = 1;
        }
        if (parent.keys[KeyEvent.VK_A] == 1) {
            rawVel.x = -1;
        }
        if (parent.keys[KeyEvent.VK_D] == 1) {
            rawVel.x = 1;
        }
        
        if (Vector.magSqr(rawVel) != 0) {
            rawVel = Vector.normal(rawVel);
        }

        player.velocity = Vector.add(player.velocity, Vector.scale(rawVel, parent.speed));
    }
}

class CollisionMath {

    public final Manifold circleCollision(Circle a, Circle b) {
        double m = Vector.mag(Vector.sub(a.position, b.position));
        if (m <= a.radius + b.radius) {
            Vector v = Vector.sub(a.position, b.position);
            if (m == 0) {
                v = new Vector(1, 0);
                m = 1;
            }
            v = Vector.normal(v);
            double depth = ((b.radius + a.radius) - m);
            if (Vector.dot(a.velocity, v) > a.radius) {
                v = Vector.scale(v, -1);
                depth = (m * 2) + ((b.radius + a.radius) - m);
            }
            
            return new Manifold(depth, v);
        }
        return null;
    }
    
    public Manifold wallCollision(Circle a, Wall w) {
        Vector v = pnt2line(a.position, w);
        return circleCollision(a, new Circle(v.x, v.y, 0));
    }

    public Vector pnt2line(Vector v, Wall w) {
        Vector p = Vector.sub(w.B, w.A);

        double sqrDist = Vector.magSqr(p);
        double u = ((v.x - w.A.x) * p.x + (v.y - w.A.y) * p.y) / sqrDist;

        if (u > 1) {
            u = 1;
        } else if (u < 0) {
            u = 0;
        }
        
        return Vector.addScaled(w.A, p, u);
    }
}