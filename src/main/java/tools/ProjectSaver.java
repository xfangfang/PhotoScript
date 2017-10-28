package tools;

import customView.DragBox;
import customView.ImageBox;

import java.io.Serializable;

public class ProjectSaver implements Serializable {
    public double x;
    public double y;
    public double w;
    public double h;
    public double r, g, b, a;
    public SHAPE.TYPE type;
    public double fa;
    public double fr;
    public double fg;
    public double fb;
    public double rotate;
    public double strokeWidth;
    public String svgPath;
    public String text;
    public double fontSize;

    public void setSvgPath(String svgPath) {
        this.svgPath = svgPath;
    }


    public ProjectSaver(double x, double y, double w, double h, SHAPE.TYPE type, double a, double r, double g, double b,
                        double fa, double fr, double fg, double fb, double rotate, double strokeWidth) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.type = type;
        this.rotate = rotate;
        this.strokeWidth = strokeWidth;
        this.a = a;
        this.r = r;
        this.g = g;
        this.b = b;
        this.fa = fa;
        this.fr = fr;
        this.fg = fg;
        this.fb = fb;
    }

    public void setTextAndFont(String text, double fontSize) {
        this.text = text;
        this.fontSize = fontSize;
    }

    public String getText() {
        return text;
    }

    public double getFontSize() {
        return fontSize;
    }

    public DragBox construtDragBox() {
        return new DragBox(x, y, w, h, type, rotate, strokeWidth, a, r, g, b, fa, fr, fg, fb);
    }

    public ImageBox constructImage() {
        return new ImageBox(x, y, w, h, type, rotate, a, r, g, b, fa, fr, fg, fb);
    }

    public SHAPE.TYPE getType() {
        return type;
    }
}

// TODO: 2017/10/24 背景颜色没有保存 