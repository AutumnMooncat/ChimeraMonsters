package ChimeraMonsters.util;

import ChimeraMonsters.actions.DoAction;
import ChimeraMonsters.orbs.MonsterDark;
import ChimeraMonsters.orbs.MonsterLightning;
import ChimeraMonsters.patches.CustomIntentPatches;
import ChimeraMonsters.patches.EnemyMoveInfoFields;
import ChimeraMonsters.patches.MonsterFields;
import ChimeraMonsters.patches.MoveManipulationPatches;
import basemod.ReflectionHacks;
import com.badlogic.gdx.math.MathUtils;
import com.evacipated.cardcrawl.modthespire.lib.*;
import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.MathHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;
import com.megacrit.cardcrawl.orbs.AbstractOrb;
import com.megacrit.cardcrawl.orbs.EmptyOrbSlot;
import com.megacrit.cardcrawl.orbs.Plasma;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.powers.FocusPower;
import com.megacrit.cardcrawl.vfx.ThoughtBubble;

import java.util.ArrayList;
import java.util.Collections;

public class MonsterOrbHelper {
    public static ArrayList<AbstractOrb> getOrbs(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return null;
        return MonsterFields.orbs.get(owner);
    }

    public static int getMaxOrbs(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return 0;
        return MonsterFields.maxOrbs.get(owner);
    }

    public static AbstractMonster findOwner(AbstractOrb orb) {
        return MonsterOrbField.monsterOwner.get(orb);
    }

    public static EnemyMoveInfo makeEvokeMove(int amount) {
        EnemyMoveInfo move = new EnemyMoveInfo((byte) -1, CustomIntentPatches.CHIMERA_MONSTERS_EVOKE, -1, 0, false);
        EnemyMoveInfoFields.evokes.set(move, amount);
        return move;
    }

    public static EnemyMoveInfo makeChannelMove(int amount) {
        EnemyMoveInfo move = new EnemyMoveInfo((byte) -1, AbstractMonster.Intent.MAGIC, -1, 0, false);
        EnemyMoveInfoFields.channels.set(move, amount);
        return move;
    }

    public static int intendedEvokes(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return 0;
        ArrayList<AbstractOrb> orbs = MonsterFields.orbs.get(owner);
        int evokes = 0;
        if (orbs != null) {
            evokes += EnemyMoveInfoFields.evokes.get(MoveManipulationPatches.getMove(owner));
            for (IntentPayload pl : IntentManager.extraIntents((AbstractMonster) owner)) {
                evokes += EnemyMoveInfoFields.evokes.get(pl.fields.move);
            }
            evokes = Math.min(evokes, orbs.size());
        }
        return evokes;
    }

    public static int intendedOrbDamage(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return 0;
        int dmg = 0;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        if (orbs != null) {
            int evokes = intendedEvokes(owner);

            for (int i = 0 ; i < evokes ; i++) {
                if (orbs.get(i) instanceof MonsterLightning || orbs.get(i) instanceof MonsterDark) {
                    dmg += AbstractOrb.applyLockOn(AbstractDungeon.player, orbs.get(i).evokeAmount);
                }
            }

            for (int i = evokes ; i < orbs.size() ; i++) {
                if (orbs.get(i) instanceof MonsterLightning) {
                    dmg += AbstractOrb.applyLockOn(AbstractDungeon.player, orbs.get(i).passiveAmount);
                }
            }
        }
        return dmg;
    }

    public static void applyFocus(AbstractCreature owner, AbstractOrb orb) {
        if (!(owner instanceof AbstractMonster)) return;
        AbstractPower power = owner.getPower(FocusPower.POWER_ID);
        if (power != null && !orb.ID.equals(Plasma.ORB_ID)) {
            orb.passiveAmount = Math.max(0, getBasePassive(orb) + power.amount);
            orb.evokeAmount = Math.max(0, getBaseEvoke(orb) + power.amount);
        } else {
            orb.passiveAmount = getBasePassive(orb);
            orb.evokeAmount = getBaseEvoke(orb);
        }
    }

