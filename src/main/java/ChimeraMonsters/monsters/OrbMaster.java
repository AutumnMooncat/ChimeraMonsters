package ChimeraMonsters.monsters;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.orbs.MonsterFrost;
import ChimeraMonsters.orbs.MonsterLightning;
import ChimeraMonsters.powers.OrbMasterPower;
import ChimeraMonsters.util.IntentManager;
import ChimeraMonsters.util.IntentPayload;
import ChimeraMonsters.util.MonsterOrbHelper;
import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.spine.AnimationState;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.animations.AnimateShakeAction;
import com.megacrit.cardcrawl.actions.animations.AnimateSlowAttackAction;
import com.megacrit.cardcrawl.actions.common.*;
import com.megacrit.cardcrawl.actions.utility.WaitAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.status.Burn;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;

import java.util.ArrayList;

public class OrbMaster extends AbstractEasyMonster {
    public static final String ID = ChimeraMonstersMod.makeID(OrbMaster.class.getSimpleName());
    private static final byte LASER = 1;
    private static final byte CLAW = 2;
    private static final byte INIT = 3;

    public OrbMaster(float offsetX, float offsetY) {
        super(ID, EnemyType.NORMAL, 90, -20.0F, -14.0F, 280.0F, 250.0F, null, offsetX, offsetY);
        this.loadAnimation("images/monsters/theForest/orbWalker/skeleton.atlas", "images/monsters/theForest/orbWalker/skeleton.json", 1.0F);
        AnimationState.TrackEntry e = this.state.setAnimation(0, "Idle", true);
        e.setTime(e.getEndTime() * MathUtils.random());
        
        setHp(scaleTougher(this, 90, 92), scaleTougher(this, 96, 102));
        DamageInfo laserDmg = new DamageInfo(this, scaleDeadlier(this, 10, 11));
        DamageInfo clawDmg = new DamageInfo(this, scaleDeadlier(this, 15, 16));
        linkDamage(LASER, laserDmg);
        linkDamage(CLAW, clawDmg);
        addMove(LASER, Intent.ATTACK_DEBUFF, laserDmg.base);
        addMove(CLAW, Intent.ATTACK, clawDmg.base);
        addMove(INIT, Intent.DEFEND_BUFF);
    }

    @Override
    public void usePreBattleAction() {
        addToBot(new ApplyPowerAction(this, this, new OrbMasterPower(this, scaleAbilities(this, 1, 2))));
    }

    @Override
    public void takeTurn() {
        switch (nextMove) {
            case LASER:
                addToBot(new ChangeStateAction(this, "ATTACK"));
                addToBot(new WaitAction(0.4F));
                addToBot(new DamageAction(AbstractDungeon.player, moveDamages.get(nextMove), AbstractGameAction.AttackEffect.FIRE));
                addToBot(new MakeTempCardInDiscardAndDeckAction(new Burn()));
                break;
            case CLAW:
                addToBot(new AnimateSlowAttackAction(this));
                addToBot(new DamageAction(AbstractDungeon.player, moveDamages.get(nextMove), AbstractGameAction.AttackEffect.SLASH_HEAVY));
                break;
            case INIT:
                addToBot(new GainBlockAction(this, this, scaleAbilities(this, 15, 25)));
                addToBot(new DoAction(() -> {
                    MonsterOrbHelper.increaseMaxOrbSlots(this, 2, true);
                    IntentPayload pl = new IntentPayload();
                    pl.owner = this;
                    pl.fields.move = MonsterOrbHelper.makeChannelMove(1);
                    pl.takeTurnFunc = thiz -> {
                        ArrayList<AbstractOrb> orbs = MonsterOrbHelper.getOrbs(this);
                        boolean zap = AbstractDungeon.monsterRng.randomBoolean();
                        if (orbs == null || orbs.stream().anyMatch(o -> o instanceof EmptyOrbSlot)) {
                            addToBot(new AnimateShakeAction(this, 1f, 0.1f));
                            addToBot(new MonsterOrbHelper.MonsterChannelAction(this, zap ? new MonsterLightning() : new MonsterFrost()));
                        } else {
                            addToBot(new MonsterOrbHelper.EvokeMonsterOrbAction(this, orbs.size()));
                        }
                        addToBot(new DoAction(() -> {
                            if (orbs == null || orbs.stream().anyMatch(o -> o instanceof EmptyOrbSlot)) {
                                thiz.fields.move = MonsterOrbHelper.makeChannelMove(1);
                            } else {
                                thiz.fields.move = MonsterOrbHelper.makeEvokeMove(orbs.size());
                            }
                        }));
                    };
                    IntentManager.addExtraIntent(this, pl);
                }));
                break;
        }

        addToBot(new RollMoveAction(this));
    }

    @Override
    protected void getMove(int i) {
        if (firstMove) {
            setMoveInfo(INIT);
            firstMove = false;
            return;
        }
        ArrayList<Byte> moves = new ArrayList<>();
        if (!lastTwoMoves(LASER)) {
            moves.add(LASER);
            moves.add(LASER);
            moves.add(LASER);
        }
        if (!lastTwoMoves(CLAW)) {
            moves.add(CLAW);
            moves.add(CLAW);
        }
        setMoveInfo(choiceFromRoll(i, moves));
    }

    @Override
    public void damage(DamageInfo info) {
        super.damage(info);
        if (info.owner != null && info.type != DamageInfo.DamageType.THORNS && info.output > 0) {
            state.setAnimation(0, "Hit", false);
            state.addAnimation(0, "Idle", true, 0.0F);
        }
    }

    @Override
    public void changeState(String stateName) {
        if (stateName.equals("ATTACK")) {
            state.setAnimation(0, "Attack", false);
            state.addAnimation(0, "Idle", true, 0.0F);
        }
    }
}
