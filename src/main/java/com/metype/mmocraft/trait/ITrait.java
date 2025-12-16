package com.metype.mmocraft.trait;

import com.metype.mmocraft.interfaces.*;
import com.metype.mmocraft.player.MMOPlayer;

public interface ITrait extends INamed, ISerializable, ICopyable, IIdentifiable, IDescribable {
    int getLevel();
    double modulate(double input, double factor);
    String getActionMessage();
    boolean levelUp();
    void associatePlayer(MMOPlayer player);
}
