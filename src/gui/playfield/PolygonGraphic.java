package gui.playfield;

import core.Coord;
import movement.map.MapNode;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

public class PolygonGraphic extends PlayFieldGraphic {
    private final List<Coord> coords;

    private final Color PATH_COLOR = Color.ORANGE;
    private final Color BG_COLOR = Color.WHITE;

    public PolygonGraphic(Coord... coords) {
        this.coords = Arrays.asList(coords);
    }

    public PolygonGraphic(List<Coord> coords) {
        this.coords = coords;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(PATH_COLOR);
        g2.setBackground(BG_COLOR);

        for(int i = 0; i < coords.size()-1; i++) {
            g2.drawLine(scale(coords.get(i).getX()), scale(coords.get(i).getY()),
                    scale(coords.get(i+1).getX()), scale(coords.get(i+1).getY()));
        }
    }
}