    public static int getBasePassive(AbstractOrb orb) {
        return ReflectionHacks.getPrivate(orb, AbstractOrb.class, "basePassiveAmount");
    }

    public static int getBaseEvoke(AbstractOrb orb) {
        return ReflectionHacks.getPrivate(orb, AbstractOrb.class, "baseEvokeAmount");
    }

    public static void setSlot(AbstractCreature owner, AbstractOrb orb, int slotNum, int maxOrbs) {
        if (!(owner instanceof AbstractMonster)) return;
        MonsterOrbField.monsterOwner.set(orb, (AbstractMonster) owner);
        float dist = 160.0F * Settings.scale + (float)maxOrbs * 10.0F * Settings.scale;
        float angle = 100.0F + (float)maxOrbs * 12.0F;
        float offsetAngle = angle / 2.0F;
        angle *= (maxOrbs - 1 - slotNum) / (maxOrbs - 1f);
        angle += 90.0F - offsetAngle;
        orb.tX = dist * MathUtils.cosDeg(angle) + owner.drawX;
        orb.tY = dist * MathUtils.sinDeg(angle) + owner.drawY + owner.hb_h / 2.0F;
        if (maxOrbs == 1) {
            orb.tX = owner.drawX;
            orb.tY = 160.0F * Settings.scale + owner.drawY + owner.hb_h / 2.0F;
        }

        orb.hb.move(orb.tX, orb.tY);
    }

    public static ArrayList<AbstractOrb> initSlots(AbstractCreature owner, int amount) {
        if (!(owner instanceof AbstractMonster)) return null;
        ArrayList<AbstractOrb> orbs = new ArrayList<>();
        MonsterFields.orbs.set(owner, orbs);
        for (int i = 0; i < amount; i++) {
            orbs.add(new EmptyOrbSlot());
            setSlot(owner, orbs.get(i), i, amount);
        }
        return orbs;
    }

    public static void channelOrb(AbstractCreature owner, AbstractOrb orb) {
        if (!(owner instanceof AbstractMonster)) return;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        int max = getMaxOrbs(owner);
        if (orbs == null) {
            orbs = initSlots(owner, max);
        }

        int index = -1;
        int count;
        for (count = 0; count < orbs.size(); ++count) {
            if (orbs.get(count) instanceof EmptyOrbSlot) {
                index = count;
                break;
            }
        }

        if (index != -1) {
            orb.cX = orbs.get(index).cX;
            orb.cY = orbs.get(index).cY;
            orbs.set(index, orb);
            setSlot(owner, orb, index, max);
            orb.playChannelSFX();
            MonsterOrbField.justChanneled.set(orb, true);

            for (AbstractPower p : owner.powers) {
                p.onChannel(orb);
            }
            applyFocus(owner, orb);
        } else {
            AbstractDungeon.actionManager.addToTop(new MonsterChannelAction(owner, orb));
            AbstractDungeon.actionManager.addToTop(new EvokeMonsterOrbAction(owner, 1));
            AbstractDungeon.actionManager.addToTop(new AnimateMonsterOrbAction(owner, 1));
        }
    }

    public static void triggerEvokeAnimation(AbstractCreature owner, int slot) {
        if (!(owner instanceof AbstractMonster)) return;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        if (orbs != null && getMaxOrbs(owner) > 0) {
            orbs.get(slot).triggerEvokeAnimation();
        }
    }

    public static void evokeOrb(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        int maxOrbs = getMaxOrbs(owner);
        if (orbs != null && !orbs.isEmpty()) {
            AbstractOrb orb = orbs.get(0);
            if (!(orb instanceof EmptyOrbSlot)) {
                orb.onEvoke();
                AbstractOrb orbSlot = new EmptyOrbSlot();

                int i;
                for(i = 1; i < orbs.size(); ++i) {
                    Collections.swap(orbs, i, i - 1);
                }

                orbs.set(orbs.size() - 1, orbSlot);

                for(i = 0; i < orbs.size(); ++i) {
                    setSlot(owner, orbs.get(i), i, maxOrbs);
                }
            }
        }
    }

