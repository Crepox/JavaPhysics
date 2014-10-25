class Wall {

    Vector A, B;

    public Wall(Vector A, Vector B) {
        this.A = A;
        this.B = B;
    }

    public Wall(double x1, double y1, double x2, double y2) {
        A = new Vector(x1, y1);
        B = new Vector(x2, y2);
    }
}