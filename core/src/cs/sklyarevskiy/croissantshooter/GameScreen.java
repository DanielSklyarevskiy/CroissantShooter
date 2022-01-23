package cs.sklyarevskiy.croissantshooter;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import  com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;

/**
 * This is the main class where interaction between the various gameObjects happens
 */
public class GameScreen implements Screen{
    //screen
    private Camera camera;
    private Viewport viewport;

    //graphics
    private SpriteBatch batch;
    private Texture[] backgrounds;
    private Texture playerTexture, invincibleTexture, focusTexture;
    private Texture enemyTexture, miniBossTexture, bossTexture;
    private Texture playerBulletTexture;
    private Texture enemyBulletTexture0, enemyBulletTexture1, enemyBulletTexture2;
    private Texture explosionTexture;
    private Texture lore1, lore2, lore3, loreGO;

    //timing
    private float[] backgroundOffsets = {0,0,0,0,0};
    private float backgroundMaxScrollSpeed;
    private float timeBetweenEnemySpawns = 3f;
    private float enemySpawnTimer = 0;
    private int totalEnemies = 0;

    //booleans
    private boolean playerIsShooting;
    private boolean playerIsFocused;
    private boolean bossDead;

    //world parameters
    private final float WORLD_WIDTH = 72;
    private final float WORLD_HEIGHT = 128;

    //game objects
    private Player player;
    private Boss boss;
    private LinkedList<Enemy> enemyList;
    private LinkedList<Bullet> playerBulletList;
    private LinkedList<Bullet> enemyBulletList;
    private LinkedList<Explosion> explosionList;
    private Enemy loreBlock;

    private int score = 0;
    int loreCount = 0;

    //HUD
    BitmapFont letterFont;
    BitmapFont numberFont;
    float hudVerticalMargin, hudLeftX, hudRightX, hudCentreX, hudRow1Y, hudRow2Y, hudSectionWidth;

    GameScreen() {
        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        //setting up the background
        backgrounds = new Texture[5];
        backgrounds[0] = new Texture("backgroundLayer1.png");
        backgrounds[1] = new Texture("backgroundLayer2.png");
        backgrounds[2] = new Texture("backgroundLayer3.png");
        backgrounds[3] = new Texture("backgroundLayer4.png");
        backgrounds[4] = new Texture("backgroundLayer5.png");
        backgroundMaxScrollSpeed = (float) WORLD_HEIGHT/ 4;

        //initialize textures
        playerTexture = new Texture("Player.png");
        invincibleTexture = new Texture("Player_hurt.png");
        focusTexture = new Texture("Focus.png");
        enemyTexture = new Texture("Dastardly_dosa.png");
        miniBossTexture = new Texture("Dastardly_dosa_miniboss.png");
        bossTexture = new Texture("Bad_baguette.png");
        playerBulletTexture = new Texture("Bullet.png");
        enemyBulletTexture0 = new Texture("Enemy_bullet_0.png");
        enemyBulletTexture1 = new Texture("Enemy_bullet_1.png");
        enemyBulletTexture2 = new Texture("Enemy_bullet_2.png");
        explosionTexture = new Texture("explosion.png");
        lore1 = new Texture("Lore1.png");
        lore2 = new Texture("Lore2.png");
        lore3 = new Texture("Lore3.png");
        loreGO = new Texture("LoreGO.png");

        //set up game objects
        player = new Player((float)WORLD_WIDTH/2,(float)WORLD_HEIGHT/4,
                                        10, 10, 48, 15,
                                        3f,3f,70,0.3f,
                                        playerTexture, playerBulletTexture, invincibleTexture, focusTexture, 2f);
        enemyList = new LinkedList<>();
        playerBulletList = new LinkedList<>();
        enemyBulletList = new LinkedList<>();
        explosionList = new LinkedList<>();

        batch = new SpriteBatch();

        prepareHUD();
    }

