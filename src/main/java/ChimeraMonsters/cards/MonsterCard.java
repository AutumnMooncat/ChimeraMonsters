package ChimeraMonsters.cards;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.patches.CantUpgradeFieldPatches;
import ChimeraMonsters.patches.RoomSafetyPatches;
import ChimeraMonsters.patches.TypeOverridePatch;
import ChimeraMonsters.util.ColorUtil;
import basemod.abstracts.CustomCard;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireOverride;
import com.evacipated.cardcrawl.modthespire.lib.SpireSuper;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.colorless.Metamorphosis;
import com.megacrit.cardcrawl.cards.status.VoidCard;
import com.megacrit.cardcrawl.cards.tempCards.Miracle;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.monsters.AbstractMonster;

public class MonsterCard extends CustomCard {
    private static final String[] TYPE_TEXT = CardCrawlGame.languagePack.getUIString(ChimeraMonstersMod.makeID(MonsterCard.class.getSimpleName())).TEXT;
    public AbstractMonster storedMonster;

    // TODO temporary placeholder until screen can actually render monsters
    public MonsterCard(AbstractMonster monster) {
        super(ChimeraMonstersMod.makeID(monster.id+"Card"), monster.name, (String) null, -2, "", CardType.SKILL, CardColor.COLORLESS, CardRarity.SPECIAL, CardTarget.NONE);
        TypeOverridePatch.TypeOverrideField.typeOverride.set(this, monster.type == AbstractMonster.EnemyType.NORMAL ? TYPE_TEXT[0] : monster.type == AbstractMonster.EnemyType.ELITE ? TYPE_TEXT[1] : TYPE_TEXT[2]);
        this.storedMonster = monster;
        this.portrait = monster.type == AbstractMonster.EnemyType.NORMAL ? CardLibrary.getCard(Miracle.ID).portrait : monster.type == AbstractMonster.EnemyType.ELITE ? CardLibrary.getCard(Metamorphosis.ID).portrait : CardLibrary.getCard(VoidCard.ID).portrait;
        processDescription();
        CantUpgradeFieldPatches.CantUpgradeField.preventUpgrades.set(this, true);
    }

    public void processDescription() {
        // TODO this seems awful
        rawDescription = "";
        initializeDescription();
    }

    // TODO this also seems awful
    /*private TextureAtlas.AtlasRegion getPortrait() {
        TextureAtlas.AtlasRegion cardBack = storedPower.type == AbstractPower.PowerType.DEBUFF ? CardLibrary.getCard(VoidCard.ID).portrait : CardLibrary.getCard(Miracle.ID).portrait;
        TextureAtlas.AtlasRegion powerIcon = storedPower.region128;
        powerIcon.flip(false, true);
        cardBack.flip(false, true);
        FrameBuffer fb = ImageHelper.createBuffer(250, 190);
        OrthographicCamera og = new OrthographicCamera(250, 190);
        SpriteBatch sb = new SpriteBatch();
        sb.setProjectionMatrix(og.combined);
        ImageHelper.beginBuffer(fb);
        sb.setColor(Color.WHITE);
        sb.begin();
        sb.draw(cardBack, -125, -95);
        sb.draw(powerIcon, -powerIcon.packedWidth/2F, -powerIcon.packedHeight/2F);
        sb.end();
        fb.end();
        cardBack.flip(false, true);
        powerIcon.flip(false, true);
        TextureRegion a = ImageHelper.getBufferTexture(fb);
        return new TextureAtlas.AtlasRegion(a.getTexture(), 0, 0, 250, 190);
    }*/

    @Override
    public boolean canUpgrade() {
        return false;
    }

    @Override
    public void upgrade() {}

    @Override
    public void use(AbstractPlayer abstractPlayer, AbstractMonster abstractMonster) {}

    @Override
    public AbstractCard makeCopy() {
        return new MonsterCard(storedMonster);
    }

    @SpireOverride
    protected void renderImage(SpriteBatch sb, boolean hovered, boolean selected) {
        storedMonster.drawX = current_x;
        storedMonster.drawY = current_y - 256f * Settings.scale * drawScale;
        if (AbstractDungeon.player == null) {
            AbstractDungeon.player = RoomSafetyPatches.getDummyPlayer();
        }
        storedMonster.render(sb);
        if (AbstractDungeon.player == RoomSafetyPatches.getDummyPlayer()) {
            AbstractDungeon.player = null;
        }
        //SpireSuper.call(sb);
    }

    @SpireOverride
    protected void renderType(SpriteBatch sb) {
        //SpireSuper.call(sb);
    }

    @SpireOverride
    protected void renderTitle(SpriteBatch sb) {
        FontHelper.cardTitleFont.getData().setScale(drawScale * 0.85F);
        FontHelper.renderRotatedText(sb, FontHelper.cardTitleFont, name, current_x, current_y, 0.0F, -256f * drawScale * Settings.scale, angle, false, ColorUtil.WHITE);
        //SpireSuper.call(sb);
    }

    @SpireOverride
    protected void renderTint(SpriteBatch sb) {
        //SpireSuper.call(sb);
    }

    @SpireOverride
    protected void renderEnergy(SpriteBatch sb) {
        //SpireSuper.call(sb);
    }
}
