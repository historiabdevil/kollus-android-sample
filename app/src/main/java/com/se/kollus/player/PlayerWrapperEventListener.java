package com.se.kollus.player;

public interface PlayerWrapperEventListener {
    default void progress(int current, PlayerStates playerState){}
    default void loadBookmark(){}
    default void prepared(){};
}
