package ChimeraMonsters.ui;

import ChimeraMonsters.util.TipBuffer;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.ModToggleButton;
import basemod.patches.com.megacrit.cardcrawl.helpers.TipHelper.HeaderlessTip;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.core.Settings;

import java.util.function.Consumer;

public class ModLabeledToggleTooltipButton extends ModLabeledToggleButton {
    private final Consumer<ModLabeledToggleTooltipButton> updateFunc;
    public ModLabeledToggleTooltipButton(String labelText, String tooltipText, float xPos, float yPos, Color color, BitmapFont font, boolean enabled, ModPanel p, Consumer<ModLabeledToggleTooltipButton> updateFunc, Consumer<ModToggleButton> c) {
        super(labelText, tooltipText, xPos, yPos, color, font, enabled, p, l -> {}, c);
        this.updateFunc = updateFunc;
    }

    @Override
    public void render(SpriteBatch sb) {
        this.toggle.render(sb);
        this.text.render(sb);
    }

    @Override
    public void update() {
        updateFunc.accept(this);
        super.update();
        if (this.toggle.enabled) {
            TipBuffer.renderWith(sb -> {
                HeaderlessTip.renderHeaderlessTip(toggle.getX()*Settings.scale + 00.0F*Settings.scale, toggle.getY()*Settings.scale - 35.0F*Settings.scale, this.tooltip);
            });
        }
    }
}
