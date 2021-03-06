package item;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import actor.Modifier;
import actor.player.Inventory;
import combat.CombatMove;

public class ItemWeapon extends ItemEquip {

	private String tagSet = "";

	public ItemWeapon(Element Inode, int uid) {
		super(uid);
		NodeList children = Inode.getChildNodes();

		m_use = ItemUse.EQUIP;
		m_name = Inode.getAttribute("name");
		if (Inode.getAttribute("shortName").length()>1)
		{
			shortName = Inode.getAttribute("shortName");			
		}	
		m_weight = Float.parseFloat(Inode.getAttribute("weight"));
		itemValue = Float.parseFloat(Inode.getAttribute("value"));
		m_slot = Inventory.HAND;
		moveList = new ArrayList<CombatMove>();

		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				if (Enode.getTagName() == "description") {
					m_description = Enode.getTextContent().replace("\n", "");
				}
				if (Enode.getTagName() == "modifier") {
					m_modifier = new Modifier(Enode);
				}
				if (Enode.getTagName() == "combatMove") {
					moveList.add(new CombatMove(Enode));
				}
				if (Enode.getTagName() == "energy") {
					m_energy = new ItemEnergy(Enode);
				}
				if (Enode.getTagName() == "stackEquip") {
					if (Enode.getAttribute("value").equals("true")) {
						stackEquip = true;
					}
				}
				if (Enode.getTagName() == "tagSet") {
					tagSet = Enode.getAttribute("value");
				}
			}
		}
	}

	public String getTagSet() {
		return tagSet;
	}

	public boolean canStack()
	{
		return stackEquip;
	}
}
