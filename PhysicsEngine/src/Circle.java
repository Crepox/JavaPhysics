class Circle {

    Vector position;
    Vector velocity = new Vector(0, 0);
    double radius;

    public Circle(Vector position, double radius) {
        this.position = position;
        this.radius = radius;
    }

    public Circle(double x, double y, double radius) {
        this.position = new Vector(x, y);
        this.radius = radius;
    }

    public Circle() {
        position = new Vector(0, 0);
        radius = 0.0;
    }
}