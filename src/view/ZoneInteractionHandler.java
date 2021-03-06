package view;

import actor.Actor;
import actor.Attackable;
import actor.npc.NPC;
import actor.player.Player;
import actorRPG.player.Player_RPG;
import combat.CombatMove.AttackPattern;
import faction.violation.FactionListener;
import faction.violation.FactionRule.ViolationType;
import shared.Vec2f;
import shared.Vec2i;
import vmo.GameManager;
import widgets.Widget;
import widgets.WidgetBreakable;
import widgets.WidgetSlot;
import zone.Zone;

public class ZoneInteractionHandler {

	Zone m_zone;

	private FactionListener factionListener;

	public static ModelController_Int m_view;

	ZoneInteractionHandler(Zone zone, ModelController_Int view) {
		setZone(zone);
		m_view = view;
	}

	public void update() {
		if (factionListener != null) {
			factionListener.update();
		}

	}

	public void setZone(Zone zone) {
		if (zone != null) {
			if (factionListener != null) {
				m_zone.setViolationLevel(factionListener.getViolationLevel());
			}
			m_zone = zone;
			factionListener = null;
			if (zone.getZoneRules() != null) {
				factionListener = new FactionListener(zone.getZoneRules(), m_zone.getActors());
				if (zone.getViolationLevel() > 0) {
					factionListener.setViolation(zone.getViolationLevel(), null);
				}
			}
		}
	}

	public void shutdown() {
		if (factionListener != null) {
			if (factionListener.getViolationLevel() > 0) {
				m_zone.setViolationLevel(factionListener.getViolationLevel());
			}
		}
	}

	public boolean ContainsPoint(Vec2f p) {
		if (p.x >= 0 && p.x < m_zone.getWidth()) {
			if (p.y >= 0 && p.y < m_zone.getHeight()) {
				return true;
			}
		}
		return false;
	}

	public boolean Look(Vec2f p) {
		// check for tile visibility
		int x0 = (int) p.x;
		int y0 = (int) p.y;
		if (m_zone.zoneTileGrid[x0][y0] == null) {
			return false;
		}
		if (m_zone.zoneTileGrid[x0][y0].getVisible() == false) {
			return false;
		}

		// check for actor in tile
		if (m_zone.getActor((int)p.x,(int)p.y)!=null) {
			if (m_zone.getActor((int)p.x,(int)p.y).getVisible()) {
				m_view.DrawText(m_zone.zoneTileGrid[(int) p.x][(int) p.y].getActorInTile().getDescription());

				return false;
			}
		}

		if (m_zone.zoneTileGrid[x0][y0].getWidgetObject() != null) {
			String str = m_zone.zoneTileGrid[x0][y0].getWidgetObject().getDescription();
			if (str == null) {
				str = m_zone.zoneTileGrid[x0][y0].getDefinition().getDescription();
			}
			m_view.DrawText(str);

			return false;
		}

		// just post the description for the tile
		m_view.DrawText(m_zone.zoneTileGrid[x0][y0].getDefinition().getDescription());

		return false;
	}

	boolean InteractActor(Vec2f p, Player player) {
		// if actor is player dont interact
		Actor actor = m_zone.getActor((int) p.x, (int) p.y);
		if (actor != null) {
			if (actor != player) {
				actor.Interact(player);
				player.TakeAction();
				violationCheck(actor.getName(), p, ViolationType.Interact);
				return true;
			}
		}
		return false;
	}

