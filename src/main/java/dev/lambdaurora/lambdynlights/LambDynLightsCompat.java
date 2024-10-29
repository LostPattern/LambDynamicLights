/*
 * Copyright © 2020 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights;

import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.LoadingModList;

/**
 * Represents a utility class for compatibility.
 *
 * @author LambdAurora
 * @version 3.1.0
 * @since 1.0.0
 */
public final class LambDynLightsCompat {
	/**
	 * Returns whether Canvas is installed.
	 *
	 * @return {@code true} if Canvas is installed, else {@code false}
	 */
	public static boolean isCanvasInstalled() {
		return LoadingModList.get().getModFileById("canvas") != null;
	}

	public static boolean isSodiumInstalled() {
		return LoadingModList.get().getModFileById("sodium") != null;
	}

	public static boolean isSodium05XInstalled() {
		return false;
	}
}
