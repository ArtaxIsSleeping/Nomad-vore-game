package worldgentools.blockdungeon;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import item.Item;
import shared.ParserHelper;

import widgets.Widget;
import widgets.WidgetBreakable;
import widgets.WidgetContainer;
import widgets.WidgetConversation;
import widgets.WidgetDoor;
import widgets.WidgetHarvestable;
import widgets.WidgetItemPile;
import widgets.WidgetPortal;
import widgets.WidgetScripted;
import widgets.traps.Widget_Trap;
import worldgentools.LootTable;

public class DungeonWidgetLoader {

	private Map<String, LootTable> lootTables;

	public DungeonWidgetLoader() {
		lootTables = new HashMap<String, LootTable>();

	}

	public Widget addItemPile(WidgetDefinition definition) {
		LootTable lt = lootTables.get(definition.getWidgetInfo());
		ArrayList<Item> itemList = lt.generateLoot();

		WidgetItemPile pile = new WidgetItemPile(2, "a pile of items containing ");
		for (int i = 0; i < itemList.size(); i++) {
			pile.AddItem(itemList.get(i));
		}

		return pile;
	}

	public Widget loadWidget(WidgetDefinition definition) {
		if (definition.getWidgetName().equals("itempile")) {
			return addItemPile(definition);
		}
		Document doc = ParserHelper.LoadXML("assets/data/widgets/" + definition.getWidgetName() + ".xml");
		Element root = doc.getDocumentElement();
		Element n = (Element) doc.getFirstChild();
		Widget widget = null;
		if (root.getTagName().contains("breakable")) {
			widget = new WidgetBreakable(n);
		}
		if (root.getTagName().contains("trap")) {
			widget = new Widget_Trap(n);
		}
		if (root.getTagName().contains("portal")) {
			widget = new WidgetPortal(root);
			WidgetPortal wp = (WidgetPortal) widget;
			wp.setDestination(definition.getWidgetInfo(), Integer.parseInt(definition.getWidgetVariable()));
		}
		if (root.getTagName().contains("harvestable")) {
			widget = new WidgetHarvestable(root);
		}
		if (root.getTagName().contains("door")) {
			widget = new WidgetDoor(root);
			WidgetDoor wd = (WidgetDoor) widget;
			if (definition.getWidgetVariable() != null && definition.getWidgetVariable().length() > 0) {
				wd.setLockStrength(Integer.parseInt(definition.getWidgetVariable()));
			}
			if (definition.getWidgetInfo() != null) {
				wd.setLockKey(definition.getWidgetInfo());
			}
		}
		if (root.getTagName().contains("scripted")) {
			widget = new WidgetScripted(root);
			WidgetScripted ws = (WidgetScripted) widget;
			if (definition.getWidgetInfo() != null) {
				ws.setScript(definition.getWidgetInfo());
			}
		}
		if (root.getTagName().contains("container")) {
			widget = new WidgetContainer(root);
			WidgetContainer wc = (WidgetContainer) widget;
			LootTable lt = lootTables.get(definition.getWidgetInfo());
			wc.setItems(lt.generateLoot());
		}
		if (root.getTagName().contains("conversation")) {
			widget = new WidgetConversation(root);
			WidgetConversation wc = (WidgetConversation) widget;
			wc.setConversationFileName(definition.getWidgetInfo());
			wc.setSprite(Integer.parseInt(definition.getWidgetVariable()));
		}
		return widget;
	}

	public void loadLootTable(Element enode) {

		LootTable loot = new LootTable(enode);
		lootTables.put(enode.getAttribute("identity"), loot);

	}

}