	public boolean CanTalk(Player player) {
		if (m_zone.getZoneConditions()!=null && m_zone.getZoneConditions().getDanger())
		{
			return false;
		}
		for (int i = 0; i < m_zone.getActors().size(); i++) {
			Actor actor = m_zone.getActors().get(i);
			if (actor.getClass().getName().contains("NPC")) {
				NPC npc = (NPC) actor;
				if (npc.getVisible() == true && npc.getRPGHandler().getActive() == true) {
					if (npc.isHostile(player.getActorFaction().getFilename()) == true) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void Interact(Vec2f p, Player player) {
		if (canAct(player))
		{
			return;
		}
		if (!m_zone.contains((int)p.x, (int)p.y))
		{
			return;
		}
		if (m_zone.zoneTileGrid[(int) p.x][(int) p.y] == null) {
			return;
		}
		// check position within 1 tile
		float d = p.getDistance(player.getPosition());
		if (d < 2.5F) {
			if (CanTalk(player)) {
				// actor check
				if (InteractActor(p, player) == false) {
					// check for widget to interact with
					if (m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject() != null) {
						m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject().Interact(player);
						if (m_zone.contains((int)p.x, (int)p.y))
						{
							player.TakeAction();
							if (WidgetBreakable.class
									.isInstance(m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject())) {
								WidgetBreakable w = (WidgetBreakable) m_zone.zoneTileGrid[(int) p.x][(int) p.y]
										.getWidgetObject();
								violationCheck(w.getName(), p, ViolationType.Interact);
							}
						}
					}
				}
			} else {
				// check for widget to interact with
				if (m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject() != null) {
					Widget widget = m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject();
					if (widget.safeOnly()) {
						if (CanTalk(player)) {
							widget.Interact(player);
							player.TakeAction();
							if (WidgetBreakable.class
									.isInstance(m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject())) {
								WidgetBreakable w = (WidgetBreakable) m_zone.zoneTileGrid[(int) p.x][(int) p.y]
										.getWidgetObject();
								violationCheck(w.getName(), p, ViolationType.Interact);
							}
						}
						else {
							m_view.DrawText("Not safe to do that right now");
						}
					} else {
						if (WidgetBreakable.class
								.isInstance(m_zone.zoneTileGrid[(int) p.x][(int) p.y].getWidgetObject())) {
							WidgetBreakable w = (WidgetBreakable) m_zone.zoneTileGrid[(int) p.x][(int) p.y]
									.getWidgetObject();
							violationCheck(w.getName(), p, ViolationType.Interact);
						}
						widget.Interact(player);
						player.TakeAction();

					}

				} else {
					m_view.DrawText("Not safe to do that right now");
				}

			}
		}
	}

	public void violationCheck(String name, Vec2f p, ViolationType type) {
		if (factionListener != null) {
			factionListener.checkViolation(name, p, type);
		}
	}


	public static int getDirection(Vec2f o, Vec2f p) {
		int d = 0;
		if (o.x > p.x) {
			d = d + 1; // east
		}
		if (o.x < p.x) {
			d = d + 2;// west
		}
		if (o.y > p.y) {
			d = d + 4; // south
		}
		if (o.y < p.y) {
			d = d + 8; // north
		}
		switch (d) {

		case 2:
			return 2;
		case 1:
			return 6;
		case 4:
			return 4;
		case 5:
			return 5;
		case 6:
			return 3;
		case 8:
			return 0;
		case 9:
			return 7;
		case 10:
			return 1;
		}

		return -1;
	}

	public static Vec2f getPos(int i, Vec2f p) {
		switch (i) {
		case 0:
			return new Vec2f(p.x, p.y + 1);
		case 1:
			return new Vec2f(p.x + 1, p.y + 1);
		case 2:
			return new Vec2f(p.x + 1, p.y);
		case 3:
			return new Vec2f(p.x + 1, p.y - 1);
		case 4:
			return new Vec2f(p.x, p.y - 1);
		case 5:
			return new Vec2f(p.x - 1, p.y - 1);
		case 6:
			return new Vec2f(p.x - 1, p.y);
		case 7:
			return new Vec2f(p.x - 1, p.y + 1);
		}
		return p;
	}

	public static Vec2i getPos(int i, Vec2i p) {
		switch (i) {
		case 0:
			return new Vec2i(p.x, p.y + 1);
		case 1:
			return new Vec2i(p.x + 1, p.y + 1);
		case 2:
			return new Vec2i(p.x + 1, p.y);
		case 3:
			return new Vec2i(p.x + 1, p.y - 1);
		case 4:
			return new Vec2i(p.x, p.y - 1);
		case 5:
			return new Vec2i(p.x - 1, p.y - 1);
		case 6:
			return new Vec2i(p.x - 1, p.y);
		case 7:
			return new Vec2i(p.x - 1, p.y + 1);
		}
		return p;
	}

	public FactionListener getFactionListener() {
		return factionListener;
	}

	public void setFactionListener(FactionListener factionListener) {
		this.factionListener = factionListener;
	}

	private boolean canAct(Player player)
	{
		if (player.getRPG().getStatusEffectHandler().isTransformed())
		{
			return true;
		}
		return false;
	}

	public void attack(Vec2f p, Player player) {
		if (canAct(player))
		{
			return;
		}
		// use first attack on location
		if (!useMove(0, p, player)) {
			Player_RPG rpg = (Player_RPG) player.getRPG();
			rpg.genMoveList();
		}
	}

	public boolean special(Vec2f p, Player player) {
		// use numbered attack on the location
		if (canAct(player))
		{
			return false;
		}
		return useMove(player.getSpecialMove(), p, player);
	}

	private boolean useTargetedMove(int number, int x, int y, Player player) {
		if (GameManager.m_los.existsLineOfSight(m_zone.getBoard(1), (int) player.getPosition().x,
				(int) player.getPosition().y, x, y, true)) {
			if (m_zone.zoneTileGrid[x][y].getWidgetObject() != null) {
				if (Attackable.class.isInstance(m_zone.zoneTileGrid[x][y].getWidgetObject())) {
					Attackable attackable = (Attackable) m_zone.zoneTileGrid[x][y].getWidgetObject();
					// attack this
					m_view.Flash(new Vec2f(x, y), 0);
					// attackable.Harm(player.getAttack().getDamage(0),
					// player,0);
					violationCheck(attackable.getName(), new Vec2f(x, y), ViolationType.Attack);
					return player.useMove(number, attackable);
					// return true;
				}
				if (WidgetSlot.class.isInstance(m_zone.zoneTileGrid[x][y].getWidgetObject())) {
					WidgetSlot ws = (WidgetSlot) m_zone.zoneTileGrid[x][y].getWidgetObject();
					if (ws.getWidget() != null) {
						Attackable attackable = ws.getWidget();
						if (!ws.checkDismantle(player.getMove(number)))
						{
							if (player.useMove(number, attackable)) {
								ws.handleAttack(player.getMove(number));
							} else {
								return false;
							}
						}
						else
						{

							return false;
						}

					}
				}
			}
			// check for actor in tile
			if (m_zone.zoneTileGrid[x][y] != null && m_zone.zoneTileGrid[x][y].getActorInTile() != null
					&& m_zone.zoneTileGrid[x][y].getActorInTile() != player) {
				Attackable attackable = m_zone.zoneTileGrid[x][y].getActorInTile();
				// player.Attack(attackable,m_view);
				if (player.getMove(number).isNonViolent()) {
					violationCheck(attackable.getName(), new Vec2f(x, y), ViolationType.Seduce);
				} else {
					violationCheck(attackable.getName(), new Vec2f(x, y), ViolationType.Attack);
				}

				return player.useMove(number, attackable);
			}

		}

		return false;
	}

	private boolean useSelfMove(int number, Player player) {
		if (player.useMove(number, player)) {
			m_view.Flash(new Vec2f(player.getPosition().x, player.getPosition().y), 2);
			ViewScene.m_interface.redrawBars();
			return true;
		}
		return false;
	}

	private boolean useMove(int number, Vec2f p, Player player) {
		// check widget
		int xt = (int) p.x;
		int yt = (int) p.y;
		// check line of sight
		if (player.getMove(number) == null) {
			return false;
		}
		if (player.getMove(number).getAttackPattern() == AttackPattern.P_SELF) {
			return useSelfMove(number, player);
		}
		return useTargetedMove(number, xt, yt, player);

	}
}
