/*
 * Copyright © 2024 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.compat;

import dev.emi.trinkets.api.TrinketsApi;
import dev.lambdaurora.lambdynlights.LambDynLights;
import net.minecraft.world.entity.LivingEntity;

/**
 * Represents the Trinkets compatibility layer.
 *
 * @author LambdAurora
 * @version 3.1.4
 * @since 3.1.4
 */
final class TrinketsCompat implements CompatLayer {
	@Override
	public int getLivingEntityLuminanceFromItems(LivingEntity entity, boolean submergedInWater) {
		int luminance = 0;
		var component = TrinketsApi.getTrinketComponent(entity);

		if (component.isPresent()) {
			for (var equipped : component.get().getAllEquipped()) {
				if (!equipped.getB().isEmpty()) {
					luminance = Math.max(luminance, LambDynLights.getLuminanceFromItemStack(equipped.getB(), submergedInWater));

					if (luminance >= 15) {
						break;
					}
				}
			}
		}

		return luminance;
	}
}