    private void prepareHUD(){
        //Create a BitmapFont from font file
        FreeTypeFontGenerator letterFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Big Space DEMO.ttf"));
        FreeTypeFontGenerator numberFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Space Angel.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();

        fontParameter.size = 72;
        fontParameter.borderWidth = 3.6f;
        fontParameter.color = new Color(1,1,1,0.3f);
        fontParameter.borderColor = new Color(0,0,0,0.3f);

        letterFont = letterFontGenerator.generateFont(fontParameter);
        numberFont = numberFontGenerator.generateFont(fontParameter);

        //Scale the font to fit world
        letterFont.getData().setScale(0.1f);
        numberFont.getData().setScale(0.1f);

        //Calculate hud margins, etc.
        hudVerticalMargin = letterFont.getCapHeight() / 2;
        hudLeftX = hudVerticalMargin;
        hudRightX = WORLD_WIDTH * 2 / 3 - hudLeftX;
        hudCentreX = WORLD_WIDTH / 3 ;
        hudRow1Y = WORLD_HEIGHT - hudVerticalMargin;
        hudRow2Y = hudRow1Y - hudVerticalMargin - letterFont.getCapHeight();
        hudSectionWidth = WORLD_WIDTH / 3;

    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        //scrolling background
        renderBackground(deltaTime);

        detectInput(deltaTime);
        player.update(deltaTime);
        if(player.getIsInvincible()){
            player.respawn(deltaTime);
        }

        makeLoreBox();

        //determine movement of enemies
        ListIterator<Enemy> enemyListIterator = enemyList.listIterator();
        while (enemyListIterator.hasNext()){
            Enemy enemy = enemyListIterator.next();
            if(enemy.getMoveType() == 1){
                moveEnemyType1(enemy, deltaTime, enemy.getMoveDir());
            }
            else if(enemy.getMoveType() == 2){
                moveEnemyType2(enemy, deltaTime);
            }
            else if(enemy.getMoveType() == 3){
                moveEnemyType3(enemy, deltaTime, enemy.getMoveDir());
            }
            else if(enemy.getMoveType() == 0){
                moveEnemyRandom(enemy, deltaTime);
            }
            if(boss != null){
                moveBoss(boss, deltaTime);
            }
            enemy.update(deltaTime);
            enemy.draw(batch);
        }

        //player
        if(!player.getIsInvincible()) player.draw(batch);
        else                          player.drawInvincible(batch);
        if(playerIsFocused)           player.drawFocus(batch);

        //lore
        if(loreBlock != null) loreBlock.draw(batch);

        //boss
        if(boss != null && !bossDead) boss.draw(batch);

        //bullets
        renderBullets(deltaTime);

        //detect collisions between bullets and characters
        detectCollisions(deltaTime);

        //explosions
        renderExplosions(deltaTime);

        //HUD
        updateAndRenderHUD();


        //Waves
        spawnEnemiesInWaves(deltaTime);

        batch.end();
    }

