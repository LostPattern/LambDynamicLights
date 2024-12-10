/*
 * Copyright © 2024 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.compat;

import dev.lambdaurora.lambdynlights.LambDynLights;
import net.minecraft.world.entity.LivingEntity;
import top.theillusivec4.curios.api.CuriosApi;

/**
 * Represents the Accessories compatibility layer.
 *
 * @author LambdAurora
 * @version 3.1.4
 * @since 3.1.4
 */
final class CuriosCompat implements CompatLayer {
	@Override
	public int getLivingEntityLuminanceFromItems(LivingEntity entity, boolean submergedInWater) {
		int luminance = 0;
		var inventory = CuriosApi.getCuriosInventory(entity);

		if (inventory.isPresent()) {
			for (var handler : inventory.get().getCurios().values()) {
				var stacks = handler.getStacks();
				for (var slot = 0; slot < stacks.getSlots(); slot++) {
					luminance = Math.max(luminance, LambDynLights.getLuminanceFromItemStack(stacks.getStackInSlot(slot), submergedInWater));

					if (luminance >= 15) {
						break;
					}
				}
			}
		}

		return luminance;
	}
}
