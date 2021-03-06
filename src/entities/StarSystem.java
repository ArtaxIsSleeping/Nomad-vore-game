package entities;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import entities.scripting.ScriptTool;
import entities.stations.Station;
import nomad.universe.Universe;
import shared.ParserHelper;
import shared.Vec2f;
import shared.Vec2i;
import solarview.spawningSystem.SpawningSystem;
import spaceship.Spaceship;
import spaceship.Spaceship.ShipState;
import spaceship.npcShips.NpcShipController;

public class StarSystem {

	public ArrayList<Entity> entitiesInSystem;
	public String systemName;
	public Vec2i systemPosition;

	public StarSystem(String name, int x, int y) {
		systemName = name;
		systemPosition = new Vec2i(x, y);
	}

	public void GenerateSystem(boolean firstload) {
		if (entitiesInSystem == null) {
			// generate the system
			entitiesInSystem = new ArrayList<Entity>();
			Document doc = ParserHelper.LoadXML("assets/data/systems/" + systemName + ".xml");
			Element root = doc.getDocumentElement();
			Element n = (Element) doc.getFirstChild();
			NodeList children = n.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				Node node = children.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					Element Enode = (Element) node;
					if (Enode.getTagName() == "World") {
						// add this world
						World world = new World(node.getTextContent(), Integer.parseInt(Enode.getAttribute("x")),
								Integer.parseInt(Enode.getAttribute("y")));
						if (Enode.getAttribute("visibility").length() > 0) {
							world.setVisibilityScript(Enode.getAttribute("visibility"));
						}
						entitiesInSystem.add(world);
					}

					if (Enode.getTagName() == "Station") {
						// add this world
						Station station= new Station(node.getTextContent(),
								Integer.parseInt(Enode.getAttribute("x")), Integer.parseInt(Enode.getAttribute("y")));
						if (Enode.getAttribute("visibility").length()>0) {
							station.setVisibilityScript(Enode.getAttribute("visibility"));
						}
						entitiesInSystem.add(station);
					}
					if (Enode.getTagName() == "Star") {
						entitiesInSystem.add(new Star(Integer.parseInt(Enode.getAttribute("intensity")),
								Integer.parseInt(Enode.getAttribute("x")), Integer.parseInt(Enode.getAttribute("y")),
								Enode.getAttribute("sprite")));
					}
					if (Enode.getTagName() == "Spaceship" && firstload == true) {
						Spaceship ship = new Spaceship(Enode.getAttribute("file"),
								Integer.parseInt(Enode.getAttribute("x")), Integer.parseInt(Enode.getAttribute("y")),
								ShipState.SPACE);
						if (Enode.getAttribute("controller").length()>0)
						{
							Document doc0 = ParserHelper.LoadXML("assets/data/shipControllers/" + Enode.getAttribute("controller") + ".xml");
							Element n0 = (Element) doc0.getFirstChild();
							ship.setShipController(new NpcShipController(n0));
						}
						if (Enode.getAttribute("visibility").length()>0) {
							ship.setVisibilityScript(Enode.getAttribute("visibility"));
						}

						entitiesInSystem.add(ship);
					}
				}

			}

		}
	}

	public String getName() {
		return systemName;
	}

	public World getWorld(int x, int y) {
		for (int i = 0; i < entitiesInSystem.size(); i++) {
			Vec2f p = entitiesInSystem.get(i).getPosition();
			int xcomp = (int) p.x;
			int ycomp = (int) p.y;
			if (xcomp == x && ycomp == y) {
				if (entitiesInSystem.get(i).getClass().getName().contains("World")) {
					return (World) entitiesInSystem.get(i);
				}
			}

		}
		return null;
	}

	public void Save(String filename) throws IOException {
		// save file as filename
		File file = new File("saves/" + filename + "/" + systemName + ".sav");
		if (file.exists() == false) {
			file.createNewFile();
		}

		FileOutputStream fstream = new FileOutputStream(file);
		DataOutputStream dstream = new DataOutputStream(fstream);

		// save entities
		if (entitiesInSystem != null) {
			dstream.writeInt(entitiesInSystem.size());
			for (int i = 0; i < entitiesInSystem.size(); i++) {
				if (entitiesInSystem.get(i).isStatic()) {
					dstream.writeBoolean(false);
					entitiesInSystem.get(i).Save(filename);
				} else {
					dstream.writeBoolean(true);
					entitiesInSystem.get(i).save(dstream);
				}

			}
		} else {
			dstream.writeInt(0);
		}
		dstream.close();

	}

	public ArrayList<Entity> getEntities() {
		return entitiesInSystem;
	}

	public void arrival()
	{
		if (entitiesInSystem==null)
		{
			try {
				if (fileExists(Universe.getInstance().getSaveName()))
				{
					GenerateSystem(false);
					load(Universe.getInstance().getSaveName());
				}
				else
				{
					GenerateSystem(true);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}
	}

	public boolean fileExists(String filename) throws IOException
	{
		File file = new File("saves/" + filename + "/" + systemName + ".sav");
		if (file.exists())
		{
			return true;
		}
		return false;
	}

	public void load(String filename) throws IOException {
		File file = new File("saves/" + filename + "/" + systemName + ".sav");
		FileInputStream fstream = new FileInputStream(file);
		DataInputStream dstream = new DataInputStream(fstream);

		int count = dstream.readInt();
		for (int i = 0; i < count; i++) {
			boolean b = dstream.readBoolean();
			if (b == true) {
				int t = dstream.readInt();
				switch (t) {
				case 1:
					Spaceship ship = new Spaceship();
					ship.load(dstream);
					entitiesInSystem.add(ship);
					break;

				}
			}
		}
		dstream.close();
		fstream.close();
	}

	public void systemEntry() {

		Document doc = ParserHelper.LoadXML("assets/data/systems/" + systemName + ".xml");
		Element n = (Element) doc.getFirstChild();
		if (n.getAttribute("script").length() > 0) {
			new ScriptTool(n.getAttribute("script")).run();
		}

		new SpawningSystem(systemName).run(entitiesInSystem);
		for (int i=0;i<entitiesInSystem.size();i++)
		{
			entitiesInSystem.get(i).systemEntry();
		}
	}

	public Vec2i getPosition() {
		return systemPosition;
	}

	public boolean canVisit() {
		Document doc = ParserHelper.LoadXML("assets/data/systems/" + systemName + ".xml");
		Element n = (Element) doc.getFirstChild();
		if (n.getAttribute("restrictionScript").length() > 0) {
			return new ScriptTool(n.getAttribute("restrictionScript")).checkScript();
		}

		return true;
	}

}
