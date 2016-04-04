/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package deflektor;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.shape.Line;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author Tom
 */
public class Deflektor extends JPanel {
    
    static int sirka = 900;
    static int vyska = 600;
    static boolean doleva;
    static boolean doprava;
    static boolean released;
    
    static double rychlost = 0.04;
    
    static int aktAngle = 0;
    
    static int blokX[];
    static int blokY[];
    static int pocetBloku;
    
    private LinkedList<Line> lines = new LinkedList<Line>();
    
    static int posuvCara = 0;
    
    double[] i = new double[pocetBloku];
    Rectangle2D[] kolemBlok = new Rectangle2D[pocetBloku];
    BufferedImage[] blok = new BufferedImage[pocetBloku];
    AffineTransform[] at = new AffineTransform[pocetBloku];
    
    static int blokNow = 0;
    public static void main(String[] args) {
        JFrame f = new JFrame();
        nastavBloky();
        f.addKeyListener(new KeyListener() {

            @Override
            public void keyPressed(KeyEvent e)
            {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT)
                {
                    doleva = true;
                    released = false;
                }
                if (key == KeyEvent.VK_RIGHT)
                {
                    doprava = true;
                    released = false;
                }
                if (key == KeyEvent.VK_UP)
                {
                    blokNow++;
                    if (blokNow >= pocetBloku)
                        blokNow = 0;
                }
                if (key == KeyEvent.VK_DOWN)
                {
                    blokNow--;
                    if (blokNow < 0)
                        blokNow = pocetBloku - 1;
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                int key = e.getKeyCode();
                if (key == KeyEvent.VK_LEFT)
                {
                    doleva = false;
                }
                if (key == KeyEvent.VK_RIGHT)
                {
                    doprava = false;
                }
                released = true;
            }
            @Override
            public void keyTyped(KeyEvent e)
            {
                
            }
        });
        f.add(new Deflektor());
        f.setSize(sirka, vyska);
        f.setBackground(Color.WHITE);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    static int[] poziceX;
    static int[] poziceY;
    static int[] endX;
    static int[] endY;
    static int pocetCar = 1;
    public static void nastavBloky()
    {
        pocetBloku = 3;
        blokX = new int[pocetBloku];
        blokY = new int[pocetBloku];
        blokX[0] = sirka - 150;
        blokY[0] = vyska - 115;
        blokX[1] = sirka - 150;
        blokY[1] = 100;
        blokX[2] = 150;
        blokY[2] = 100; 
        poziceX = new int[pocetBloku];
        poziceY = new int[pocetBloku];
        endX = new int[pocetBloku];
        endY = new int[pocetBloku];
    }
    @Override
    public void paintComponent(Graphics g)
    {
        g.clearRect(0, 0, sirka, vyska);
        BufferedImage laser = loadImg("laser.png");
        g.drawImage(laser, 10, vyska - 100, null);
        g.setColor(Color.MAGENTA);
        
        for (int j = 0; j < pocetBloku; j++)
        {
            if (i[j] == 0)
            {
                blok[j] = (BufferedImage) vykresliBlok(g, blokX[j], blokY[j], blok[j]);
            }
            at[j] = AffineTransform.getTranslateInstance(blokX[j], blokY[j]);
            
            Graphics2D g2d = blok[j].createGraphics();
    
            // Draw on the buffered image
            g2d.setColor(Color.GREEN);
            kolemBlok[j] = new Rectangle.Float(0, 0, 1000, 1000);
            g2d.fill(kolemBlok[j]);
            g2d.dispose();
        }
        g.drawRect(blokX[blokNow] - blok[blokNow].getHeight() / 2 + blok[blokNow].getWidth() / 2 - 5, blokY[blokNow] - 5, blok[blokNow].getHeight() + 10, blok[blokNow].getHeight() + 10);
        
        if (doleva == true)
        {
            otocBloky(g, -1);
        }
        else if (doprava == true)
        {
            otocBloky(g, 1);
        }
        else if (released == true)
        {
            otocBloky(g, 0);
        }
        poziceY[0] = vyska - 100 + (laser.getHeight() / 2);
        poziceX[0] = 10 + laser.getWidth();
        Line2D l2d = new Line2D.Float(poziceX[0], poziceY[0], sirka, poziceY[0]) {};
        if (jeKolize(l2d, kolemBlok[0]))
        {
            g.drawLine(poziceX[0], poziceY[0], (int) getIntersectionPoint(l2d, kolemBlok[0])[2].getX(), (int) getIntersectionPoint(l2d, kolemBlok[0])[2].getY());
        }
        repaint();
    }
    
    void otocBloky(Graphics g, int zpusob)
    {
        if (zpusob != 0)
        {
            for (int j = 0; j < pocetBloku; j++)
            {
                if (j != blokNow)
                {
                    at[j].rotate(i[j], blok[j].getWidth()/ 2, blok[j].getHeight()/ 2);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.drawImage(blok[j], at[j], null);
                }
                else
                {
                    if (zpusob == 1)
                        at[j].rotate(i[j] += rychlost, blok[j].getWidth()/ 2, blok[j].getHeight()/ 2);
                    else
                        at[j].rotate(i[j] -= rychlost, blok[j].getWidth()/ 2, blok[j].getHeight()/ 2);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.drawImage(blok[j], at[j], null);
                }
            }
        }
        else
        {
            for (int j = 0; j < pocetBloku; j++)
            {
                at[j].rotate(i[j], blok[j].getWidth()/ 2, blok[j].getHeight()/ 2);
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(blok[j], at[j], null);
            }
        }

    }
    
    public static boolean jeKolize(Line2D l2d, Rectangle2D rect)
    {
        if (l2d.intersects(rect))
            return true;
        else
            return false;
    }
    
    public Image vykresliBlok(Graphics g, int poziceX, int poziceY, Image blok)
    {
        blok = loadImg("block.png");
        Graphics2D g2d = (Graphics2D) g;
        AffineTransform a = AffineTransform.getTranslateInstance(poziceX, poziceY);
        g2d.drawImage(blok, a, null);
        return blok;
    }
    
    public Point2D[] getIntersectionPoint(Line2D line, Rectangle2D rectangle)
    {
        Point2D[] p = new Point2D[4];

        // Top line
        p[0] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX(),
                        rectangle.getY(),
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY()));
        // Bottom line
        p[1] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX(),
                        rectangle.getY() + rectangle.getHeight(),
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY() + rectangle.getHeight()));
        // Left side...
        p[2] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX(),
                        rectangle.getY(),
                        rectangle.getX(),
                        rectangle.getY() + rectangle.getHeight()));
        // Right side
        p[3] = getIntersectionPoint(line,
                        new Line2D.Double(
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY(),
                        rectangle.getX() + rectangle.getWidth(),
                        rectangle.getY() + rectangle.getHeight()));

        return p;

    }

    public Point2D getIntersectionPoint(Line2D lineA, Line2D lineB)
    {

        double x1 = lineA.getX1();
        double y1 = lineA.getY1();
        double x2 = lineA.getX2();
        double y2 = lineA.getY2();

        double x3 = lineB.getX1();
        double y3 = lineB.getY1();
        double x4 = lineB.getX2();
        double y4 = lineB.getY2();

        Point2D p = null;

        double d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (d != 0) {
            double xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
            double yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

            p = new Point2D.Double(xi, yi);

        }
        return p;
    }
    
    
    BufferedImage loadImg(String filename)
    {
        BufferedImage img = null;
        try
        {
            img = ImageIO.read(new File(filename));
        }
        catch(IOException e)
        {
                    
        }
        return img;
    }
}
