package xzeroair.trinkets.util.handlers.mana;

import java.util.Random;

import net.minecraft.util.math.BlockPos;

public class ManaSphere {

	private final BlockPos center;
	private final float radius;

	private float currentMana = 0;

	public ManaSphere(BlockPos center, float radius) {
		this.center = center;
		this.radius = radius;

	}

	public float getCurrentMana() {
		return currentMana;
	}

	public void setCurrentMana(float currentMana) {
		this.currentMana = currentMana;
	}

	public BlockPos getCenter() {
		return center;
	}

	public float getRadius() {
		return radius;
	}

	public static boolean isCenterChunk(Long seed, int chunkX, int chunkZ) {
		final Random random = new Random(seed + (chunkX * 1766557063L) + (chunkZ *21766558031L));
		return random.nextFloat() < 0.03f;
	}

	public static float getRadius(Long seed, int chunkX, int chunkZ) {
		final Random random = new Random(seed + (chunkX * 31766435083L) + (chunkZ *655987873L));
		return (random.nextFloat() * 40) + 20;
	}

	public static int getRandomYOffset(Long seed, int chunkX, int chunkZ) {
		final Random random = new Random(seed + (chunkX * 3556692499L) + (chunkZ *2998604447L));
		return random.nextInt(60) + 40;
	}

}
