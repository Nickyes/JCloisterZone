package com.jcloisterzone.game.capability;

import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.common.collect.Sets;
import com.jcloisterzone.Player;
import com.jcloisterzone.board.Tile;
import com.jcloisterzone.game.CapabilityController;

public class AbbeyCapability extends CapabilityController {

    private Set<Player> unusedAbbey = Sets.newHashSet();

    @Override
    public void initPlayer(Player player) {
        unusedAbbey.add(player);
    }

    @Override
    public String getTileGroup(Tile tile) {
        return tile.getId().equals(Tile.ABBEY_TILE_ID) ? "inactive": null;
    }

    public boolean hasUnusedAbbey(Player player) {
        return unusedAbbey.contains(player);
    }

    public void useAbbey(Player player) {
        if (!unusedAbbey.remove(player)) {
            throw new IllegalArgumentException("Player alredy used his abbey");
        }
    }

    @Override
    public AbbeyCapability copy() {
        AbbeyCapability copy = new AbbeyCapability();
        copy.game = game;
        copy.unusedAbbey = Sets.newHashSet(unusedAbbey);
        return copy;
    }

    @Override
    public void saveToSnapshot(Document doc, Element node) {
        for (Player player: game.getAllPlayers()) {
            Element el = doc.createElement("player");
            node.appendChild(el);
            el.setAttribute("index", "" + player.getIndex());
            el.setAttribute("abbey", "" + unusedAbbey.contains(player));
        }
    }

    @Override
    public void loadFromSnapshot(Document doc, Element node) {
        NodeList nl = node.getElementsByTagName("player");
        for (int i = 0; i < nl.getLength(); i++) {
            Element playerEl = (Element) nl.item(i);
            Player player = game.getPlayer(Integer.parseInt(playerEl.getAttribute("index")));
            if (!Boolean.parseBoolean(playerEl.getAttribute("abbey"))) {
                useAbbey(player);
            }
        }
    }

}