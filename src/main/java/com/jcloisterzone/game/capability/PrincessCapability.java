package com.jcloisterzone.game.capability;

import com.jcloisterzone.action.ReturnMeepleAction;
import com.jcloisterzone.board.pointer.MeeplePointer;
import com.jcloisterzone.feature.City;
import com.jcloisterzone.feature.Feature;
import com.jcloisterzone.feature.Scoreable;
import com.jcloisterzone.game.Capability;
import com.jcloisterzone.game.state.Flag;
import com.jcloisterzone.game.state.GameState;
import com.jcloisterzone.game.state.PlacedTile;
import com.jcloisterzone.io.message.ReturnMeepleMessage.ReturnMeepleSource;
import io.vavr.collection.Set;
import org.w3c.dom.Element;

import static com.jcloisterzone.XMLUtils.attributeBoolValue;

public class PrincessCapability extends Capability<Void> {

	private static final long serialVersionUID = 1L;

    @Override
    public Feature initFeature(GameState state, String tileId, Feature feature, Element xml) {
        if (feature instanceof City && attributeBoolValue(xml, "princess")) {
            feature = ((City)feature).setPrincess(true);
        }
        return feature;
    }

    @Override
    public GameState onActionPhaseEntered(GameState state) {
        if (state.getFlags().contains(Flag.PRINCESS_USED)) {
            return state;
        }

        PlacedTile lastTile = state.getLastPlaced();
        Set<MeeplePointer> options = state.getTileFeatures2(lastTile.getPosition(), Scoreable.class)
        .filter(t -> {
            if (t._2 instanceof City) {
                City part = (City) lastTile.getInitialFeaturePartOf(t._1);
                return part.isPrincess();
            } else {
                return false;
            }
        })
        .flatMap(featureTuple -> {
            City cityWithPrincess = (City) featureTuple._2;
            return cityWithPrincess.getFollowers2(state).map(MeeplePointer::new);
        })
        .toSet();

        if (options.isEmpty()) {
            return state;
        }

        return state.appendAction(new ReturnMeepleAction(options, ReturnMeepleSource.PRINCESS));
    }
}
