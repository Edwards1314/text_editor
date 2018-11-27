package lab14;

public class FontAttributes {
    String FontFamily;
    String FontStyle;
    int FontSize;

    public String getFontFamily() {
        return FontFamily;
    }

    public void setFontFamily(String FontFamily) {
        this.FontFamily = FontFamily;
    }

    public String getFontStyle() {
        return FontStyle;
    }

    public void setFontStyle(String FontStyle) {
        this.FontStyle = FontStyle;
    }

    public int getFontSize() {
        return FontSize;
    }

    public void setFontSize(int FontSize) {
        this.FontSize = FontSize;
    }

    public FontAttributes(String FontFamily, String FontStyle, int FontSize) {
        this.FontFamily = FontFamily;
        this.FontStyle = FontStyle;
        this.FontSize = FontSize;
    }
}
