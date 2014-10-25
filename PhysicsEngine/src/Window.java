
import javax.swing.*;

/**
 * Write a description of class Window here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

class Window  {
    public static void main(String[] args) {
        Game f = new Game();
        f.setBounds(0, 0, Game.WIDTH, Game.HEIGHT);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        f.init();
    }    
}