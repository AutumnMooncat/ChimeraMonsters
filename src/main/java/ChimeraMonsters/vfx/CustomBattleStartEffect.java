package ChimeraMonsters.vfx;

import ChimeraMonsters.patches.StartOfCombatPatches;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.ScreenShake;
import com.megacrit.cardcrawl.localization.UIStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.UpgradeShineParticleEffect;


public class CustomBattleStartEffect extends AbstractGameEffect {
    private static final UIStrings uiStrings;
    public static final String[] TEXT;
    private static final float EFFECT_DUR = 7.0F;
    private static final float HEIGHT_DIV_2;
    private static final float WIDTH_DIV_2;
    private boolean surpriseAttack;
    private boolean soundPlayed = false;
    private boolean bossFight = false;
    private Color bgColor;
    private static final float TARGET_HEIGHT;
    private static final float BG_RECT_EXPAND_SPEED = 3.0F;
    private float currentHeight = 0.0F;
    private String battleStartMessage;
    private static final String BATTLE_START_MSG;
    public static final String PLAYER_TURN_MSG;
    public static final String ENEMY_TURN_MSG;
    public static final String TURN_TXT;
    private String turnMsg;
    private static final float TEXT_FADE_SPEED = 5.0F;
    private static final float MAIN_MSG_OFFSET_Y;
    private static final float TURN_MSG_OFFSET_Y;
    private Color turnMessageColor = new Color(0.7F, 0.7F, 0.7F, 0.0F);
    private float timer1 = 1.0F;
    private float timer2 = 1.0F;
    private static final float MSG_VANISH_X;
    private float firstMessageX;
    private float secondMessageX;
    private boolean showHb;
    private static TextureAtlas.AtlasRegion img;
    private static final float SWORD_ANIM_TIME = 0.5F;
    private float swordTimer;
    private static final float SWORD_START_X;
    private static final float SWORD_DEST_X;
    private float swordX;
    private float swordY;
    private float swordAngle;
    private boolean swordSound1;
    private Color swordColor;

    public CustomBattleStartEffect(boolean surpriseAttack) {
        this.firstMessageX = (float) Settings.WIDTH / 2.0F;
        this.secondMessageX = (float)Settings.WIDTH * 1.5F;
        this.showHb = false;
        this.swordTimer = SWORD_ANIM_TIME;
        this.swordSound1 = false;
        this.swordColor = new Color(0.9F, 0.9F, 0.85F, 0.0F);
        this.duration = EFFECT_DUR;
        this.startingDuration = EFFECT_DUR;
        this.surpriseAttack = surpriseAttack;
        this.bgColor = new Color(AbstractDungeon.fadeColor.r / 2.0F, AbstractDungeon.fadeColor.g / 2.0F, AbstractDungeon.fadeColor.b / 2.0F, 0.0F);
        if (img == null) {
            img = ImageMaster.vfxAtlas.findRegion("combat/battleStartSword");
        }

        this.scale = Settings.scale;
        this.swordY = (float)Settings.HEIGHT / 2.0F - (float)img.packedHeight / 2.0F + 20.0F * Settings.scale;
        if (surpriseAttack) {
            this.turnMsg = ENEMY_TURN_MSG;
        } else {
            this.turnMsg = PLAYER_TURN_MSG;
        }

        this.color = Settings.GOLD_COLOR.cpy();
        this.color.a = 0.0F;
        if (Settings.usesOrdinal) {
            this.battleStartMessage = Integer.toString(GameActionManager.turn) + getOrdinalNaming(GameActionManager.turn) + TURN_TXT;
        } else if (Settings.language == Settings.GameLanguage.VIE) {
            this.battleStartMessage = TURN_TXT + " " + Integer.toString(GameActionManager.turn);
        } else {
            this.battleStartMessage = Integer.toString(GameActionManager.turn) + TURN_TXT;
        }

        if (AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss) {
            this.bossFight = true;
            CardCrawlGame.sound.play("BATTLE_START_BOSS");
        } else if (MathUtils.randomBoolean()) {
            CardCrawlGame.sound.play("BATTLE_START_1");
        } else {
            CardCrawlGame.sound.play("BATTLE_START_2");
        }

    }

    public static String getOrdinalNaming(int i) {
        return i % 100 != 11 && i % 100 != 12 && i % 100 != 13 ? (new String[]{"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th"})[i % 10] : "th";
    }

    public void update() {
        if (!this.showHb) {
            AbstractDungeon.player.showHealthBar();

            for(AbstractMonster m : AbstractDungeon.getMonsters().monsters) {
                m.showHealthBar();
            }

            this.showHb = true;
        }

        if (this.duration > 6.0F) {
            this.currentHeight = MathUtils.lerp(this.currentHeight, TARGET_HEIGHT, Gdx.graphics.getDeltaTime() * BG_RECT_EXPAND_SPEED);
        } else if (this.duration < 3.5F) {
            this.currentHeight = MathUtils.lerp(this.currentHeight, 0.0F, Gdx.graphics.getDeltaTime() * BG_RECT_EXPAND_SPEED * 2);
        }

        if (this.duration < 3.0F && this.timer1 != 0.0F) {
            this.timer1 -= Gdx.graphics.getDeltaTime();
            if (this.timer1 < 0.0F) {
                this.timer1 = 0.0F;
            }

            this.firstMessageX = Interpolation.pow2In.apply(this.firstMessageX, MSG_VANISH_X, 1.0F - this.timer1);
        } else if (this.duration < 3.0F && this.timer2 != 0.0F) {
            if (!this.soundPlayed) {
                CardCrawlGame.sound.play("TURN_EFFECT");
                AbstractDungeon.getMonsters().showIntent();
                this.soundPlayed = true;
            }

            this.timer2 -= Gdx.graphics.getDeltaTime();
            if (this.timer2 < 0.0F) {
                this.timer2 = 0.0F;
            }

            this.secondMessageX = Interpolation.pow2In.apply(this.secondMessageX, WIDTH_DIV_2, 1.0F - this.timer2);
        }

        if (this.duration > 2.0F) {
            this.color.a = MathUtils.lerp(this.color.a, 1.0F, Gdx.graphics.getDeltaTime() * TEXT_FADE_SPEED);
        } else {
            this.color.a = MathUtils.lerp(this.color.a, 0.0F, Gdx.graphics.getDeltaTime() * TEXT_FADE_SPEED);
        }

        this.bgColor.a = this.color.a * 0.75F;
        this.turnMessageColor.a = this.color.a;
        if (Settings.FAST_MODE) {
            this.duration -= Gdx.graphics.getDeltaTime();
        }

        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }

