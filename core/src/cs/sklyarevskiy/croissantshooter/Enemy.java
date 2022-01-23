package cs.sklyarevskiy.croissantshooter;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

public class Enemy extends Character{
    private Vector2 directionVector;
    private float timeSinceLastDirectionChange = 0;
    private float directionChangeFrequency = 0.75f;

    private int moveType;
    private int moveDir;
    private int shootType;

    public Enemy(float xCentre, float yCentre,
                  float width, float height,
                  float movementSpeed, int health,
                  float bulletWidth, float bulletHeight,
                  float bulletMovementSpeed, float timeBetweenShots,
                  Texture characterTexture,
                  Texture bulletTexture,
                  int moveType,int moveDir, int shootType) {
        super(xCentre, yCentre, width, height, movementSpeed, health, bulletWidth, bulletHeight, bulletMovementSpeed, timeBetweenShots, characterTexture, bulletTexture);

        directionVector = new Vector2(0,-1);
        this.moveType = moveType;
        this.moveDir = moveDir;
        this.shootType = shootType;
    }

    public Vector2 getDirectionVector(){
        return directionVector;
    }

    private void randomizeDirectionVector(){
        double bearing = CroissantShooterGame.getRandom().nextDouble() * 6.23185; //0 to 2* pi
        directionVector.x = (float)Math.sin(bearing);
        directionVector.y = (float)Math.cos(bearing);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        timeSinceLastDirectionChange += deltaTime;
        if (timeSinceLastDirectionChange > directionChangeFrequency){
            randomizeDirectionVector();
            timeSinceLastDirectionChange -= directionChangeFrequency;
        }
    }

    @Override
    public Bullet[] shootBullets(){
        Bullet[] bullets = new Bullet[1];
        bullets[0] = new Bullet(getBoundingBox().x + getBoundingBox().width,getBoundingBox().y - getBulletHeight(),
                getBulletWidth(),getBulletHeight(),
                getBulletMovementSpeed(),getBulletTexture());
        setBulletTimer(0);

        return bullets;
    }
    public Bullet[] shoot8Bullets(){
        Bullet[] bullets = new Bullet[8];
        for(int i = 0; i < bullets.length; i++){
            bullets[i] = new Bullet(getBoundingBox().x + getBoundingBox().width * 0.07f, getBoundingBox().y + getBoundingBox().height - 10,
                    getBulletWidth(),getBulletHeight(),
                    getMovementSpeed(),getBulletTexture());
        }
        setBulletTimer(0);

        return bullets;
    }
    public Bullet[] shoot9Bullets(){
        Bullet[] bullets = new Bullet[9];
        for(int i = 0; i < bullets.length; i++){
            bullets[i] = new Bullet(getBoundingBox().x + getBoundingBox().width * 0.07f, getBoundingBox().y + getBoundingBox().height - 10,
                    getBulletWidth(),getBulletHeight(),
                    getMovementSpeed(),getBulletTexture());
        }
        setBulletTimer(0);

        return bullets;
    }

    public int getMoveType() {
        return moveType;
    }

    public int getMoveDir() {
        return moveDir;
    }

    public int getShootType() {
        return shootType;
    }
}
