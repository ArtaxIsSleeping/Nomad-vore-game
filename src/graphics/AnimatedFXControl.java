package graphics;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import java.util.Queue;

import org.lwjgl.util.vector.Vector4f;

import graphics.rays.Ray;
import rendering.SquareRenderer;
import rendering.Square_Int;
import shared.Vec2f;

public class AnimatedFXControl {

	private Queue<FX> visualEffects;
	private FX currentVisualEffect;
	private Square_Int[] m_squares;
	
	public AnimatedFXControl() {
		visualEffects = new LinkedList<FX>();

		m_squares = new Square_Int[4];
		m_squares[0] = new SquareRenderer(255, new Vec2f(0, 0), new Vector4f(1, 0, 0, 1));
		m_squares[1] = new SquareRenderer(254, new Vec2f(0, 0), new Vector4f(1, 0, 0, 1));
		m_squares[2] = new Ray(253, new Vec2f(0, 0), new Vector4f(1, 0, 0, 1));		
		m_squares[3] = new SquareCollection();
	}

	public void Update() {
		if (currentVisualEffect != null) {
			currentVisualEffect.update(m_squares);

			if (currentVisualEffect.getLifeSpan() == 0) {
				if (visualEffects.size() > 0) {
					currentVisualEffect = visualEffects.poll();
					initEffect(currentVisualEffect);
					currentVisualEffect.update(m_squares);
				} else {
					currentVisualEffect = null;
				}
			}
		}

	}

	public void Draw(int matrixloc, int tint, FloatBuffer matrix44fbuffer) {
		if (currentVisualEffect != null) {
			for (int i=0;i<4;i++)
			{
				if (m_squares[i].getVisible())
				{
					m_squares[i].draw(matrixloc, tint, matrix44fbuffer);		
				}
			}
		}

	}

	private void initEffect(FX effect)
	{
		for(int i=0;i<4;i++)
		{
			m_squares[i].setColour(currentVisualEffect.getRed(),
					currentVisualEffect.getGreen(), currentVisualEffect.getBlue());		
			m_squares[i].setVisible(false);
		}
		//m_squares[effect.getIndex()].reposition(currentVisualEffect.getPosition());
		currentVisualEffect.initializeEffect(m_squares[effect.getIndex()]);
	}
	
	public void addEffect(FX effect) {
		if (currentVisualEffect == null) {
			currentVisualEffect = effect;
			initEffect(effect);
		} else {
			visualEffects.add(effect);
		}

	}

	public boolean getActive() {
		if (currentVisualEffect != null) {
			return true;
		}
		return false;
	}

	public void discard() {
		for (int i = 0; i < m_squares.length; i++) {
			m_squares[i].discard();
		}

	}

	public void CompileEffect(int index, Vec2f p, int r, int g, int b, int lifespan) {
		// TODO Auto-generated method stub
			
	}
}
