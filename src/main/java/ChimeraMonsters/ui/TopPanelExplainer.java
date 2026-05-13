package ChimeraMonsters.ui;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.modifiers.AbstractMonsterModifier;
import ChimeraMonsters.util.FormatHelper;
import ChimeraMonsters.util.TextureLoader;
import basemod.TopPanelItem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.TipHelper;

import java.util.List;
import java.util.stream.Collectors;

public class TopPanelExplainer extends TopPanelItem {
    public static final Texture ICON = TextureLoader.getTexture(ChimeraMonstersMod.makeImagePath("ui/explainerIcon.png"));
    public static final Texture ICON_ATTENTION = TextureLoader.getTexture(ChimeraMonstersMod.makeImagePath("ui/explainerIconNew.png"));
    public static final String ID = ChimeraMonstersMod.makeID("ExplainerItem");
    public static final String[] TEXT = CardCrawlGame.languagePack.getUIString(ID).TEXT;
    private static final float TIP_Y = Settings.HEIGHT - (120.0f * Settings.scale);
    private static final float TOP_RIGHT_TIP_X = 1550.0F * Settings.scale;

    public TopPanelExplainer() {
        super(ICON_ATTENTION, ID);
    }

    public void reset() {
        image = ICON_ATTENTION;
    }

    @Override
    protected void onClick() {}

    @Override
    protected void onHover() {
        image = ICON;
        super.onHover();
    }

    @Override
    protected void onUnhover() {
        super.onUnhover();
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if (hitbox.hovered) {
            TipHelper.renderGenericTip(Math.min(this.x, TOP_RIGHT_TIP_X), TIP_Y, TEXT[0], assembleString());
        }
    }

    private String assembleString() {
        List<AbstractMonsterModifier> mods = ChimeraMonstersMod.currentCombatModifiers();
        if (mods.isEmpty()) {
            return TEXT[1];
        }
        return mods.stream().map(mod -> FormatHelper.prefixWords(mod.getModifierName(), "#y") + ": NL " + mod.getModifierDescription()).collect(Collectors.joining(" NL NL "));
    }
}
