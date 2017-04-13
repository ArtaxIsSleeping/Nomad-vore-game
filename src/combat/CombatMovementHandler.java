package combat;

import actor.Actor;
import nomad.Universe;
import shared.Vec2f;
import view.ViewScene;
import zone.Tile;
import zone.TileDef.TileMovement;

public class CombatMovementHandler {

	public static void MoveAway(Actor actor, Actor from,int distance)
	{
		Vec2f direction=new Vec2f(actor.getPosition().x-from.getPosition().x,actor.getPosition().y-from.getPosition().y);
		direction.normalize();
		Vec2f p=new Vec2f(actor.getPosition().x,actor.getPosition().y);
		Vec2f last=p;
		for (int i=0;i<distance;i++)
		{
			
			p.x+=direction.x; p.y+=direction.y;
			int x=(int)p.x; int y=(int)p.y;
			Tile t=Universe.getInstance().getCurrentZone().getTile(x, y);
			if (t==null)
			{
				break;
			}
			if (t.getDefinition().getBlockVision())
			{
				break;
			}
			if (t.getDefinition().getMovement()==TileMovement.WALK && t.getActorInTile()==null)
			{
				last=new Vec2f(x,y);
			}	
		}
		actor.setPosition(last);
		ViewScene.m_interface.redraw();
	}
}