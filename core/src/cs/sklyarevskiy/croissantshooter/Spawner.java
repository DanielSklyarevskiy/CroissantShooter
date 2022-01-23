package cs.sklyarevskiy.croissantshooter;

import com.badlogic.gdx.Screen;

public abstract class Spawner implements Screen {
    private float timeBetweenEnemySpawns = 1.5f;
    private float enemySpawnTimer = 0;
    private int waveCount = 0;
    private GameScreen gameScreen = new GameScreen();

    @Override
    public void render(float deltaTime){
        System.out.println("hi");
        spawnEnemiesInWaves(deltaTime);
    }

    private void spawnEnemiesInWaves(float deltaTime){
        if(waveCount == 0){
            timeBetweenEnemySpawns = 3f;
            wave1(deltaTime);
            waveCount++;
        }
    }

    private void wave1(float deltaTime){
      /*  int enemiesInWave = 0;
        enemySpawnTimer = 0;
        while (enemiesInWave < 3){
            enemySpawnTimer += 0.00001;
            if(enemySpawnTimer > timeBetweenEnemySpawns) {
                gameScreen.getEnemyList().add(new Enemy(   gameScreen.getWORLD_WIDTH()-10,
                        (float) gameScreen.getWORLD_HEIGHT() - 30,
                        10, 10, 30, 5,
                        4f, 12f, 50, 0.8f,
                        gameScreen.getEnemyTexture(), gameScreen.getEnemyBulletTexture0()));
                System.out.println("hi");
                enemiesInWave++;
                enemySpawnTimer = 0;
            }
        }*/
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
