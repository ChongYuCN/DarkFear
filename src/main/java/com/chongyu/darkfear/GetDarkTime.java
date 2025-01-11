package com.chongyu.darkfear;

import net.minecraft.entity.player.PlayerEntity;

import java.lang.reflect.Field;

public class GetDarkTime {
    public static boolean isInDark(PlayerEntity player) throws IllegalAccessException {
        Field darkTime = null;
        try {
            darkTime = PlayerEntity.class.getDeclaredField("darkTime");
        } catch (Exception e) {
            e.printStackTrace();
        }
        assert darkTime != null;
        return darkTime.getInt(player)>=6*20;
    }
}
