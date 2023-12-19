package gui.playfield;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ImageGraphic extends PlayFieldGraphic {
    private final double scaleFactor;
    private final BufferedImage image;

    public ImageGraphic(String path, double scaleFactor) throws IOException {
        this.image = ImageIO.read(Objects.requireNonNull(getClass().getResource(path)));
        this.scaleFactor = scaleFactor;
    }

    @Override
    public void draw(Graphics2D g2) {
        AffineTransform at = new AffineTransform();
        at.scale(scale * scaleFactor, scale * scaleFactor);
        g2.drawImage(image, new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR), 0, 0);
    }
}
