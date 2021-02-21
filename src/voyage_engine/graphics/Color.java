package voyage_engine.graphics;

import voyage_engine.util.Vec4;

public class Color {
    public static final Color WHITE = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    public static final Color BLACK = new Color(0.0f, 0.0f, 0.0f, 1.0f);
    public static final Color RED = new Color(10f, 0.0f, 0.0f, 1.0f);
    public static final Color GREEN = new Color(0.0f, 1.0f, 0.0f, 1.0f);
    public static final Color BLUE = new Color(0.0f, 0.0f, 1.0f, 1.0f);
    
    Vec4 color_as_vector;
    public int r, g, b, a;
    
    public Color (float r, float g, float b, float a) {
        color_as_vector = new Vec4(r, g, b, a);
    }

    public Color (int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
        color_as_vector = new Vec4(r / 255f, g / 255f, b / 255f, a / 255f) ;
    }

    public Vec4 getColorVector() {
        return color_as_vector;
    }
}
