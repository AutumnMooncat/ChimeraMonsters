package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import basemod.helpers.VfxBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.LoseHPAction;
import com.megacrit.cardcrawl.actions.common.ReducePowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;


public class HoverPower extends AbstractEasyPower implements RenderModifierPower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(HoverPower.class.getSimpleName());
    private static final PowerStrings powerStrings = CardCrawlGame.languagePack.getPowerStrings(POWER_ID);
    public static final String NAME = powerStrings.NAME;
    public static final String[] DESCRIPTIONS = powerStrings.DESCRIPTIONS;
    private float timePassed;

    public static final Texture KITE = new Texture(ChimeraMonstersMod.getModID() + "Resources/images/modifiers/HoveringKite.png");
    public static final TextureRegion kite_region = new TextureRegion(KITE);

    public HoverPower(AbstractCreature owner, int amount) {
        super(POWER_ID, NAME, PowerType.BUFF, false, owner, amount);
        timePassed = 0f;
        priority=-20;

    }

    @Override
    public void updateDescription() {
        this.description = DESCRIPTIONS[0] + amount + DESCRIPTIONS[1];
    }

    @Override
    public float atDamageFinalReceive(float damage, DamageInfo.DamageType type) {
        return calculateDamageTakenAmount(damage, type);
    }

    private float calculateDamageTakenAmount(float damage, DamageInfo.DamageType type) {
        if (type != DamageInfo.DamageType.HP_LOSS && type != DamageInfo.DamageType.THORNS)
            return damage / 2.0F;
        return damage;
    }

    public int onAttacked(DamageInfo info, int damageAmount) {
        boolean willLive = (calculateDamageTakenAmount(damageAmount, info.type) < this.owner.currentHealth);
        if (info.owner != null && info.type != DamageInfo.DamageType.HP_LOSS && info.type != DamageInfo.DamageType.THORNS && damageAmount > 0 && willLive) {
            flash();
            addToBot(new ReducePowerAction(this.owner, this.owner, this.ID, 1));
        }
        return damageAmount;
    }

    public void onRemove() {
        addToBot(new LoseHPAction(owner, owner, (int) (0.25f * this.owner.maxHealth)));
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
//        if (timePassed == 0f) {
//            System.out.println("HELLO KITE");
//            spawnKite(owner);
//        }
        timePassed += Gdx.graphics.getDeltaTime();
        Color origColor = sb.getColor();
        sb.setColor(Color.WHITE);
        render(sb, tex, (float) (50 * Math.sin(timePassed)), 150f + (float) (50 * Math.sin(timePassed)), 1f, 15);
        render(sb, kite_region, owner.hb_x+owner.drawX+150f+ (float)(50 * Math.sin(timePassed)), owner.hb_y+owner.drawY+250 + (float) (50 * Math.sin(timePassed)), 1f, 15);
        render(sb, kite_region, 0, 0);
        render(sb, kite_region, Settings.WIDTH/2, Settings.HEIGHT/2);
//        render(sb, kite_region, 0, 0);
//        render(sb, kite_region, 0, 0);
//        render(sb, kite_region, 0, 0);
//        render(sb, kite_region, 0, 0);
//        render(sb, kite_region, 0, 0);


        sb.setColor(origColor);
    }

//    public static void spawnKite(AbstractCreature c) {
//        if (c.hasPower(HoverPower.POWER_ID)) {
//            AbstractDungeon.actionManager.addToBottom(new VFXAction(kiteEffect(c)));
//        }
//    }

//    public static AbstractGameEffect kiteEffect(AbstractCreature c) {
//        float startX = c.hb.cX + 50.0f;
//        float startY = c.hb.cY + 150.0f;
//        return new VfxBuilder(KITE, startX, startY, 5f)
//                .emitEvery(
//                        (x, y) -> new VfxBuilder(KITE, c.hb.cX, c.hb.cY, 0.01f)
//                                .build(), 0.01f)
////                .moveX(startX, aboveHeadX)
////                .moveY(startY, aboveHeadY)
////                .rotate(5.0f)
//                .whenComplete(vfxBuilder -> spawnKite(c))
//                .build();
//    }
}
