package solarview;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import rendering.Sprite;
import rendering.Square_Rotatable_Int;
import shared.Vec2f;

public class SpriteRotatable extends Sprite implements Square_Rotatable_Int {

	float currentFacing;
	public SpriteRotatable(Vec2f vec2f, int numFrames) {
		super(vec2f, 1, numFrames);
		m_matrix.m00=1;	m_matrix.m11=1; m_matrix.m22=1; m_matrix.m33=1;
		spriteDepth=0.6F;
	}

	@Override
	public int getFacing() {

		return (int) currentFacing;
	}

	@Override
	public void setFacing(int facing) {

		currentFacing=facing;
		Matrix4f.setIdentity(m_matrix);
		m_matrix.m00=spriteSize;	m_matrix.m11=spriteSize; m_matrix.m22=spriteSize; m_matrix.m33=1;
		m_matrix.m30=0; m_matrix.m31=0; m_matrix.m32=0;

		//rotate things
		Matrix4f.rotate(((float)facing)*-0.785398F, new Vector3f(0,0,1), m_matrix, m_matrix);
		m_matrix.m30=spritePosition.x+0.5F;
		m_matrix.m31=spritePosition.y+0.5F;			
	}

	@Override
	public void setFacing(float facing) {

		currentFacing=facing;
		Matrix4f.setIdentity(m_matrix);
		m_matrix.m00=spriteSize;	m_matrix.m11=spriteSize; m_matrix.m22=spriteSize; m_matrix.m33=1;
		m_matrix.m30=0; m_matrix.m31=0; m_matrix.m32=0;

		//rotate things
		Matrix4f.rotate(((float)facing)*-0.785398F, new Vector3f(0,0,1), m_matrix, m_matrix);
		m_matrix.m30=spritePosition.x+0.5F;
		m_matrix.m31=spritePosition.y+0.5F;			
	}	
	
}