        this.updateSwords();
    }

    private void updateSwords() {
        this.swordTimer -= Gdx.graphics.getDeltaTime();
        if (this.swordTimer < 0.0F) {
            this.swordTimer = 0.0F;
        }

        this.swordColor.a = Interpolation.fade.apply(1.0F, 0.01F, this.swordTimer / 0.5F);
        if (this.bossFight) {
            if (this.swordTimer < 0.1F && !this.swordSound1) {
                this.swordSound1 = true;
                CardCrawlGame.screenShake.shake(ScreenShake.ShakeIntensity.MED, ScreenShake.ShakeDur.SHORT, false);

                for(int i = 0; i < 30; ++i) {
                    if (MathUtils.randomBoolean()) {
                        AbstractDungeon.effectsQueue.add(new UpgradeShineParticleEffect((float)Settings.WIDTH / 2.0F + MathUtils.random(-150.0F, 150.0F) * Settings.scale, (float)Settings.HEIGHT / 2.0F + MathUtils.random(-10.0F, 50.0F) * Settings.scale));
                    } else {
                        AbstractDungeon.topLevelEffectsQueue.add(new UpgradeShineParticleEffect((float)Settings.WIDTH / 2.0F + MathUtils.random(-150.0F, 150.0F) * Settings.scale, (float)Settings.HEIGHT / 2.0F + MathUtils.random(-10.0F, 50.0F) * Settings.scale));
                    }
                }
            }

            this.swordX = Interpolation.pow3Out.apply(SWORD_DEST_X, SWORD_START_X, this.swordTimer / 0.5F);
            this.swordAngle = Interpolation.pow3Out.apply(-50.0F, 500.0F, this.swordTimer / 0.5F);
        } else {
            this.swordX = SWORD_DEST_X;
            this.swordAngle = -50.0F;
        }

    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.bgColor);
        sb.draw(ImageMaster.WHITE_SQUARE_IMG, 0.0F, HEIGHT_DIV_2 - this.currentHeight / 2.0F, (float)Settings.WIDTH, this.currentHeight);
        this.renderSwords(sb);
        FontHelper.renderFontCentered(sb, FontHelper.bannerNameFont, StartOfCombatPatches.getBattleStartText(), this.firstMessageX, HEIGHT_DIV_2 + MAIN_MSG_OFFSET_Y, this.color, 1.0F);
        FontHelper.renderFontCentered(sb, FontHelper.bannerNameFont, this.turnMsg, this.secondMessageX, HEIGHT_DIV_2 + MAIN_MSG_OFFSET_Y, this.color, 1.0F);
        if (!this.surpriseAttack) {
            FontHelper.renderFontCentered(sb, FontHelper.turnNumFont, this.battleStartMessage, this.secondMessageX, HEIGHT_DIV_2 + TURN_MSG_OFFSET_Y, this.turnMessageColor);
        }

    }

    public void dispose() {
    }

    private void renderSwords(SpriteBatch sb) {
        sb.setColor(this.swordColor);
        sb.draw(img, (float)Settings.WIDTH - this.swordX - (float)img.packedWidth / 2.0F + this.firstMessageX - (float)Settings.WIDTH / 2.0F, this.swordY, (float)img.packedWidth / 2.0F, (float)img.packedHeight / 2.0F, (float)img.packedWidth, (float)img.packedHeight, -this.scale, -this.scale, -this.swordAngle + 180.0F);
        sb.draw(img, this.swordX - (float)img.packedWidth / 2.0F + this.firstMessageX - (float)Settings.WIDTH / 2.0F, this.swordY, (float)img.packedWidth / 2.0F, (float)img.packedHeight / 2.0F, (float)img.packedWidth, (float)img.packedHeight, this.scale, this.scale, this.swordAngle);
    }

    static {
        uiStrings = CardCrawlGame.languagePack.getUIString("BattleStartEffect");
        TEXT = uiStrings.TEXT;
        HEIGHT_DIV_2 = (float)Settings.HEIGHT / 2.0F;
        WIDTH_DIV_2 = (float)Settings.WIDTH / 2.0F;
        TARGET_HEIGHT = 150.0F * Settings.scale;
        BATTLE_START_MSG = TEXT[0];
        PLAYER_TURN_MSG = TEXT[1];
        ENEMY_TURN_MSG = TEXT[2];
        TURN_TXT = TEXT[3];
        MAIN_MSG_OFFSET_Y = 20.0F * Settings.scale;
        TURN_MSG_OFFSET_Y = -30.0F * Settings.scale;
        MSG_VANISH_X = (float)(-Settings.WIDTH) * 0.25F;
        img = null;
        SWORD_START_X = -50.0F * Settings.scale;
        SWORD_DEST_X = (float)Settings.WIDTH / 2.0F + 0.0F * Settings.scale;
    }
}
