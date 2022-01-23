package cs.sklyarevskiy.croissantshooter;

import com.badlogic.gdx.graphics.Texture;

public class Boss extends Character{
    public Boss(float xCentre, float yCentre,
                 float width, float height,
                 float movementSpeed, int health,
                 float bulletWidth, float bulletHeight,
                 float bulletMovementSpeed, float timeBetweenShots,
                 Texture characterTexture,
                 Texture bulletTexture) {
        super(xCentre, yCentre, width, height, movementSpeed, health, bulletWidth, bulletHeight, bulletMovementSpeed, timeBetweenShots, characterTexture, bulletTexture);
    }

    @Override
    public Bullet[] shootBullets() {
        return new Bullet[0];
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
}
