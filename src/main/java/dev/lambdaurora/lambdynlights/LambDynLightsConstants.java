/*
 * Copyright © 2024 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights;


import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.LoadingModList;

/**
 * Contains constants about LambDynamicLights.
 *
 * @author LambdAurora
 * @version 3.1.2
 * @since 3.0.1
 */
public final class LambDynLightsConstants {
	/**
	 * The namespace of this mod, whose value is {@value}.
	 */
	public static final String NAMESPACE = "lambdynlights";

	/**
	 * The unsupported development mode text.
	 */
	public static final String DEV_MODE_OVERLAY_TEXT = "[LambDynamicLights Dev Version (Unsupported)]";

	/**
	 * {@return {@code true} if this mod is in development mode, or {@code false} otherwise}
	 */
	public static boolean isDevMode() {
		var mod = LoadingModList.get().getModFileById(NAMESPACE);

		return mod == null || mod.getMods().getFirst()
                .getVersion().toString().endsWith("-local");
	}
}
