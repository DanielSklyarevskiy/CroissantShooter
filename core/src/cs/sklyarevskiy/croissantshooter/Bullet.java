package cs.sklyarevskiy.croissantshooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bullet {
    //position and dimensions
    private Rectangle rectangle;
    private double direction;

    //bullet physical characteristics
    private float movementSpeed; //world units per second

    //graphics
    private Texture texture;

    public Bullet(float xCentre, float yBottom, float width, float height, float movementSpeed, Texture texture) {
        this.rectangle = new Rectangle(xCentre - width/2,yBottom, width, height);
        this.movementSpeed = movementSpeed;
        this.texture = texture;
    }

    public void aimTowardsAngle(double angle){
        direction = Math.toRadians(angle);
    }
    public void aimTowardsPlayer(Player player){
        double dx = rectangle.x - player.getBoundingBox().x;
        double dy = rectangle.y - player.getBoundingBox().y;
        direction = Math.atan2(dy, dx);
    }
    public void moveForward(float deltaTime){
        rectangle.x += -movementSpeed * Math.cos(direction) * deltaTime;
        rectangle.y += -movementSpeed * Math.sin(direction) * deltaTime;
    }


    public void draw(Batch batch) {
        batch.draw(texture, rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public Rectangle getBoundingBox(){ return rectangle;}
    public void setDirection(double direction) {
        this.direction = direction;
    }

    public String toString(){
        return "Bullet width: " + rectangle.width + "\nBullet height: " + rectangle.height;
    }
}
