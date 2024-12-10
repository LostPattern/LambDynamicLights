/*
 * Copyright © 2021 LambdAurora <email@lambdaurora.dev>
 *
 * This file is part of LambDynamicLights.
 *
 * Licensed under the Lambda License. For more information,
 * see the LICENSE file.
 */

package dev.lambdaurora.lambdynlights.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import dev.lambdaurora.spruceui.background.Background;
import dev.lambdaurora.spruceui.background.SimpleColorBackground;
import dev.lambdaurora.spruceui.util.ColorUtil;
import dev.lambdaurora.spruceui.widget.SpruceWidget;
import io.github.queerbric.pride.PrideFlag;
import io.github.queerbric.pride.PrideFlagShape;
import io.github.queerbric.pride.PrideFlagShapes;
import io.github.queerbric.pride.PrideFlags;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Text;
import net.minecraft.resources.Identifier;
import org.joml.Matrix4f;
import org.joml.Vector4f;

import java.util.Random;

/**
 * Displays a pride flag.
 * <p>
 * If you have an issue with this, I don't care.
 *
 * @author LambdAurora
 * @version 3.1.3
 * @since 2.1.0
 */
public class RandomPrideFlagBackground implements Background {
	private static final Background SECOND_LAYER = new SimpleColorBackground(0xe0101010);
	private static final IntList DEFAULT_RAINBOW_COLORS = IntList.of(
			0xffff0018, 0xffffa52c, 0xffffff41, 0xff008018, 0xff0000f9, 0xff86007d
	);
	private static final PrideFlagShape PROGRESS = PrideFlagShapes.get(Identifier.of("pride", "progress"));
	private static final Random RANDOM = new Random();

	private final PrideFlag flag;
	private final boolean nuhUh;

	RandomPrideFlagBackground(PrideFlag flag, boolean nuhUh) {
		this.flag = flag;
		this.nuhUh = nuhUh;
	}

	private IntList getColors() {
		return this.nuhUh ? DEFAULT_RAINBOW_COLORS : this.flag.getColors();
	}

	@Override
	public void render(GuiGraphics graphics, SpruceWidget widget, int vOffset, int mouseX, int mouseY, float delta) {
		int x = widget.getX();
		int y = widget.getY();
		int width = widget.getWidth();
		int height = widget.getHeight();

		RenderSystem.setShader(GameRenderer::getPositionColorShader);
		if (this.nuhUh || this.flag.getShape() == PrideFlagShapes.get(Identifier.of("pride", "horizontal_stripes"))) {
			var model = graphics.matrixStack().peek().model();
			var tessellator = Tessellator.getInstance();
			var vertices = tessellator.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

			var colors = this.getColors();

			float partHeight = height / (colors.size() - 1.f);

			// First one
			float rightY = y;
			float leftY = y;

			int[] color = ColorUtil.unpackARGBColor(colors.getInt(0));
			vertex(vertices, model, x + width, rightY + partHeight, 0).color(color[0], color[1], color[2], color[3]);
			vertex(vertices, model, x + width, rightY, 0).color(color[0], color[1], color[2], color[3]);
			vertex(vertices, model, x, leftY, 0).color(color[0], color[1], color[2], color[3]);

			rightY += partHeight;

			for (int i = 1; i < colors.size() - 1; i++) {
				color = ColorUtil.unpackARGBColor(colors.getInt(i));

				vertex(vertices, model, x + width, rightY + partHeight, 0).color(color[0], color[1], color[2], color[3]);
				vertex(vertices, model, x + width, rightY, 0).color(color[0], color[1], color[2], color[3]);
				vertex(vertices, model, x, leftY, 0).color(color[0], color[1], color[2], color[3]);

				vertex(vertices, model, x + width, rightY + partHeight, 0).color(color[0], color[1], color[2], color[3]);
				vertex(vertices, model, x, leftY, 0).color(color[0], color[1], color[2], color[3]);
				vertex(vertices, model, x, leftY + partHeight, 0).color(color[0], color[1], color[2], color[3]);

				rightY += partHeight;
				leftY += partHeight;
			}

			// Last one
			color = ColorUtil.unpackARGBColor(colors.getInt(colors.size() - 1));
			vertex(vertices, model, x + width, rightY, 0).color(color[0], color[1], color[2], color[3]);
			vertex(vertices, model, x, leftY, 0).color(color[0], color[1], color[2], color[3]);
			vertex(vertices, model, x, y + height, 0).color(color[0], color[1], color[2], color[3]);

			MeshData builtBuffer = vertices.build();
			if (builtBuffer != null) {
				BufferUploader.drawWithShader(builtBuffer);
			}
			tessellator.clear();
		} else {
			this.flag.render(graphics.matrixStack(), x, y, widget.getWidth(), widget.getHeight());
		}

		SECOND_LAYER.render(graphics, widget, vOffset, mouseX, mouseY, delta);

		if (this.nuhUh) {
			var text = Text.literal("Nuh uh, you're not going to remove this, try harder :3c");
			var font = Minecraft.getInstance().font;
			var lines = font.wrapLines(text, width - 8);

			int startY = y + height - 24 - lines.size() * (font.lineHeight + 2);

			for (var line : lines) {
				graphics.drawCenteredShadowedText(font, line, x + width / 2, startY, 0xffff0000);
				startY += font.lineHeight + 2;
			}
		}
	}

	/**
	 * Returns a random pride flag as background.
	 *
	 * @return the background
	 */
	public static Background random() {
		var flag = PrideFlags.getRandomFlag(RANDOM);
		boolean nuhUh = flag == null || (flag.getShape() != PROGRESS && areColorsSpoofed(flag.getColors()));

		return new RandomPrideFlagBackground(flag, nuhUh);
	}

	private static boolean areColorsSpoofed(IntList colors) {
		if (colors.size() < 2) {
			return true;
		} else {
			int maxDist = 0;

			for (int colorA : colors) {
				for (int colorB : colors) {
					int dist = colorDist(colorA, colorB);

					if (dist > maxDist) {
						maxDist = dist;
					}
				}
			}

			return maxDist < 10;
		}
	}

	private static int colorDist(int a, int b) {
		// https://en.wikipedia.org/wiki/Color_difference#sRGB
		float r = (ColorUtil.argbUnpackRed(a) + ColorUtil.argbUnpackRed(b)) / 2.f;
		int deltaR = ColorUtil.argbUnpackRed(a) - ColorUtil.argbUnpackRed(b);
		int deltaG = ColorUtil.argbUnpackGreen(a) - ColorUtil.argbUnpackGreen(b);
		int deltaB = ColorUtil.argbUnpackBlue(a) - ColorUtil.argbUnpackBlue(b);

		return (int) Math.sqrt((2 + r / 256.f) * deltaR * deltaR + 4 * deltaG * deltaG + (2 + (255 - r) / 256) * deltaB * deltaB);
	}

	private static VertexConsumer vertex(BufferBuilder builder, Matrix4f matrix, float x, float y, float z) {
		Vector4f vector4f = matrix.transform(new Vector4f(x, y, z, 1.0f));
		return builder.addVertex(vector4f.x(), vector4f.y(), vector4f.z());
	}
}
