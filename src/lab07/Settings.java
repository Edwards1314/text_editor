package lab07;

public class Settings {
    double width;
    double height;
    String input;

    public Settings() {
    }

    public Settings(double width, double height, String input) {
        this.width = width;
        this.height = height;
        this.input = input;
    }
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    public String getInput() {
        return input;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setInput(String input) {
        this.input = input;
    }


}
