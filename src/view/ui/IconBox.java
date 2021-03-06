package view.ui;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import actorRPG.player.Player_RPG;
import combat.statusEffects.StatusEffect;
import gui.GUIBase;
import nomad.universe.Universe;
import rendering.Sprite;
import shared.Tools;
import shared.Vec2f;

public class IconBox extends GUIBase {

	int textureId;
	Sprite[] sprites;
	Vec2f position;

	public IconBox(Vec2f p) {
		// load texture
		textureId = Tools.loadPNGTexture("assets/art/statusEffects.png", GL13.GL_TEXTURE0);
		// load sprites
		sprites = new Sprite[8];
		for (int i = 0; i < 8; i++) {
			sprites[i] = new Sprite(new Vec2f(0, 0), 2, 256);
			sprites[i].setVisible(true);
			sprites[i].setImage(0);
		}
		position = p;
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub

	}

	public void redraw() {
		Player_RPG rpg = (Player_RPG) Universe.getInstance().getPlayer().getRPG();
		ArrayList<StatusEffect> statusEffects = rpg.getStatusEffects();

		for (int i = 0; i < 8; i++) {
			if (i >= statusEffects.size()) {
				sprites[i].setVisible(false);
			} else {
				if (statusEffects.get(i).getStatusIcon()!=-1)
				{
					sprites[i].setVisible(true);
					sprites[i].setImage(statusEffects.get(i).getStatusIcon());				
				}
				else
				{
					sprites[i].setVisible(false);
				}
			}
		}
	}

	@Override
	public boolean ClickEvent(Vec2f pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void Draw(FloatBuffer buffer, int matrixloc) {

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);
		for (int i = 0; i < 8; i++) {
			if (sprites[i].getVisible()) {
				sprites[i].draw(matrixloc, 0, buffer);
			}

		}
	}

	@Override
	public void discard() {

		GL11.glDeleteTextures(textureId);
		for (int i = 0; i < 8; i++) {
			sprites[i].discard();
		}
	}

	@Override
	public void AdjustPos(Vec2f p) {
		for (int i = 0; i < 8; i++) {
			sprites[i].reposition(new Vec2f(position.x + p.x + (i * 2), position.y + p.y));
		}
	}

}
