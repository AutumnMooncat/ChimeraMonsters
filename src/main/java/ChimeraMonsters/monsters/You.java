package ChimeraMonsters.monsters;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.CreatureRenderPatches;
import ChimeraMonsters.patches.RunicPatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateHopAction;
import com.megacrit.cardcrawl.actions.animations.AnimateShakeAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.animations.VFXAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.actions.common.RollMoveAction;
import com.megacrit.cardcrawl.actions.utility.SFXAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.VulnerablePower;
import com.megacrit.cardcrawl.powers.WeakPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.combat.ShockWaveEffect;

import java.util.ArrayList;

public class You extends AbstractEasyMonster {
    public static final String ID = ChimeraMonstersMod.makeID(You.class.getSimpleName());
    private static final byte ANGER = 1;
    private static final byte ENVY = 2;
    private static final byte SADNESS = 3;
    private static final byte PANIC = 4;

    // Ironclad defaults if no player exists
    public static float getHBX() {
        return AbstractDungeon.player != null ? AbstractDungeon.player.hb_x : -4f;
    }

    public static float getHBY() {
        return AbstractDungeon.player != null ? AbstractDungeon.player.hb_y : -16f;
    }

    public static float getHBW() {
        return AbstractDungeon.player != null ? AbstractDungeon.player.hb_w : 220f;
    }

    public static float getHBH() {
        return AbstractDungeon.player != null ? AbstractDungeon.player.hb_h : 290f;
    }

    public You() {
        super(ID, EnemyType.ELITE, 80, getHBX(), getHBY(), getHBW(), getHBH(), null);
        loadAnimation("images/characters/ironclad/idle/skeleton.atlas", "images/characters/ironclad/idle/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        stateData.setMix("Hit", "Idle", 0.1F);
        e.setTimeScale(0.6F);

        setHp(scaleTougher(this, 76, 80), scaleTougher(this, 80, 84));
        DamageInfo angerDmg = new DamageInfo(this, scaleDeadlier(this, 6, 8));
        DamageInfo envyDmg = new DamageInfo(this, scaleDeadlier(this, 8, 10));
        DamageInfo sadnessDmg = new DamageInfo(this, scaleDeadlier(this, 14, 16));
        linkDamage(ANGER, angerDmg);
        linkDamage(ENVY, envyDmg);
        linkDamage(SADNESS, sadnessDmg);
        addMove(monStrings.MOVES[0], ANGER, Intent.ATTACK, angerDmg.base, 2);
        addMove(monStrings.MOVES[1], ENVY, Intent.ATTACK_DEBUFF, envyDmg.base);
        addMove(monStrings.MOVES[2], SADNESS, Intent.ATTACK, sadnessDmg.base);
        addMove(monStrings.MOVES[3], PANIC, Intent.DEFEND_DEBUFF);
    }

    @Override
    public void takeTurn() {
        switch (nextMove) {
            case ANGER:
                addToBot(new AnimateSlowAttackAction(this));
                addToBot(new DamageAction(AbstractDungeon.player, moveDamages.get(nextMove), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                addToBot(new DamageAction(AbstractDungeon.player, moveDamages.get(nextMove), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                break;
            case ENVY:
                addToBot(new AnimateSlowAttackAction(this));
                addToBot(new DamageAction(AbstractDungeon.player, moveDamages.get(nextMove), AbstractGameAction.AttackEffect.BLUNT_LIGHT));
                addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new WeakPower(AbstractDungeon.player, 2, true)));
                break;
            case SADNESS:
                addToBot(new AnimateShakeAction(this, 1f, 0.25f));
                addToBot(new SFXAction("ATTACK_PIERCING_WAIL"));
                addToBot(new VFXAction(this, new ShockWaveEffect(hb.cX, hb.cY, Settings.BLUE_TEXT_COLOR, ShockWaveEffect.ShockWaveType.CHAOTIC), 0.3F));
                addToBot(new DamageAction(AbstractDungeon.player, moveDamages.get(nextMove), AbstractGameAction.AttackEffect.BLUNT_HEAVY));
                break;
            case PANIC:
                addToBot(new AnimateHopAction(this));
                addToBot(new GainBlockAction(this, scaleTougher(this, 16, scaleAbilities(this, 22, 28))));
                addToBot(new ApplyPowerAction(AbstractDungeon.player, this, new VulnerablePower(AbstractDungeon.player, 2, true)));
                break;
        }
        addToBot(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {
        ArrayList<Byte> moves = new ArrayList<>();
        if (!lastMove(ANGER) && !lastMoveBefore(ANGER)) {
            moves.add(ANGER);
        }
        if (!lastMove(ENVY) && !lastMoveBefore(ENVY)) {
            moves.add(ENVY);
        }
        if (lastMove(PANIC)) {
            moves.clear();
            moves.add(SADNESS);
        }
        if (!lastMove(PANIC) && !lastMoveBefore(PANIC)) {
            moves.add(PANIC);
        }
        setMoveInfo(choiceFromRoll(i, moves));
    }

    @Override
    public void render(SpriteBatch sb) {
        if (!isDead && !escaped) {
            TextureRegion tex = CreatureRenderPatches.referenceTex(AbstractDungeon.player);
            float[] transform = CreatureRenderPatches.referenceTransform(AbstractDungeon.player);
            if (tex != null) {
                sb.setColor(tint.color);
                sb.draw(tex,
                        drawX + animX - tex.getRegionWidth()/2f,
                        drawY + animY + transform[4] + AbstractDungeon.sceneOffsetY - tex.getRegionHeight()/2f,
                        tex.getRegionWidth()/2f, tex.getRegionHeight()/2f,
                        tex.getRegionWidth(), tex.getRegionHeight(),
                        flipHorizontal ? 1 : -1, flipVertical ? -1 : 1, 0
                );
            }

            if (!this.isDying && !this.isEscaping && AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT && !AbstractDungeon.player.isDead && !AbstractDungeon.player.hasRelic("Runic Dome") && !RunicPatch.hideIntent(this) && intent != Intent.NONE && !Settings.hideCombatElements) {
                renderIntentVfxBehind(sb);
                renderIntent(sb);
                renderIntentVfxAfter(sb);
                renderDamageRange(sb);
            }

            hb.render(sb);
            intentHb.render(sb);
            healthHb.render(sb);
        }

        if (!AbstractDungeon.player.isDead) {
            renderHealth(sb);
            renderName(sb);
        }
    }
}
