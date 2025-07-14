package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.util.TextureLoader;
import basemod.ReflectionHacks;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;
import com.megacrit.cardcrawl.vfx.combat.SilentGainPowerEffect;

import java.util.ArrayList;

public abstract class AbstractEasyPower extends AbstractPower {
    private final ArrayList<AbstractGameEffect> array;
    private float flashTimer;
    private boolean flashing;

    public AbstractEasyPower(String ID, String name, PowerType powerType, boolean isTurnBased, AbstractCreature owner, int amount) {
        this.ID = ID;
        this.name = name;
        this.isTurnBased = isTurnBased;

        this.owner = owner;
        this.amount = amount;
        this.type = powerType;

        String modID = ChimeraMonstersMod.getModID();
        Texture normalTexture = TextureLoader.getTexture(modID + "Resources/images/powers/" + ID.replace(":", "").replaceAll(modID,"") + "32.png");
        Texture hiDefImage = TextureLoader.getTexture(modID + "Resources/images/powers/" + ID.replace(":", "").replaceAll(modID,"") + "84.png");
        if (hiDefImage != null && hiDefImage != TextureLoader.missingTexture) {
            region128 = new TextureAtlas.AtlasRegion(hiDefImage, 0, 0, hiDefImage.getWidth(), hiDefImage.getHeight());
            if (normalTexture != null && normalTexture != TextureLoader.missingTexture)
                region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
        } else if (normalTexture != null && normalTexture != TextureLoader.missingTexture) {
            this.img = normalTexture;
            region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
        } else {
            hiDefImage = TextureLoader.getTexture(modID + "Resources/images/powers/PlaceholderPower84.png");
            normalTexture = TextureLoader.getTexture(modID + "Resources/images/powers/PlaceholderPower32.png");
            region128 = new TextureAtlas.AtlasRegion(hiDefImage, 0, 0, hiDefImage.getWidth(), hiDefImage.getHeight());
            region48 = new TextureAtlas.AtlasRegion(normalTexture, 0, 0, normalTexture.getWidth(), normalTexture.getHeight());
        }

        this.updateDescription();
        array = ReflectionHacks.getPrivateInherited(this, AbstractEasyPower.class, "effect");
    }

    protected void startFlashing() {
        flashing = true;
    }

    protected void stopFlashing() {
        flashing = false;
        flashTimer = 0f;
    }

    @Override
    public void update(int slot) {
        super.update(slot);
        if (flashing) {
            flashTimer += Gdx.graphics.getDeltaTime();
            if (flashTimer > 1f) {
                array.add(new SilentGainPowerEffect(this));
                flashTimer = 0f;
            }
        }
    }
}