    public static void evokeNewestOrb(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        if (orbs != null && !orbs.isEmpty()) {
            AbstractOrb orb = orbs.get(orbs.size() - 1);
            if (!(orb instanceof EmptyOrbSlot)) {
                orb.onEvoke();
            }
        }
    }

    public static void evokeWithoutLosingOrb(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        if (orbs != null && !orbs.isEmpty()) {
            AbstractOrb orb = orbs.get(0);
            if (!(orb instanceof EmptyOrbSlot)) {
                orb.onEvoke();
            }
        }
    }

    public static void removeNextOrb(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        int maxOrbs = getMaxOrbs(owner);
        if (orbs != null && !orbs.isEmpty() && !(orbs.get(0) instanceof EmptyOrbSlot)) {
            AbstractOrb orbSlot = new EmptyOrbSlot();

            int i;
            for(i = 1; i < orbs.size(); ++i) {
                Collections.swap(orbs, i, i - 1);
            }

            orbs.set(orbs.size() - 1, orbSlot);

            for(i = 0; i < orbs.size(); ++i) {
                setSlot(owner, orbs.get(i), i, maxOrbs);
            }
        }
    }

    public static boolean hasEmptyOrb(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return false;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        if (orbs == null || orbs.isEmpty()) {
            return false;
        } else {
            return orbs.stream().anyMatch(o -> o instanceof EmptyOrbSlot);
        }
    }

    public static boolean hasOrb(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return false;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        if (orbs == null || orbs.isEmpty()) {
            return false;
        } else {
            return !(orbs.get(0) instanceof EmptyOrbSlot);
        }
    }

    public static int filledOrbCount(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return 0;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        if (orbs == null) {
            return 0;
        }
        return (int) orbs.stream().filter(o -> !(o instanceof EmptyOrbSlot)).count();
    }

    public static void increaseMaxOrbSlots(AbstractCreature owner, int amount, boolean playSfx) {
        if (!(owner instanceof AbstractMonster)) return;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        int maxOrbs = getMaxOrbs(owner);
        if (orbs == null) {
            orbs = initSlots(owner, maxOrbs);
        }
        if (maxOrbs == 10) {
            AbstractDungeon.effectList.add(new ThoughtBubble(owner.dialogX, owner.dialogY, 3.0F, AbstractPlayer.MSG[3], false));
        } else {
            if (playSfx) {
                CardCrawlGame.sound.play("ORB_SLOT_GAIN", 0.1F);
            }

            MonsterFields.maxOrbs.set(owner, maxOrbs + amount);

            int i;
            for (i = 0; i < amount; ++i) {
                orbs.add(new EmptyOrbSlot());
            }

            for (i = 0; i < orbs.size(); ++i) {
                setSlot(owner, orbs.get(i), i, orbs.size());
            }

        }
    }

    public static void decreaseMaxOrbSlots(AbstractCreature owner, int amount) {
        if (!(owner instanceof AbstractMonster)) return;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        int maxOrbs = getMaxOrbs(owner);
        if (orbs == null) {
            orbs = initSlots(owner, maxOrbs);
        }
        if (maxOrbs > 0) {
            MonsterFields.maxOrbs.set(owner, Math.max(0, maxOrbs - amount));

            if (!orbs.isEmpty()) {
                orbs.remove(orbs.size() - 1);
            }

            for (int i = 0; i < orbs.size(); ++i) {
                setSlot(owner, orbs.get(i), i, orbs.size());
            }

        }
    }

