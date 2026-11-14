package com.companion;

import com.companion.Items.*;
import net.fabricmc.api.ModInitializer;


import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompanionAI implements ModInitializer {
	public static final String MOD_ID = "companionai";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ParticleManage.MAINparticleReg();

		LOGGER.info("Hello Fabric world!");
	}


}