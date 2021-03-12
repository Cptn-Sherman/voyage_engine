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

    public Color (int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Vec4 getColorVector() {
        return color_as_vector;
    }

    // !this code could fail given big numbers.
    public static  Color[] generateUniqueArray(int number) {
        // generate lists of unique colors
        Color[] colors = new Color[number];
        int spacing = (256 * 3) / number - 1;
        colors[0] = new Color(spacing, 0, 0);

        int r, g, b;
        for (int n = 1; n < number; n++) {
            r = colors[n - 1].r + spacing;
            g = (r > 255) ? colors[n - 1].g + spacing : colors[n - 1].g;
            b = (g > 255) ? colors[n - 1].b + spacing : colors[n - 1].b;
            r %= 256;
            g %= 256;
            b %= 256;
            colors[n] = new Color(r, g, b, 255);
        }
        return colors;
    }
}