    public static void applyStartOfTurnOrbs(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return;
        ArrayList<AbstractOrb> orbs = getOrbs(owner);
        if (orbs != null && !orbs.isEmpty()) {
            for (AbstractOrb o : orbs) {
                o.onStartOfTurn();
            }
        }
    }

    public static void applyEndOfTurnOrbs(AbstractCreature owner) {
        if (!(owner instanceof AbstractMonster)) return;
        AbstractDungeon.actionManager.addToBottom(new DoAction(() -> {
            ArrayList<AbstractOrb> orbs = getOrbs(owner);
            if (orbs != null && !orbs.isEmpty()) {
                for (AbstractOrb o : orbs) {
                    if (MonsterOrbField.justChanneled.get(o)) {
                        MonsterOrbField.justChanneled.set(o, false);
                    } else {
                        o.onEndOfTurn();
                    }
                }
            }
        }));
    }

    public static class AnimateMonsterOrbAction extends AbstractGameAction {
        private final int orbCount;

        public AnimateMonsterOrbAction(AbstractCreature owner, int amount) {
            this.source = owner;
            this.orbCount = amount;
        }

        public void update() {
            for (int i = 0; i < orbCount; ++i) {
                triggerEvokeAnimation(source, i);
            }
            this.isDone = true;
        }
    }

    public static class EvokeMonsterOrbAction extends AbstractGameAction {
        private final int orbCount;

        public EvokeMonsterOrbAction(AbstractCreature owner, int amount) {
            if (Settings.FAST_MODE) {
                startDuration = Settings.ACTION_DUR_XFAST;
            } else {
                startDuration = Settings.ACTION_DUR_FAST;
            }

            this.duration = this.startDuration;
            this.source = owner;
            this.orbCount = amount;
            this.actionType = ActionType.DAMAGE;
        }

        public void update() {
            if (this.duration == this.startDuration) {
                for (int i = 0; i < this.orbCount; ++i) {
                    evokeOrb(source);
                }
            }
            this.tickDuration();
        }
    }

    public static class MonsterChannelAction extends AbstractGameAction {
        private final AbstractOrb orbType;
        private final boolean autoEvoke;

        public MonsterChannelAction(AbstractCreature owner, AbstractOrb newOrbType) {
            this(owner, newOrbType, true);
        }

        public MonsterChannelAction(AbstractCreature owner, AbstractOrb newOrbType, boolean autoEvoke) {
            this.duration = Settings.ACTION_DUR_FAST;
            this.source = owner;
            this.orbType = newOrbType;
            this.autoEvoke = autoEvoke;
        }

        public void update() {
            if (this.duration == Settings.ACTION_DUR_FAST) {
                if (this.autoEvoke) {
                    channelOrb(source, orbType);
                } else {
                    for (AbstractOrb o : AbstractDungeon.player.orbs) {
                        if (o instanceof EmptyOrbSlot) {
                            channelOrb(source, orbType);
                            break;
                        }
                    }
                }
                if (Settings.FAST_MODE) {
                    this.isDone = true;
                    return;
                }
            }

            this.tickDuration();
        }
    }

    @SpirePatch2(clz = AbstractOrb.class, method = SpirePatch.CLASS)
    public static class MonsterOrbField {
        public static SpireField<AbstractMonster> monsterOwner = new SpireField<>(() -> null);
        public static SpireField<Boolean> justChanneled = new SpireField<>(() -> false);
    }

    @SpirePatch2(clz = AbstractOrb.class, method = "updateAnimation")
    public static class Reposition {
        @SpirePostfixPatch
        public static void plz(AbstractOrb __instance) {
            AbstractMonster owner = MonsterOrbField.monsterOwner.get(__instance);
            if (owner != null) {
                __instance.cX = MathHelper.orbLerpSnap(__instance.cX, owner.animX + __instance.tX);
                __instance.cY = MathHelper.orbLerpSnap(__instance.cY, owner.animY + __instance.tY);
            }
        }
    }
}
