package com.metype.mmocraft.interfaces;

public interface ISerializable {
    String serialize();
    boolean deserialize(String data);
}
