
package deflektor;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JPanel;


public class ZkouskaRotaci extends JPanel{
    public static void main(String[] args)
    {
        JFrame f = new JFrame();
        f.add(new ZkouskaRotaci());
        f.setSize(900, 600);
        f.setBackground(Color.WHITE);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    static double i = 0;
    @Override
    public void paintComponent(Graphics g)
    {
        g.clearRect(0, 0, 900, 600);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(Color.BLUE);
        Rectangle rect2 = new Rectangle(300, 200, 20, 20);
        g2d.translate(300 + rect2.width / 2, 200 + rect2.height / 2);
        g2d.rotate(i += 0.01);
        g2d.translate(-(300 + rect2.width / 2), -(200 + rect2.height / 2));

        //g2d.rotate(Math.toRadians(45));
        g2d.draw(rect2);
        g2d.fill(rect2);
        repaint();
    }
}
