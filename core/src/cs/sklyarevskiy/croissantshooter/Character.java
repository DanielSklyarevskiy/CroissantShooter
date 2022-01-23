package cs.sklyarevskiy.croissantshooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

abstract class Character {
    //characteristics
    private float movementSpeed; //world units per second
    private int health;

    //position & dimension
    private Rectangle boundingbox;

    //laser information
    private float bulletWidth, bulletHeight;
    private float bulletMovementSpeed;
    private float timeBetweenShots;
    private float bulletTimer = 0;

    //graphics
    private Texture characterTexture, bulletTexture;

    public Character(float xCentre, float yCentre,
                     float width, float height,
                     float movementSpeed, int health,
                     float bulletWidth, float bulletHeight, float bulletMovementSpeed,
                     float timeBetweenShots,
                     Texture characterTexture, Texture bulletTexture) {
        this.boundingbox = new Rectangle(xCentre - width/2,yCentre - height/2,
                                        width,height);
        this.movementSpeed = movementSpeed;
        this.health = health;
        this.bulletWidth = bulletWidth;
        this.bulletHeight = bulletHeight;
        this.bulletMovementSpeed = bulletMovementSpeed;
        this.timeBetweenShots = timeBetweenShots;
        this.characterTexture = characterTexture;
        this.bulletTexture = bulletTexture;
    }

    public void update(float deltaTime) {
        bulletTimer += deltaTime;
    }

    public boolean intersects(Rectangle otherRectangle){
        return boundingbox.overlaps(otherRectangle);
    }

    public boolean canShoot(){ return bulletTimer - timeBetweenShots >= 0; }

    public abstract Bullet[] shootBullets();

    public void takeDamage() {this.health--;}

    public void translate(float xChange, float yChange){
        boundingbox.setPosition(boundingbox.x + xChange, boundingbox.y + yChange);
    }

    public void draw(Batch batch){
        batch.draw(characterTexture, boundingbox.x, boundingbox.y, boundingbox.width, boundingbox.height);
    }

    public float getMovementSpeed() { return movementSpeed; }
    public Rectangle getBoundingBox() { return boundingbox; }
    public float getBulletWidth() { return bulletWidth; }
    public float getBulletHeight() { return bulletHeight; }
    public float getBulletMovementSpeed() {return bulletMovementSpeed;}
    public int getHealth() {return health;}
    public Texture getBulletTexture() { return bulletTexture; }
    public void setBulletTimer(float bulletTimer) { this.bulletTimer = bulletTimer; }

}
