package ua.jackshen.editor;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Line implements GraphicShape {
    private double posX1, posY1, posX2, posY2;
    private int strokeWidth;
    private Color color;

    Line(double posX1, double posY1, double posX2, double posY2, int strokeWidth, Color color) {
        this.posX1 = posX1;
        this.posY1 = posY1;
        this.posX2 = posX2;
        this.posY2 = posY2;
        this.strokeWidth = strokeWidth;
        this.color = color;
    }

    public void drawShape(GraphicsContext g) {
        g.setLineWidth(strokeWidth);
        g.setStroke(color);
        g.strokeLine(posX1, posY1, posX2, posY2);
    }
}