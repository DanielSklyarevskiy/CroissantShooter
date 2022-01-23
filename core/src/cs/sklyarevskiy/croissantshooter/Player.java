package cs.sklyarevskiy.croissantshooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;
/**
 * This is the player class from which the GameScreen takes algorithms related to the player
 */

public class Player extends Character{

    private float respawnTime;
    private float respawnTimer;
    private boolean isInvincible;
    private Texture invincibleTexture;
    private Texture focusTexture;
    private Rectangle hitbox;

    private static int example;


    public Player(float xCentre, float yCentre,
                  float width, float height,
                  float movementSpeed, int health,
                  float bulletWidth, float bulletHeight,
                  float bulletMovementSpeed, float timeBetweenShots,
                  Texture characterTexture, Texture bulletTexture,
                  Texture invincibleTexture, Texture focusTexture, float respawnTime) {
        super(xCentre, yCentre, width, height, movementSpeed, health, bulletWidth, bulletHeight, bulletMovementSpeed, timeBetweenShots, characterTexture, bulletTexture);
        this.invincibleTexture = invincibleTexture;
        this.focusTexture = focusTexture;
        this.respawnTime = respawnTime;
        hitbox = new Rectangle(xCentre - 1.8f , yCentre - 4, 3, 3);
    }

    @Override
    public Bullet[] shootBullets(){
        Bullet[] bullets = new Bullet[2];
        bullets[0] = new Bullet(getBoundingBox().x + getBoundingBox().width * 0.07f, getBoundingBox().y + getBoundingBox().height - 5,
                                getBulletWidth(),getBulletHeight(),
                                getMovementSpeed(),getBulletTexture());
        bullets[1] = new Bullet(getBoundingBox().x + getBoundingBox().width * 0.93f, getBoundingBox().y +getBoundingBox().height - 5,
                getBulletWidth(),getBulletHeight(),
                getMovementSpeed(),getBulletTexture());

        setBulletTimer(0);

        return bullets;
    }

    public boolean intersects(Rectangle otherRectangle){
        return hitbox.overlaps(otherRectangle);
    }

    @Override
    public void translate(float xChange, float yChange){
        getBoundingBox().setPosition(getBoundingBox().x + xChange, getBoundingBox().y + yChange);
        hitbox.setPosition(hitbox.x + xChange, hitbox.y + yChange);
    }

    public void respawn(float deltaTime){
        respawnTimer += deltaTime;
        if (respawnTimer > respawnTime){
            isInvincible = false;
            respawnTimer = 0;
        }
    }

    public void drawFocus(Batch batch){
        batch.draw(focusTexture, hitbox.x, hitbox.y, hitbox.width, hitbox.height);
    }
    public void drawInvincible(Batch batch){
        batch.draw(invincibleTexture, getBoundingBox().x, getBoundingBox().y, getBoundingBox().width, getBoundingBox().height);
    }

    public boolean getIsInvincible() { return isInvincible; }
    public void setIsInvincible(boolean isInvincible) { this.isInvincible = isInvincible; }

    public static void setExample(int example) { Player.example = example; }
}
