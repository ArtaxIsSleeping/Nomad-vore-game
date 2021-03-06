package worldgentools.blockdungeon.advanced;

import java.util.ArrayList;

import actor.npc.NPC;
import worldgentools.blockdungeon.DungeonWidgetLoader;
import zone.Tile;
import zone.Zone;

public interface AdvancedBlock{

	boolean canPlace(int x, int y, int[][] grid);

	short getEdgeValue();

	void apply(int x, int y, Tile[][] grid, Zone zone, DungeonWidgetLoader loader, ArrayList<NPC> templates);

	int getKeyHeat();

	public short getValueSide(int i);

	default void mark(int x, int y, int[][] blockGrid, ArrayList<AdvancedOpening> openings) {}

	boolean matchEdges(short[] edgeTypes);

	int getTile(int x, int y);

	String getName();
}
