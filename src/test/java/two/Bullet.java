package two;

import org.util.annotations.SendField;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet extends JLabel {
    @SendField
    int x;

    @SendField
    int y;

    @SendField
    int w;

    @SendField
    int h;

    int f = -6;

    public Bullet(int x, int y) {
        super(getNewIcon());
        setBounds(this.x = x, this.y = y, w = 50, h = 50);
    }

    public void Update() {
        setBounds(x, y = (y + f), w, h);
    }

    private static ImageIcon getNewIcon() {
        int w = 50, h = 500, p = 2, d = 10;
        BufferedImage im = new BufferedImage(w, h, 2);
        for (int i = 0; i < im.getWidth(); i++) for (int j = 0; j < im.getHeight(); j++) im.setRGB(i, j, 0);

        Graphics2D gg = im.createGraphics();

        gg.setColor(Color.RED);
        gg.fillOval(d, d, w - 2 * d, h - d);
        gg.setColor(Color.YELLOW);
        gg.fillPolygon(new int[]{w / 3, w / 2, 2 * w / 3}, new int[]{h, h / 2, h}, 3);

        return new ImageIcon(im);
    }

    public boolean isARetard(int m) {
        return y < 0 || y > m;
    }

    public void setY(int i) {
        y = i;
    }

    public void setF(int i) {
        f = 6;
    }
}
