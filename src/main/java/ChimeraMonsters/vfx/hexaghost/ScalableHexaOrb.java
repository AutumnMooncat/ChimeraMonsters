package ChimeraMonsters.vfx.hexaghost;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.vfx.BobEffect;

public class ScalableHexaOrb {
    public static final String ID = "HexaghostOrb";
    private BobEffect effect = new BobEffect(2.0F);
    private float activateTimer;
    public boolean activated = false;
    public boolean hidden = false;
    public boolean playedSfx = false;
    private Color color;
    private float x;
    private float y;
    private float particleTimer = 0.0F;
    private static final float PARTICLE_INTERVAL = 0.06F;
    private float scale;

    public ScalableHexaOrb(float x, float y, int index, float scale) {
        this.x = x * Settings.scale * scale + MathUtils.random(-10.0F, 10.0F) * Settings.scale * scale;// 32
        this.y = y * Settings.scale * scale + MathUtils.random(-10.0F, 10.0F) * Settings.scale * scale;// 33
        this.activateTimer = (float)index * 0.3F;// 34
        this.color = Color.CHARTREUSE.cpy();// 35
        this.color.a = 0.0F;// 36
        this.hidden = true;// 37
        this.scale = scale;
    }// 38

    public void activate(float oX, float oY) {
        this.playedSfx = false;// 44
        this.activated = true;// 45
        this.hidden = false;// 46
    }// 47

    public void deactivate() {
        this.activated = false;// 53
    }// 54

    public void hide() {
        this.hidden = true;// 57
    }// 58

    public void update(float oX, float oY) {
        if (!this.hidden) {// 64
            if (this.activated) {// 65
                this.activateTimer -= Gdx.graphics.getDeltaTime();// 66
                if (this.activateTimer < 0.0F) {// 67
                    if (!this.playedSfx) {// 68
                        this.playedSfx = true;// 69
                        AbstractDungeon.effectsQueue.add(new ScalableIgniteEffect(this.x + oX, this.y + oY, scale));// 70
                        if (MathUtils.randomBoolean()) {// 71
                            CardCrawlGame.sound.play("GHOST_ORB_IGNITE_1", 0.3F);// 72
                        } else {
                            CardCrawlGame.sound.play("GHOST_ORB_IGNITE_2", 0.3F);// 74
                        }
                    }

                    this.color.a = MathHelper.fadeLerpSnap(this.color.a, 1.0F);// 77
                    this.effect.update();// 78
                    this.effect.update();// 79
                    this.particleTimer -= Gdx.graphics.getDeltaTime();// 80
                    if (this.particleTimer < 0.0F) {// 81
                        AbstractDungeon.effectList.add(new ScalableGhostlyFireEffect(this.x + oX + this.effect.y * 2.0F, this.y + oY + this.effect.y * 2.0F, scale));// 82
                        this.particleTimer = 0.06F;// 84
                    }
                }
            } else {
                this.effect.update();// 88
                this.particleTimer -= Gdx.graphics.getDeltaTime();// 89
                if (this.particleTimer < 0.0F) {// 90
                    AbstractDungeon.effectList.add(new ScalableGhostlyWeakFireEffect(this.x + oX + this.effect.y * 2.0F, this.y + oY + this.effect.y * 2.0F, scale));// 91
                    this.particleTimer = 0.06F;// 93
                }
            }
        } else {
            this.color.a = MathHelper.fadeLerpSnap(this.color.a, 0.0F);// 97
        }

    }// 99
}
