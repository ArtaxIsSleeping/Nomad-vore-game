package combat.effect.map;

import java.nio.FloatBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import gui.Button;
import gui.Window;
import input.MouseHook;
import nomad.universe.Universe;
import shared.Callback;
import shared.Screen;
import shared.Tools;
import shared.Vec2f;
import zone.Tile;
import zone.TileDef.TileMovement;
import zone.Zone;

public class MapScreen extends Screen {

	private Window window;
	private Callback callback;
	private MapGrid grid;
	private MapIndicator indicator;
	private int[] textures;
	public MapScreen(int strength) {
		setTexture();
		grid = new MapGrid(new Vec2f(0.5F, 3), textures[0]);
		buildGrid(strength);
		grid.setSize(27, 27);
	}

	private void buildGrid(int strength)
	{

		Zone z=Universe.getInstance().getCurrentZone();
		int [][] vgrid=new int[z.getWidth()][];
		for (int i=0;i<vgrid.length;i++)
		{
			vgrid[i]=new int[z.getHeight()];
			for (int j=0;j<vgrid[i].length;j++)
			{
				int v=0;
				if (z.getTile(i, j)!=null && z.getTile(i, j).getExplored())
				{
					Tile t=z.getTile(i, j);
					if (t.getWidgetObject()!=null && strength==3)
					{
						v=13+Universe.m_random.nextInt(2);
					}
					if (t.getDefinition().getMovement()==TileMovement.BLOCK)
					{
						v=8+Universe.m_random.nextInt(3);
						if (t.getDefinition().getBlockVision())
						{
							v=5+Universe.m_random.nextInt(4);
						}
					}
					else if (v==0)
					{
						v+=Universe.m_random.nextInt(4);
					}
				}
				vgrid[i][j]=v;
			}
		}
		if (strength>=2)
		{
			vgrid[(int) Universe.getInstance().getPlayer().getPosition().x]
					[(int) Universe.getInstance().getPlayer().getPosition().y]=12;
			indicator = new MapIndicator(new Vec2f(0.5F, 3), textures[1], vgrid.length, vgrid[0].length);
			indicator.setSize(27, 27);
		}
		grid.Generate(vgrid);
	}

	private void setTexture()
	{
		textures = new int[2];
		textures[0] = Tools.loadPNGTexture("assets/art/map.png", GL13.GL_TEXTURE0);
		textures[1] = Tools.loadPNGTexture("assets/art/green.png", GL13.GL_TEXTURE0);
	}

	@Override
	public void update(float DT) {
		// TODO Auto-generated method stub
		window.update(DT);
	}

	@Override
	public void draw(FloatBuffer buffer, int matrixloc) {

		window.Draw(buffer, matrixloc);
	}

	@Override
	public void discard(MouseHook mouse) {

		window.discard();
		mouse.Remove(window);
		GL11.glDeleteTextures(textures[0]);
		GL11.glDeleteTextures(textures[1]);
	}

	@Override
	public void ButtonCallback(int ID, Vec2f p) {
		switch (ID) {
		case 0:
			callback.Callback();
			break;
		}
	}

	@Override
	public void start(MouseHook hook) {

		hook.Register(window);
	}

	@Override
	public void initialize(int[] textures, Callback callback) {

		// 0 is font
		// 1 is frame
		// 2 button
		// 3 is button alt
		// 4 tint

		this.callback = callback;

		// build window
		window = new Window(new Vec2f(-14, -16.0F), new Vec2f(28, 32), textures[1], true);
		// build button to return
		Button button = new Button(new Vec2f(23.5F, 0.5F), new Vec2f(4, 2), textures[2], this, "exit", 0, 0.8F);
		;
		window.add(button);
		window.add(grid);
		if (indicator != null) {
			window.add(indicator);
		}
	}

}
