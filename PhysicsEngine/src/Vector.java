/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Joe
 */
public class Vector {
    
    double x, y;

    Vector() {
        x = 0;
        y = 0;
    }

    Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    Vector(Vector v) {
        this.x = v.x;
        this.y = v.y;
    }

    public String toString() {
        return "Vector[x = " + x + ", y = " + y + "]";
    }

    public static Vector add(Vector a, Vector b) {
        return new Vector(a.x + b.x, a.y + b.y);
    }

    public static Vector sub(Vector a, Vector b) {
        return new Vector(a.x - b.x, a.y - b.y);
    }

    public static Vector scale(Vector a, double b) {
        return new Vector(a.x * b, a.y * b);
    }

    public static Vector addScaled(Vector a, Vector b, double c) {
        return add(a, scale(b, c));
    }

    public static double magSqr(Vector a) {
        return a.x * a.x + a.y * a.y;
    }

    public static double dot(Vector a, Vector b) {
        return a.x * b.x + a.y * b.y;
    }

    public static double mag(Vector a) {
        return Math.sqrt(magSqr(a));
    }

    public static Vector normal(Vector a) {
        return new Vector(scale(a, 1 / mag(a)));
    }
}