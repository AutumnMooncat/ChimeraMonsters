package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.actions.TimedVFXAction;
import ChimeraMonsters.patches.CreatureRenderPatches;
import ChimeraMonsters.powers.interfaces.MonsterCantDiePower;
import ChimeraMonsters.powers.interfaces.RenderModifierPower;
import ChimeraMonsters.util.ShaderCompiler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.vfx.combat.SmokeBombEffect;

public class MimicPower extends AbstractInternalLogicPower implements RenderModifierPower, MonsterCantDiePower {
    public static final String POWER_ID = ChimeraMonstersMod.makeID(MimicPower.class.getSimpleName());
    private final AbstractMonster disguise;
    private final int origHP;
    private final int origMaxHP;
    private boolean broken;

    public MimicPower(AbstractCreature owner, AbstractMonster disguise, float hpMult) {
        super(POWER_ID, owner, -1);
        this.disguise = disguise;
        this.origHP = owner.currentHealth;
        this.origMaxHP = owner.maxHealth;
        owner.currentHealth = (int) Math.max(1, owner.currentHealth * hpMult);
        owner.maxHealth = (int) Math.max(1, owner.maxHealth * hpMult);
        owner.healthBarUpdatedEvent();
    }

    @Override
    public void onRender(SpriteBatch sb, TextureRegion tex) {
        if (broken) {
            render(sb, tex);
        } else {
            disguise.drawX = Settings.WIDTH/2f;
            disguise.drawY = Settings.HEIGHT/2f;
            disguise.animX = 0;
            disguise.animY = -CreatureRenderPatches.transformState()[4];
            disguise.render(sb);
        }
    }

    @Override
    public boolean cantDie(AbstractMonster monsterOwner) {
        return !broken;
    }

    @Override
    public void onPreventDeath(AbstractMonster monsterOwner) {
        addToBot(new TimedVFXAction(new SmokeBombEffect(owner.hb.cX, owner.hb.cY)));
        addToBot(new DoAction(() -> {
            owner.halfDead = false;
            monsterOwner.currentHealth = origHP;
            monsterOwner.maxHealth = origMaxHP;
            monsterOwner.healthBarUpdatedEvent();
            broken = true;
        }));
    }
}
