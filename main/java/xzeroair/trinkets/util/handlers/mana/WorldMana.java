package xzeroair.trinkets.util.handlers.mana;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.util.Constants;

public class WorldMana extends WorldSavedData {

	private static String NAME = "ManaData"; //needs to be unique add ModId?

	private final Map<ChunkPos, ManaSphere> spheres = new HashMap<>();
	private int ticker = 10;

	public WorldMana() {
		super(NAME);
	}

	public static WorldMana get(World world) {
		final MapStorage storage = world.getPerWorldStorage(); //per Demension
		//		world.getMapStorage(); All worlds
		WorldMana instance = (WorldMana) storage.getOrLoadData(WorldMana.class, NAME);

		if (instance == null) {
			instance = new WorldMana();
			storage.setData(NAME, instance);
		}
		return instance;
	}

	public float getManaInfluence(World world, BlockPos pos) {
		final ChunkPos chunkPos = new ChunkPos(pos);
		float mana = 0.0f;
		for (int dx = -4; dx <= 4; dx++) {
			for (int dz = -4; dz <= 4; dz++) {
				final ChunkPos cp = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
				final ManaSphere sphere = this.getOrCreateSphereAt(world, cp);
				if (sphere.getRadius() > 0) {
					final double distanceSq = pos.distanceSq(sphere.getCenter());
					if (distanceSq < (sphere.getRadius() * sphere.getRadius())) {
						final double distance = Math.sqrt(distanceSq);
						mana += (sphere.getRadius() - distance) / sphere.getRadius();

					}
				}
			}
		}
		return mana;
	}

	public float getManaStrength(World world, BlockPos pos) {
		final ChunkPos chunkPos = new ChunkPos(pos);
		float mana = 0.0f;
		for (int dx = -4; dx <= 4; dx++) {
			for (int dz = -4; dz <= 4; dz++) {
				final ChunkPos cp = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
				final ManaSphere sphere = this.getOrCreateSphereAt(world, cp);
				if (sphere.getRadius() > 0) {
					final double distanceSq = pos.distanceSq(sphere.getCenter());
					if (distanceSq < (sphere.getRadius() * sphere.getRadius())) {
						final double distance = Math.sqrt(distanceSq);
						final double influence = (sphere.getRadius() - distance) / sphere.getRadius();
						mana += influence * sphere.getCurrentMana();
					}
				}
			}
		}
		return mana;
	}

	public float extractMana(World world, BlockPos pos) {
		final float manaInfluence = this.getManaInfluence(world, pos);
		if (manaInfluence <= 0) {
			return 0;
		}
		final ChunkPos chunkPos = new ChunkPos(pos);
		float extracted = 0.0f;
		for (int dx = -4; dx <= 4; dx++) {
			for (int dz = -4; dz <= 4; dz++) {
				final ChunkPos cp = new ChunkPos(chunkPos.x + dx, chunkPos.z + dz);
				final ManaSphere sphere = this.getOrCreateSphereAt(world, cp);
				if (sphere.getRadius() > 0) {
					final double distanceSq = pos.distanceSq(sphere.getCenter());
					if (distanceSq < (sphere.getRadius() * sphere.getRadius())) {
						final double distance = Math.sqrt(distanceSq);
						double influence = (sphere.getRadius() - distance) / sphere.getRadius();
						float currentMana = sphere.getCurrentMana();
						if (influence > currentMana) {
							influence = currentMana;
						}
						currentMana -= influence;
						extracted += influence;
						sphere.setCurrentMana(currentMana);
						this.markDirty();
					}
				}
			}
		}
		return extracted;
	}

	public void tick(World world) {
		ticker--;
		if (ticker > 0) {
			return;
		}
		ticker = 10;
		this.growMana(world);
		this.sendMana(world);
	}

	private void growMana(World world) {
		for (final Map.Entry<ChunkPos, ManaSphere> entry : spheres.entrySet()) {
			final ManaSphere sphere = entry.getValue();
			if (sphere.getRadius() > 0) {
				if (world.isBlockLoaded(sphere.getCenter())) {
					float currentMana = sphere.getCurrentMana();
					currentMana += 0.01f;
					if (currentMana >= 5) {
						currentMana = 5;
					}
					sphere.setCurrentMana(currentMana);
					this.markDirty();
				}
			}
		}
	}

	private void sendMana(World world) {
		for (final EntityPlayer player : world.playerEntities) {
			final float manaStrength = this.getManaStrength(world, player.getPosition());
			final float maxInfluence = this.getManaInfluence(world, player.getPosition());
			//			System.out.println(manaStrength);
			//			final ManaStats playerMana = Capabilities.getEntityMana(player);
			//			NetworkHandler.INSTANCE.sendTo(new PacketSendMana(manaStrength,  maxInfluence, playerMana.getMana()), (EntityPlayerMP) player);
		}
	}

	private ManaSphere getOrCreateSphereAt(World world, ChunkPos cp) {
		ManaSphere sphere = spheres.get(cp);
		if (sphere == null) {
			final BlockPos center = cp.getBlock(8, ManaSphere.getRandomYOffset(world.getSeed(), cp.x, cp.z), 8);
			float radius = 0;
			if (ManaSphere.isCenterChunk(world.getSeed(), cp.x, cp.z)) {
				radius = ManaSphere.getRadius(world.getSeed(), cp.x, cp.z);
			}
			sphere = new ManaSphere(center, radius);
			spheres.put(cp, sphere);
			//			System.out.println(center);
			this.markDirty();
		}
		return sphere;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		final NBTTagList list = nbt.getTagList("spheres", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < list.tagCount(); i++) {
			final NBTTagCompound sphereNBT = list.getCompoundTagAt(i);
			final ChunkPos pos = new ChunkPos(sphereNBT.getInteger("cx"), sphereNBT.getInteger("cz"));
			final ManaSphere sphere = new ManaSphere(new BlockPos(sphereNBT.getInteger("posx"), sphereNBT.getInteger("posy"), sphereNBT.getInteger("posz")), sphereNBT.getFloat("radius"));
			sphere.setCurrentMana(sphereNBT.getFloat("mana"));
			spheres.put(pos, sphere);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		final NBTTagList list = new NBTTagList();
		for (final Map.Entry<ChunkPos, ManaSphere> entry : spheres.entrySet()) {
			final NBTTagCompound sphereNBT = new NBTTagCompound();
			final ChunkPos pos = entry.getKey();
			final ManaSphere sphere = entry.getValue();
			sphereNBT.setInteger("cx", pos.x);
			sphereNBT.setInteger("cz", pos.z);
			sphereNBT.setInteger("posx", sphere.getCenter().getX());
			sphereNBT.setInteger("posy", sphere.getCenter().getY());
			sphereNBT.setInteger("posz", sphere.getCenter().getZ());
			sphereNBT.setFloat("radius", sphere.getRadius());
			sphereNBT.setFloat("mana", sphere.getCurrentMana());
			list.appendTag(sphereNBT);
		}
		compound.setTag("spheres", list);
		return compound;
	}

}
