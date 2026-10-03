package ChimeraMonsters.monsters;

import ChimeraMonsters.patches.EnemyMoveInfoFields;
import ChimeraMonsters.util.AscensionScaling;
import basemod.abstracts.CustomMonster;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.EnemyMoveInfo;

import java.util.HashMap;
import java.util.List;

public abstract class AbstractEasyMonster extends CustomMonster implements AscensionScaling {
    protected HashMap<Byte, EnemyMoveInfo> moveInfos = new HashMap<>();
    protected HashMap<Byte, DamageInfo> moveDamages = new HashMap<>();
    protected MonsterStrings monStrings;
    protected boolean firstMove;

    public AbstractEasyMonster(String id, EnemyType type, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY) {
        super(CardCrawlGame.languagePack.getMonsterStrings(id).NAME, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY);
        this.monStrings = CardCrawlGame.languagePack.getMonsterStrings(id);
        this.type = type;
    }
    public AbstractEasyMonster(String id, EnemyType type, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl, float offsetX, float offsetY, boolean ignoreBlights) {
        super(CardCrawlGame.languagePack.getMonsterStrings(id).NAME, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl, offsetX, offsetY, ignoreBlights);
        this.monStrings = CardCrawlGame.languagePack.getMonsterStrings(id);
        this.type = type;
    }
    public AbstractEasyMonster(String id, EnemyType type, int maxHealth, float hb_x, float hb_y, float hb_w, float hb_h, String imgUrl) {
        super(CardCrawlGame.languagePack.getMonsterStrings(id).NAME, id, maxHealth, hb_x, hb_y, hb_w, hb_h, imgUrl);
        this.monStrings = CardCrawlGame.languagePack.getMonsterStrings(id);
        this.type = type;
    }

    protected void addMove(byte moveCode, Intent intent) {
        addMove(null, moveCode, intent, -1, 0, false);
    }
    protected void addMove(byte moveCode, Intent intent, int baseDamage) {
        addMove(null, moveCode, intent, baseDamage, 0, false);
    }
    protected void addMove(byte moveCode, Intent intent, int baseDamage, int multiplier) {
        addMove(null, moveCode, intent, baseDamage, multiplier, multiplier > 0);
    }
    protected void addMove(byte moveCode, Intent intent, int baseDamage, int multiplier, boolean isMultiDamage) {
        addMove(null, moveCode, intent, baseDamage, multiplier, isMultiDamage);
    }
    protected void addMove(String name, byte moveCode, Intent intent) {
        addMove(name, moveCode, intent, -1, 0, false);
    }
    protected void addMove(String name, byte moveCode, Intent intent, int baseDamage) {
        addMove(name, moveCode, intent, baseDamage, 0, false);
    }
    protected void addMove(String name, byte moveCode, Intent intent, int baseDamage, int multiplier) {
        addMove(name, moveCode, intent, baseDamage, multiplier, multiplier > 0);
    }
    protected void addMove(String name, byte moveCode, Intent intent, int baseDamage, int multiplier, boolean isMultiDamage) {
        EnemyMoveInfo info = new EnemyMoveInfo(moveCode, intent, baseDamage, multiplier, isMultiDamage);
        EnemyMoveInfoFields.name.set(info, name);
        moveInfos.put(moveCode, info);
    }

    protected void linkDamage(byte moveCode, DamageInfo info) {
        moveDamages.put(moveCode, info);
        if (!damage.contains(info)) {
            damage.add(info);
        }
    }

    public void setMoveInfo(byte next) {
        EnemyMoveInfo info = moveInfos.get(next);
        setMove(EnemyMoveInfoFields.name.get(info), next, info.intent, info.baseDamage, info.multiplier, info.isMultiDamage);
    }
    public void setMoveInfo(byte next, String textOverride) {
        EnemyMoveInfo info = moveInfos.get(next);
        setMove(textOverride, next, info.intent, info.baseDamage, info.multiplier, info.isMultiDamage);
    }

    public int indexFromRoll(int i, int choices) {
        return (int) (Math.ceil((i+1)/(100f/choices)) - 1);
    }

    public byte choiceFromRoll(int i, List<Byte> choices) {
        return choices.get(indexFromRoll(i, choices.size()));
    }
}
