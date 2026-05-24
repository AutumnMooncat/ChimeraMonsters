package ChimeraMonsters.ui;

import basemod.ModLabel;
import basemod.ModPanel;
import basemod.helpers.UIElementModificationHelper;
import basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper.HeaderlessTip;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.helpers.Hitbox;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.helpers.input.InputHelper;

import java.util.function.Consumer;

public class ModHoverTipLabel extends ModLabel {
    private static final float dx = 12f * Settings.scale;
    private static final float dy = 10f * Settings.scale;
    public String tooltip;
    public Hitbox hb;

    public ModHoverTipLabel(String labelText, String tip, float xPos, float yPos, ModPanel p, Consumer<ModLabel> updateFunc) {
        this(labelText, tip, xPos, yPos, Color.WHITE, FontHelper.buttonLabelFont, p, updateFunc);
    }

    public ModHoverTipLabel(String labelText, String tip, float xPos, float yPos, Color color, ModPanel p, Consumer<ModLabel> updateFunc) {
        this(labelText, tip, xPos, yPos, color, FontHelper.buttonLabelFont, p, updateFunc);
    }

    public ModHoverTipLabel(String labelText, String tip, float xPos, float yPos, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
        this(labelText, tip, xPos, yPos, Color.WHITE, font, p, updateFunc);
    }

    public ModHoverTipLabel(String labelText, String tip, float xPos, float yPos, Color color, BitmapFont font, ModPanel p, Consumer<ModLabel> updateFunc) {
        super(labelText, xPos, yPos, color, font, p, updateFunc);
        this.tooltip = tip;
        this.hb = new Hitbox(this.x - dx, this.y - dy, ImageMaster.OPTION_TOGGLE.getWidth() * Settings.scale, ImageMaster.OPTION_TOGGLE.getHeight() * Settings.scale);
        wrapHitboxToText(labelText, 1000f, 0f, font);
    }

    public void wrapHitboxToText(String text, float lineWidth, float lineSpacing, BitmapFont font) {
        float tWidth = FontHelper.getSmartWidth(font, text, lineWidth, lineSpacing);
        this.hb.width = tWidth + 12.0F * Settings.scale;
    }

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        hb.render(sb);
        if (tooltip != null && hb.hovered) {
            HeaderlessTip.renderHeaderlessTip((float) InputHelper.mX + 60.0F * Settings.scale, (float)InputHelper.mY - 50.0F * Settings.scale, this.tooltip);// 45
        }
    }

    @Override
    public void update() {
        super.update();
        hb.update();
    }

    @Override
    public void set(float xPos, float yPos) {
        super.set(xPos, yPos);
        fixHitBox();
    }

    @Override
    public void setX(float xPos) {
        super.setX(xPos);
        fixHitBox();
    }

    @Override
    public void setY(float yPos) {
        super.setY(yPos);
        fixHitBox();
    }

    private void fixHitBox() {
        UIElementModificationHelper.moveHitboxByOriginalParameters(hb, x - dx, y - dy);
    }
}
