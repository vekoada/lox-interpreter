Implementing an interpreter for the Lox language in Java.

In doing this, I learned what a Visitor Pattern was and had to read it several times before understanding. The terminology seemed unnecessarily obfuscatory, so I decided to develop a simple alternative as explained below.

### The Execute On Pattern
The Visitor pattern is a common design pattern where each operation has its own class which implements a shared interface. Each entity that the operation will act upon is also represented as a class. To apply an operation to an entity, you typically call `Entity.accept(Operation)`, which then triggers `Operation.visitEntity(Entity)`. This design allows you to easily add new operations without modifying the entity classes, as each operation class contains the logic specific to each type of entity.

However, the terminology used in the Visitor pattern is misleading and unintuitive. The metaphor of a visitor, however cool, doesn't clearly express what's going on. To address this, I suggest that we refer to the pattern as the "Execute On" pattern, which is much clearer.

For example, consider an entity `Entity` with one operation `Operation`. Instead of using `Entity.accept(Operation)`, you would now use `Entity.execute(Operation)`, which internally calls `Operation.onEntity(Entity)`. This "Execute On" pattern makes the code easier to understand by clearly expressing that an operation is being executed on a specific entity.

This change leads to more readable, self-documenting code.

Below is an example of how this could look. First, we define the entities:
```Java
abstract class Shape {
    abstract void execute(Operation operation);
}

class Circle extends Entity {
    @Override
    void execute(Operation operation) {
        operation.onCircle(this);
    }
}

class Triangle extends Entity {
    @Override
    void execute(Operation operation) {
        operation.onTriangle(this);
    }
}
```

Next, we define the operations:
```Java
interface Operation {
    void onCircle(Circle circle);
    void onTriangle(Triangle triangle);
}

class CalculateArea implements Operation {
    @Override
    public void onCircle(Circle circle) {
        System.out.println("Executing CalculateArea on Circle");
    }

    @Override
    public void onTriangle(Triangle triangle) {
        System.out.println("Executing CalculateArea on Triangle");
    }
}

class CalculatePerimeter implements Operation {
    @Override
    public void onCircle(Circle circle) {
        System.out.println("Executing CalculatePerimeter on Circle");
    }

    @Override
    public void onTriangle(Triangle triangle) {
        System.out.println("Executing CalculatePerimeter on Triangle");
    }
}
```
Finally, here's an example of the usage:
```Java
public class Main {
    public static void main(String[] args) {
        Shape circle = new Circle();
        Shape triangle = new Triangle();

        Operation calculateArea = new CalculateArea();
        Operation calculatePerimeter = new CalculatePerimeter();

        circle.execute(calculateArea);
        triangle.execute(calculateArea);
        circle.execute(calculatePerimeter); 
        triangle.execute(calculatePerimeter); 
    }
}
```
