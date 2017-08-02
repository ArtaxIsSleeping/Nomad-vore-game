package solarview.spaceEncounter;

import spaceship.Spaceship;
import spaceship.stats.SpaceshipAnalyzer;
import vmo.Game;
import java.util.List;
import shipsystem.ShipConverter;
public class SpaceCombatInitializer {

	private Spaceship player;
	private Spaceship enemy;
	
	public SpaceCombatInitializer(Spaceship player, Spaceship enemy)
	{
		this.player=player;
		this.enemy=enemy;
	}
	
	public void run()
	{
		List <ShipConverter> list=enemy.getShipStats().getConverters();
		for (int i=0;i<list.size();i++)
		{
			if (list.get(i).getConvertTo().equals("ENERGY"))
			{
				list.get(i).setActive(true);			
			}
		}
		enemy.setShipStats(new
				 SpaceshipAnalyzer().generateStats(enemy));
		list=enemy.getShipStats().getConverters();
		for (int i=0;i<list.size();i++)
		{
			list.get(i).setActive(true);
		}
		Spaceship []enemies=new Spaceship[1];
		enemies[0]=enemy;
		Game.sceneManager.SwapScene(new SpaceEncounter(player,enemies));
	}
	
}