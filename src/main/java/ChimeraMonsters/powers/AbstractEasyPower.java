package ChimeraMonsters.powers;

import ChimeraMonsters.ChimeraMonstersMod;
import ChimeraMonsters.util.TextureLoader;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.powers.AbstractPower;

public abstract class AbstractEasyPower extends AbstractPower {
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
    }
}