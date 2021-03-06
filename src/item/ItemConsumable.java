package item;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import combat.effect.Effect;
import combat.effect.Effect_Dialogue;
import combat.effect.Effect_Modifier;
import combat.effect.Effect_Perk;
import combat.effect.Effect_Recover;
import combat.effect.Effect_RemoveStatus;
import combat.effect.Effect_Spawn;
import combat.effect.Effect_Status;
import mutation.Effect_Mutator;

public class ItemConsumable extends Item {
	// Effect_Recover m_effect;
	ArrayList<Effect> effectList;

	public ItemConsumable(Element Inode, int uid) {

		super(uid);
		NodeList children = Inode.getChildNodes();
		int l = children.getLength();
		effectList = new ArrayList<Effect>();
		m_use = ItemUse.USE;
		m_name = Inode.getAttribute("name");
		m_weight = Float.parseFloat(Inode.getAttribute("weight"));
		itemValue = Float.parseFloat(Inode.getAttribute("value"));
		if (Inode.getAttribute("tag").length()>0)
		{
			tag=Integer.parseInt(Inode.getAttribute("tag"));
		}
		;
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element Enode = (Element) node;
				String str = Enode.getTagName();
				if (Enode.getTagName() == "description") {
					m_description = Enode.getTextContent().replace("\n", "");
				}
				if (Enode.getTagName() == "effectrecover") {
					effectList.add(new Effect_Recover(Enode));
				}
				if (Enode.getTagName() == "effectmutator") {
					effectList.add(new Effect_Mutator(Enode));
				}
				if (Enode.getTagName() == "effectstatus") {
					effectList.add(new Effect_Status(Enode));
				}
				if (Enode.getTagName() == "effectperk") {
					effectList.add(new Effect_Perk(Enode));
				}
				if (Enode.getTagName() == "effectmodifier") {
					effectList.add(new Effect_Modifier(Enode));
				}
				if (Enode.getTagName() == "effectDialogue") {
					effectList.add(new Effect_Dialogue(Enode));
				}
				if (Enode.getTagName() == "effectSpawn") {
					effectList.add(new Effect_Spawn(Enode));
				}
				if (Enode.getTagName() == "effectRemoveStatus") {
					effectList.add(new Effect_RemoveStatus(Enode));
				}
			}
		}
	}

	public Effect getEffect(int i) {
		return effectList.get(i);
	}

	public int getNumEffects() {
		return effectList.size();
	}
	
	@Override
	public boolean canStack() {
		return true;
	}
}