    private void makeLoreBox(){
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            loreCount++;
        }
        if(loreCount == 0){
            loreBlock = new Enemy(WORLD_WIDTH/2, WORLD_HEIGHT/2, 50,50,0,1,0,0,0,0,
                    lore1,lore1,-1,-1,-1);
        }
        if(loreCount == 1){
            loreBlock = new Enemy(WORLD_WIDTH/2, WORLD_HEIGHT/2, 50,50,0,1,0,0,0,0,
                    lore2,lore1,-1,-1,-1);
        }
        if(loreCount == 2){
            loreBlock = null;
        }
        if(loreCount >= 3 && bossDead){
            loreBlock = new Enemy(WORLD_WIDTH/2, WORLD_HEIGHT/2, 50,50,0,1,0,0,0,0,
                    lore3,lore1,-1,-1,-1);
        }
    }

    private void updateAndRenderHUD(){
        //render first row
        letterFont.draw(batch, "Score:", hudCentreX, hudRow1Y, hudSectionWidth, Align.center, false);
        //render second row
        numberFont.draw(batch, String.format(Locale.getDefault(), "%05d", score), hudCentreX, hudRow2Y, hudSectionWidth, Align.center, false);
    }

    private void spawnEnemiesInWaves(float deltaTime){
        if(loreBlock == null){
            if(totalEnemies < 3){
                timeBetweenEnemySpawns = 1.2f;
                wave1(deltaTime);
            }
            else if(totalEnemies < 6){
                timeBetweenEnemySpawns = 1.2f;
                wave2(deltaTime);
            }
            else if(totalEnemies < 10){
                timeBetweenEnemySpawns = 2f;
                wave3(deltaTime);
            }
            else if(totalEnemies < 16){
                timeBetweenEnemySpawns = 1.5f;
                wave4(deltaTime);
            }
            else if(totalEnemies < 18){
                timeBetweenEnemySpawns = 1.5f;
                wave5(deltaTime);
            }
            else if(totalEnemies < 22){
                timeBetweenEnemySpawns = 2f;
                wave6(deltaTime);
            }
            else if(totalEnemies < 26){
                timeBetweenEnemySpawns = 3f;
                wave7(deltaTime);
            }
            else if(totalEnemies < 32){
                timeBetweenEnemySpawns = 1.5f;
                wave8(deltaTime);
            }
            else if(totalEnemies < 38){
                timeBetweenEnemySpawns = 1.5f;
                wave9(deltaTime);
            }
            else if(totalEnemies < 39){
                timeBetweenEnemySpawns = 10f;
                miniBoss(deltaTime);
            }
            else if(totalEnemies < 40){
                timeBetweenEnemySpawns = 20f;
                bossFight(deltaTime);
            }
        }
    }

    private void wave1(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyList.add(new Enemy(   WORLD_WIDTH + 10,
                    (float) WORLD_HEIGHT - 30,
                    10, 10, 15, 4,
                    4f, 12f, 75, 0.8f,
                    enemyTexture, enemyBulletTexture0, 3, -1, 1));
            totalEnemies++;
            enemySpawnTimer = 0;
        }
    }
    private void wave2(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyList.add(new Enemy(     -10,
                    (float) WORLD_HEIGHT - 50,
                    10, 10, 15, 4,
                    4f, 12f, 45, 0.8f,
                    enemyTexture, enemyBulletTexture0, 3, 1, 1));
            totalEnemies++;
            enemySpawnTimer = 0;
        }
    }
    private void wave3(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyList.add(new Enemy(     WORLD_WIDTH / 5,
                    (float) WORLD_HEIGHT + 10,
                    10, 10, 30, 4,
                    4f, 12f, 45, 1.1f,
                    enemyTexture, enemyBulletTexture0, 2, 0, 1));
            enemyList.add(new Enemy(     WORLD_WIDTH * 4 / 5,
                    (float) WORLD_HEIGHT + 10,
                    10, 10, 30, 4,
                    4f, 12f, 45, 1.1f,
                    enemyTexture, enemyBulletTexture0, 2, 0, 1));
            totalEnemies += 2;
            enemySpawnTimer = 0;
        }
    }
    private void wave4(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyList.add(new Enemy(     -10,
                    (float) WORLD_HEIGHT - 50,
                    10, 10, 15, 2,
                    4f, 12f, 45, 0.8f,
                    enemyTexture, enemyBulletTexture0, 1, 1, 1));
            enemyList.add(new Enemy(   WORLD_WIDTH + 10,
                    (float) WORLD_HEIGHT - 30,
                    10, 10, 15, 1,
                    4f, 12f, 75, 0.8f,
                    enemyTexture, enemyBulletTexture0, 1, -1, 1));
            totalEnemies += 2;
            enemySpawnTimer = 0;
        }
    }
    private void wave5(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns){
            enemyList.add(new Enemy(     WORLD_WIDTH / 2,
                    (float) WORLD_HEIGHT + 10,
                    10, 10, 20, 10,
                    4f, 12f, 45, 1.5f,
                    enemyTexture, enemyBulletTexture1, 2, 0, 2));
            totalEnemies ++;
            enemySpawnTimer = 0;
        }
    }
    private void wave6(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns){
            enemyList.add(new Enemy(     WORLD_WIDTH / 4,
                    (float) WORLD_HEIGHT + 10,
                    10, 10, 20, 5,
                    4f, 12f, 45, 1.2f,
                    enemyTexture, enemyBulletTexture1, 2, 0, 2));
            enemyList.add(new Enemy(     WORLD_WIDTH * 3 / 4,
                    (float) WORLD_HEIGHT + 10,
                    10, 10, 20, 5,
                    4f, 12f, 45, 1.2f,
                    enemyTexture, enemyBulletTexture1, 2, 0, 2));
            totalEnemies += 2;
            enemySpawnTimer = 0;
        }
    }
    private void wave7(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyList.add(new Enemy(   WORLD_WIDTH + 10,
                    (float) WORLD_HEIGHT - 30,
                    10, 10, 15, 1,
                    4f, 12f, 60, 1.2f,
                    enemyTexture, enemyBulletTexture1, 3, -1, 2));
            enemyList.add(new Enemy(   -10,
                    (float) WORLD_HEIGHT - 30,
                    10, 10, 15, 1,
                    4f, 12f, 60, 1.2f,
                    enemyTexture, enemyBulletTexture1, 3, 1, 2));
            totalEnemies += 2;
            enemySpawnTimer = 0;
        }
    }
    private void wave8(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyList.add(new Enemy(     -10,
                    (float) WORLD_HEIGHT - 50,
                    10, 10, 15, 4,
                    4f, 12f, 45, 0.8f,
                    enemyTexture, enemyBulletTexture1, 1, 1, 2));
            enemyList.add(new Enemy(   WORLD_WIDTH + 10,
                    (float) WORLD_HEIGHT - 30,
                    10, 10, 15, 2,
                    4f, 12f, 75, 0.8f,
                    enemyTexture, enemyBulletTexture0, 1, -1, 1));
            totalEnemies += 2;
            enemySpawnTimer = 0;
        }
    }
    private void wave9(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyList.add(new Enemy(     -10,
                    (float) WORLD_HEIGHT - 50,
                    10, 10, 15, 4,
                    4f, 12f, 45, 0.8f,
                    enemyTexture, enemyBulletTexture0, 1, 1, 1));
            enemyList.add(new Enemy(   WORLD_WIDTH + 10,
                    (float) WORLD_HEIGHT - 30,
                    10, 10, 15, 2,
                    4f, 12f, 75, 0.8f,
                    enemyTexture, enemyBulletTexture1, 1, -1, 2));
            totalEnemies += 2;
            enemySpawnTimer = 0;
        }
    }
    private void miniBoss(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns) {
            enemyList.add(new Enemy(WORLD_WIDTH / 2,
                    (float) WORLD_HEIGHT * 3 / 4,
                    10, 10, 45, 25,
                    4f, 12f, 45, 0.8f,
                    miniBossTexture, enemyBulletTexture2, 0, 1, 3));
            totalEnemies++;
            enemySpawnTimer = 0;
        }
    }
    private void bossFight(float deltaTime){
        enemySpawnTimer += deltaTime;
        if(enemySpawnTimer > timeBetweenEnemySpawns) {
            boss = new Boss(WORLD_WIDTH / 2,
                    WORLD_HEIGHT + 35,
                    75, 30, 20, 30,
                    4f, 12f, 45, 1.6f,
                    bossTexture, enemyBulletTexture2);
            totalEnemies++;
            enemySpawnTimer = 0;
        }
    }

    private void detectInput(float deltaTime){
        //keyboard input
        float moveSpeed;
        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -player.getBoundingBox().x;
        downLimit = -player.getBoundingBox().y;
        rightLimit = WORLD_WIDTH - player.getBoundingBox().x - player.getBoundingBox().width;
        upLimit = WORLD_HEIGHT - player.getBoundingBox().y - player.getBoundingBox().height;
        //shooting and focus mode
        if(Gdx.input.isKeyPressed(Input.Keys.Z) && player.canShoot()){
            playerIsShooting = true;
        }
        else{playerIsShooting = false;}
        if(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            playerIsFocused = true;
            moveSpeed = player.getMovementSpeed()/2;
        }
        else {
            playerIsFocused = false;
            moveSpeed = player.getMovementSpeed();
        }
        //movement
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) && rightLimit > 0){
            player.translate(Math.min(moveSpeed * deltaTime, rightLimit), 0f);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.UP) && upLimit > 0){
            player.translate(0f, Math.min(moveSpeed * deltaTime, upLimit));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) && leftLimit < 0){
            player.translate(Math.max(-moveSpeed * deltaTime, leftLimit), 0f);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && downLimit < 0){
            player.translate(0f, Math.max(-moveSpeed * deltaTime, downLimit));
        }
    }

    private void moveEnemyRandom(Enemy enemy, float deltaTime){
        float leftLimit, rightLimit, upLimit, downLimit;
        leftLimit = -enemy.getBoundingBox().x;
        downLimit = (float)WORLD_HEIGHT/2 - enemy.getBoundingBox().y;
        rightLimit = WORLD_WIDTH - enemy.getBoundingBox().x - enemy.getBoundingBox().width;
        upLimit = WORLD_HEIGHT - enemy.getBoundingBox().y - enemy.getBoundingBox().height;

        float xMove = enemy.getDirectionVector().x * enemy.getMovementSpeed() * deltaTime;
        float yMove = enemy.getDirectionVector().y * enemy.getMovementSpeed() * deltaTime;

        if (xMove > 0) xMove = Math.min(xMove, rightLimit);
        else xMove = Math.max(xMove, leftLimit);
        if (yMove > 0) yMove = Math.min(yMove, upLimit);
        else yMove = Math.max(yMove, downLimit);

        enemy.translate(xMove,yMove);
    }
    private void moveEnemyType1(Enemy enemy, float deltaTime, int direction){
        if (enemy.getBoundingBox().x < WORLD_WIDTH / 2 - enemy.getBoundingBox().width - 10||
                enemy.getBoundingBox().x > WORLD_WIDTH / 2 + enemy.getBoundingBox().width){
            enemy.translate(direction * enemy.getMovementSpeed() * 2 * deltaTime, 0);
        }
        else {
            enemy.translate(direction * enemy.getMovementSpeed() * deltaTime, 0);
        }
    }
    private void moveEnemyType2(Enemy enemy, float deltaTime){
        if (enemy.getBoundingBox().y > WORLD_HEIGHT - 30 - enemy.getBoundingBox().height ||
                enemy.getBoundingBox().y < WORLD_HEIGHT / 2){
            enemy.translate(0, -enemy.getMovementSpeed() * deltaTime);
        }
        else {
            enemy.translate(0, -enemy.getMovementSpeed() / 5 * deltaTime);
        }
    }
    private void moveEnemyType3(Enemy enemy, float deltaTime, int direction){
        if(direction == 1){
            if (enemy.getBoundingBox().x < 10){
                enemy.translate(enemy.getMovementSpeed() * deltaTime, 0);
            }
            else{
                enemy.translate(enemy.getMovementSpeed() * 2 * deltaTime, 0);
            }
        }
        else{
            if (enemy.getBoundingBox().x > WORLD_WIDTH - 10){
                enemy.translate(-enemy.getMovementSpeed() * deltaTime, 0);
            }
            else{
                enemy.translate(-enemy.getMovementSpeed() * 2 * deltaTime, 0);
            }
        }
    }
    private void moveBoss(Boss boss, float deltaTime){
        if (boss.getBoundingBox().y > WORLD_HEIGHT - 10 - boss.getBoundingBox().height){
            boss.translate(0, -boss.getMovementSpeed() * deltaTime);
        }
    }

    private void renderBackground(float deltaTime){
        backgroundOffsets[0] += deltaTime * backgroundMaxScrollSpeed / 8;
        backgroundOffsets[1] += deltaTime * backgroundMaxScrollSpeed / 6;
        backgroundOffsets[2] += deltaTime * backgroundMaxScrollSpeed / 4;
        backgroundOffsets[3] += deltaTime * backgroundMaxScrollSpeed / 2;
        backgroundOffsets[4] += deltaTime * backgroundMaxScrollSpeed;

        for (int layer = 0; layer < backgroundOffsets.length; layer++){
            if(backgroundOffsets[layer] > WORLD_HEIGHT){
                backgroundOffsets[layer] = 0;
            }
            batch.draw(backgrounds[layer],0,-backgroundOffsets[layer],
                    WORLD_WIDTH,WORLD_HEIGHT);
            batch.draw(backgrounds[layer],0,-backgroundOffsets[layer] + WORLD_HEIGHT,
                    WORLD_WIDTH,WORLD_HEIGHT);
        }
    }

    private void renderBullets(float deltaTime){
        //player adding bullets
        if(playerIsShooting){
            Bullet[] bullets = player.shootBullets();
            for (Bullet bullet: bullets) {
                System.out.println(bullet);
                playerBulletList.add(bullet);
                score --;
            }
        }
        //enemy adding bullets
        ListIterator<Enemy> enemyListIterator = enemyList.listIterator();
        while (enemyListIterator.hasNext()){
            Enemy enemy = enemyListIterator.next();
            if(enemy.canShoot()){
                int angle = 0;
                Bullet[] bullets = enemy.shootBullets();
                if(enemy.getShootType() == 1){
                    bullets = enemy.shootBullets();
                }
                if(enemy.getShootType() == 2){
                    bullets = enemy.shoot8Bullets();
                }
                if(enemy.getShootType() == 3){
                    bullets = enemy.shoot9Bullets();
                }
                for (Bullet bullet: bullets) {
                    enemyBulletList.add(bullet);
                    if(enemy.getShootType() == 1){
                        bullet.aimTowardsPlayer(player);
                    }
                    else if(enemy.getShootType() == 2){
                        bullet.aimTowardsAngle(angle);
                        angle += 45;
                    }
                    else if(enemy.getShootType() == 3){
                        bullet.aimTowardsAngle(angle);
                        angle += 45;
                        if(angle >= 360){
                            bullet.aimTowardsPlayer(player);
                        }
                    }
                }
            }
        }

        //player shooting bullets
        ListIterator<Bullet> iterator = playerBulletList.listIterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.draw(batch);
            bullet.getBoundingBox().y += player.getBulletMovementSpeed()*deltaTime;
            if(bullet.getBoundingBox().y > WORLD_HEIGHT){
                iterator.remove();
            }
        }
        //enemy shooting bullets
        iterator = enemyBulletList.listIterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.draw(batch);
            bullet.moveForward(deltaTime);
            if(bullet.getBoundingBox().y + bullet.getBoundingBox().height < 0 ||
               bullet.getBoundingBox().x + bullet.getBoundingBox().width < 0 ||
               bullet.getBoundingBox().y > WORLD_HEIGHT ||
               bullet.getBoundingBox().x > WORLD_WIDTH){
                iterator.remove();
            }
        }

    }

    private void detectCollisions(float deltaTime){
        //for each player bullet, check if it intersects an enemy
        ListIterator<Bullet> bulletListIterator = playerBulletList.listIterator();
        while (bulletListIterator.hasNext()) {
            Bullet bullet = bulletListIterator.next();
            ListIterator<Enemy> enemyListIterator = enemyList.listIterator();
            while (enemyListIterator.hasNext()) {
                Enemy enemy = enemyListIterator.next();
                if (enemy.intersects(bullet.getBoundingBox())) {
                    //contact with enemy
                    enemy.takeDamage();
                    if(enemy.getHealth() <= 0){
                        enemyListIterator.remove();
                        score += 100;
                    }
                    explosionList.add(new Explosion(explosionTexture,
                                      new Rectangle(enemy.getBoundingBox()),
                                     0.7f));
                    bulletListIterator.remove();
                    break;
                }
                if (boss != null && boss.intersects(bullet.getBoundingBox())) {
                    //contact with boss
                    if(!bossDead){
                        boss.takeDamage();
                        if(boss.getHealth() <= 0){
                            bossDead = true;
                            loreCount++;
                            score += 10000;
                        }
                        explosionList.add(new Explosion(explosionTexture,
                                new Rectangle(boss.getBoundingBox()),
                                0.7f));
                        bulletListIterator.remove();
                        break;
                    }
                }
            }
        }
        //for each enemy laser, check if it intersects a player ship
        bulletListIterator = enemyBulletList.listIterator();
        while (bulletListIterator.hasNext()) {
            Bullet bullet = bulletListIterator.next();
            if(player.intersects(bullet.getBoundingBox())){
                //contact with player
                bulletListIterator.remove();
                if(!player.getIsInvincible()){
                    player.setIsInvincible(true);
                    score -= 100;
                    explosionList.add(new Explosion(explosionTexture,
                            new Rectangle(player.getBoundingBox()),
                            1.4f));
                }
            }
        }
    }

    private void renderExplosions(float deltaTime){
        ListIterator<Explosion> explosionListIterator = explosionList.listIterator();
        while (explosionListIterator.hasNext()){
            Explosion explosion = explosionListIterator.next();
            explosion.update(deltaTime);
            if(explosion.isFinished()){
                explosionListIterator.remove();
            }
            else {
                explosion.draw(batch);
            }
        }
    }

    //had no idea where to put this, so here is a function that determines maximum values in an array
    public static int maxInt(int[] arr){
        int max = 0;
        for(int i: arr){
            if(i > max){
                max = i;
            }
        }
        return max;
    }
    public static void main(String[] args) {
        //altering a method from another class
        Player.setExample(3);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void dispose() {

    }
}
