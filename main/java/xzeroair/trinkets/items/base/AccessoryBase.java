package xzeroair.trinkets.items.base;

import java.text.DecimalFormat;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.api.TrinketHelper.SlotInformation;
import xzeroair.trinkets.api.TrinketHelper.SlotInformation.ItemHandlerType;
import xzeroair.trinkets.attributes.UpdatingAttribute;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.Trinket.TrinketProperties;
import xzeroair.trinkets.traits.abilities.base.ItemAbilityProvider;
import xzeroair.trinkets.traits.abilities.interfaces.IAbilityInterface;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ConfigHelper;
import xzeroair.trinkets.util.config.ConfigHelper.AttributeEntry;
import xzeroair.trinkets.util.helpers.AttributeHelper;
import xzeroair.trinkets.util.helpers.TranslationHelper;
import xzeroair.trinkets.util.interfaces.IAccessoryInterface;
import xzeroair.trinkets.util.interfaces.IsModelLoaded;

public abstract class AccessoryBase extends Item implements IsModelLoaded, IAccessoryInterface, ItemAbilityProvider {

	protected UUID uuid;

	protected String[] attributeConfig;

	public AccessoryBase(String name) {
		this.setTranslationKey(name);
		this.setRegistryName(name);
		this.setMaxStackSize(1);
		this.setCreativeTab(Trinkets.trinketstab);
		attributeConfig = new String[0];
	}

	public AccessoryBase setAttributeConfig(String[] attributeConfig) {
		this.attributeConfig = attributeConfig;
		return this;
	}

	//	@Override
	//	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {
	//		//		return super.initCapabilities(stack, nbt);
	//		TrinketProperties properties = new TrinketProperties(stack);
	//		properties.loadFromNBT(nbt);
	//		return new CapabilityProviderBase<>(
	//				Capabilities.ITEM_TRINKET,
	//				properties
	//		);
	//	}

	@Override
	public void initAbilities(ItemStack stack, EntityLivingBase entity, List<IAbilityInterface> abilities) {

	}

	protected void initAttributes(String[] attributeConfig, EntityLivingBase entity) {
		if ((attributeConfig != null) && (attributeConfig.length > 0)) {
			for (String entry : attributeConfig) {
				AttributeEntry attributeShell = ConfigHelper.getAttributeEntry(entry);
				if ((attributeShell != null) && (this.getUUID() != null)) {
					final String name = attributeShell.getAttribute();
					final double amount = attributeShell.getAmount();
					final int operation = attributeShell.getOperation();
					final boolean isSaved = attributeShell.isSaved();
					if (!(this instanceof TrinketRaceBase)) {
						UpdatingAttribute attribute = new UpdatingAttribute(this.getTranslationKey() + "." + name, uuid, name).setAmount(amount).setOperation(operation).setSavedInNBT(isSaved);
						attribute.addModifier(entity, amount, operation);
					}
				}
			}
		}
	}

	@Override
	public boolean hasCustomEntity(ItemStack stack) {
		return super.hasCustomEntity(stack);
	}

	@Override
	public Entity createEntity(World world, Entity location, ItemStack itemstack) {
		return super.createEntity(world, location, itemstack);
	}

	@Override
	public boolean isShield(ItemStack stack, EntityLivingBase entity) {
		return super.isShield(stack, entity);
	}

	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity) {
		return super.isValidArmor(stack, armorType, entity);
	}

	@Override
	public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
		return super.onDroppedByPlayer(item, player);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem) {
		return super.onEntityItemUpdate(entityItem);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return super.getItemStackDisplayName(stack);
	}

	@Override
	public boolean isDamageable() {
		return false;
	}

	// ITrinketInterface

	@Override
	public int getSlot(ItemStack stack) {
		return Capabilities.getTrinketProperties(stack, -1, (prop, slot) -> prop.getSlot());
	}

	@Override
	public String getItemHandler(ItemStack stack) {
		return Capabilities.getTrinketProperties(stack, ItemHandlerType.NONE.getName(), (prop, slot) -> prop.getSlotInfo().getHandler());
	}

	@Override
	public String getAccessoryType() {
		return "trinket";//BaubleType.bauble_type;
	}

	//
	public UUID getUUID() {
		return uuid;
	}

	protected void setUUID(String uuid) {
		this.uuid = UUID.fromString(uuid);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		super.onUpdate(stack, world, entity, itemSlot, isSelected);
		//		Capabilities.getTrinketProperties(stack, cap -> {
		//			cap.onUpdate(stack, world, entity, itemSlot, isSelected);
		//		});
		Capabilities.getTrinketProperties(stack, TrinketProperties::onUpdate);
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		super.onCreated(stack, world, player);
		try {
			if (player == null) {
				return;
			}
			if (this.getTagCompoundSafe(stack).getString("crafter.id").isEmpty()) {
				if (player.getUniqueID() != null) {
					this.getTagCompoundSafe(stack).setString("crafter.id", player.getUniqueID().toString());
				}
			}
			if (this.getTagCompoundSafe(stack).getString("crafter.name").isEmpty()) {
				this.getTagCompoundSafe(stack).setString("crafter.name", player.getDisplayNameString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCrafter(ItemStack stack) {
		if (!this.getTagCompoundSafe(stack).getString("crafter.name").isEmpty()) {
			return this.getTagCompoundSafe(stack).getString("crafter.name");
		}
		return "";
	}

	public String getCrafterID(ItemStack stack) {
		if (!this.getTagCompoundSafe(stack).getString("crafter.id").isEmpty()) {
			return this.getTagCompoundSafe(stack).getString("crafter.id");
		}
		return "";
	}

	/*
	 * use the non player specific method for entities other then the player
	 */
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		this.onEntityArmorTick(world, player, itemStack);
	}

	/*
	 * Not Player Specific ArmorTick
	 */
	@Override
	public void onEntityArmorTick(World world, EntityLivingBase entity, ItemStack stack) {
	}

	@Override
	public void onAccessoryEquipped(ItemStack stack, EntityLivingBase entity) {
		final boolean isClient = entity.world.isRemote;
		Capabilities.getTrinketProperties(stack, cap -> {
			if (!isClient) {
				cap.itemEquipped(stack, entity);
			} else {
				entity.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, .75F, 1.9f);
			}
		});
	}

	@Override
	public void eventLivingUpdateTick(ItemStack stack, EntityLivingBase entity) {
		Capabilities.getTrinketProperties(stack, cap -> {
			cap.onEntityTick(stack, entity);
		});
	}

	@Override
	public void eventPlayerTick(ItemStack stack, EntityPlayer player) {
		//		this.initAbilities(player);
		this.initAttributes(attributeConfig, player);
		Capabilities.getTrinketProperties(stack, cap -> {
			cap.onEntityTick(stack, player);
		});
	}

	@Override
	public void onAccessoryUnequipped(ItemStack stack, EntityLivingBase entity) {
		final boolean isClient = entity.world.isRemote;
		Capabilities.getTrinketProperties(stack, cap -> {
			if (!isClient) {
				cap.itemUnequipped(stack, entity);
			} else {
				entity.playSound(SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND, .75F, 2f);
			}
		});
		AttributeHelper.removeAttributesByUUID(entity, this.getUUID());
	}

	@Override
	public void eventPlayerLogout(ItemStack stack, EntityPlayer player) {
		AttributeHelper.removeAttributesByUUID(player, this.getUUID());
	}

	// API

	// API END

	@Override
	public boolean canUnequipAccessory(ItemStack stack, EntityLivingBase player) {
		if ((EnchantmentHelper.hasBindingCurse(stack) == true) && (player.getHeldItem(EnumHand.MAIN_HAND).getItem() != Item.getItemById(399))) {
			return false;
		}
		if ((player instanceof EntityPlayer) && ((EntityPlayer) player).capabilities.isCreativeMode) {
			return true;
		}
		return true;
	}

	@Override
	public NBTTagCompound getNBTShareTag(ItemStack stack) {
		return Capabilities.getTrinketProperties(stack, super.getNBTShareTag(stack), (prop, tag) -> {
			if (tag == null) {
				tag = new NBTTagCompound();
				stack.setTagCompound(tag);
			}
			prop.saveToNBT(tag);
			return stack.getTagCompound();
		});
	}

	@Override
	public void readNBTShareTag(ItemStack stack, NBTTagCompound nbt) {
		super.readNBTShareTag(stack, nbt);
		Capabilities.getTrinketProperties(stack, prop -> prop.loadFromNBT(nbt));
	}

	public NBTTagCompound getTagCompoundSafe(ItemStack stack) {
		if (stack.getTagCompound() == null) {
			stack.setTagCompound(new NBTTagCompound());
		}
		return stack.getTagCompound();
	}

	@Override
	public boolean canEquipAccessory(ItemStack stack, EntityLivingBase player) {
		return !TrinketHelper.AccessoryCheck(player, this);
	}

	public static final DecimalFormat DECIMALFORMAT = new DecimalFormat("#.##");

	@SideOnly(Side.CLIENT)
	protected String customItemInformation(ItemStack stack, World world, ITooltipFlag flagIn, int index, String translation) {
		final TranslationHelper helper = TranslationHelper.INSTANCE;
		return helper.formatAddVariables(translation);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flagIn) {
		super.addInformation(stack, world, tooltip, flagIn);
		if (world == null) {
			return;
		}
		if (TrinketsConfig.CLIENT.debug.showID) {
			final TrinketProperties prop = Capabilities.getTrinketProperties(stack);
			if (prop != null) {
				final SlotInformation info = prop.getSlotInfo();
				tooltip.add("§6" + info.getHandler() + "§r:§f" + info.getSlot() + "§r");
			}
		}

		final TranslationHelper helper = TranslationHelper.INSTANCE;
		//SharedMonsterAttributes.readAttributeModifierFromNBT(compound);
		//		final String attribute = new TextComponentString("attribute.name.generic.maxHealth").getFormattedText();
		//		tooltip.add("§6" + attribute);
		// Add Attributes through the Capability, and get them from that, and then render them here, can do that instead of Adding them through the constructor
		// Also allows for more dynamic attributes, can get the attributes list on equip or something
		// Make sure not to accidentally leave attributes on the player
		// User For Loops instead of Streams, create a For loop predicate getter?

		for (int i = 1; i < 10; i++) {
			final int index = i;
			final String string = helper.getLangTranslation(stack.getTranslationKey() + ".tooltip" + i, lang -> this.customItemInformation(stack, world, flagIn, index, lang));
			if (!helper.isStringEmpty(string)) {
				tooltip.add(
						string
				);
			}
		}
		final TextComponentTranslation ctrl = new TextComponentTranslation(Reference.MODID + ".holdctrl");
		final boolean tanEnabled = (Trinkets.ToughAsNails && TrinketsConfig.compat.toughasnails);
		final boolean sdEnabled = (Trinkets.SimpleDifficulty && TrinketsConfig.compat.simpledifficulty);
		final boolean faEnabled = Trinkets.FirstAid;
		final boolean evEnabled = Trinkets.EnhancedVisuals && TrinketsConfig.compat.enhancedvisuals;
		final String TAN = !(tanEnabled || sdEnabled) ? "" : helper.getLangTranslation(stack.getTranslationKey() + ".compat.tan", lang -> this.customItemInformation(stack, world, flagIn, 11, lang));
		final String FA = !faEnabled ? "" : helper.getLangTranslation(stack.getTranslationKey() + ".compat.firstaid", lang -> this.customItemInformation(stack, world, flagIn, 12, lang));
		final String EV = !evEnabled ? "" : helper.getLangTranslation(stack.getTranslationKey() + ".compat.enhancedvisuals", lang -> this.customItemInformation(stack, world, flagIn, 13, lang));
		if (GuiScreen.isCtrlKeyDown()) {
			if (!helper.isStringEmpty(TAN)) {
				final String modifier = sdEnabled ? " (Simple Difficulty)" : tanEnabled ? " (Tough as Nails)" : "";
				tooltip.add(
						TAN + helper.gold + modifier
				);
			}
			if (!helper.isStringEmpty(FA)) {
				tooltip.add(
						FA + helper.gold + " (First Aid)"
				);
			}
			if (!helper.isStringEmpty(EV)) {
				tooltip.add(
						EV + helper.gold + " (Enhanced Visuals)"
				);
			}
		} else {
			if ((!helper.isStringEmpty(TAN)) ||
					(!helper.isStringEmpty(EV)) ||
					(!helper.isStringEmpty(FA))

			) {
				tooltip.add(helper.reset + "" + helper.dGray + ctrl.getFormattedText());
			}
		}

		try {
			final TextComponentTranslation shift = new TextComponentTranslation(Reference.MODID + ".holdshift");
			if ((attributeConfig != null) && (attributeConfig.length > 0)) {
				tooltip.add(helper.reset + "" + helper.dGray + shift.getFormattedText());
				if (GuiScreen.isShiftKeyDown()) {
					for (String entry : attributeConfig) {
						AttributeEntry attributeShell = ConfigHelper.getAttributeEntry(entry);
						if (attributeShell != null) {
							EntityPlayer player = Minecraft.getMinecraft().player;
							final String name = attributeShell.getAttribute();
							final double amount = attributeShell.getAmount();
							final int operation = attributeShell.getOperation();
							if ((player != null)) {
								if ((player.getAttributeMap().getAttributeInstanceByName(name) != null)) {
									double d0 = amount;
									double d1;
									boolean flag = false;
									if ((operation != 1) && (operation != 2)) {
										d1 = d0;
									} else {
										d1 = d0 * 100.0D;
									}
									final TextComponentTranslation AttrName = new TextComponentTranslation("attribute.name." + name);
									if (flag) {
										final TextComponentTranslation never = new TextComponentTranslation("attribute.modifier.equals." + operation, DECIMALFORMAT.format(d1), AttrName.getFormattedText());
										tooltip.add(" " + never.getFormattedText());
										//I18n.translateToLocalFormatted("attribute.modifier.equals." + operation, DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + name)));
									} else if (d0 > 0.0D) {
										final TextComponentTranslation addition = new TextComponentTranslation("attribute.modifier.plus." + operation, DECIMALFORMAT.format(d1), AttrName.getFormattedText());
										addition.getStyle().setColor(TextFormatting.BLUE);
										String s = addition.getFormattedText();
										tooltip.add(" " + s);
										// I18n.translateToLocalFormatted("attribute.modifier.plus." + operation, DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + name)));
									} else if (d0 < 0.0D) {
										d1 = d1 * -1.0D;
										final TextComponentTranslation subtraction = new TextComponentTranslation("attribute.modifier.take." + operation, DECIMALFORMAT.format(d1), AttrName.getFormattedText());
										subtraction.getStyle().setColor(TextFormatting.RED);
										String s = subtraction.getFormattedText();
										tooltip.add(" " + s);
										//I18n.translateToLocalFormatted("attribute.modifier.take." + operation, DECIMALFORMAT.format(d1), I18n.translateToLocal("attribute.name." + name)));
									}
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}

	/*
	 * Don't Edit Below this
	 */

	@Override
	public String getTranslationKey(ItemStack stack) {
		// Name + Item Damage equals the Lang File Name
		return super.getTranslationKey() + "." + stack.getItemDamage();
	}

	@Override
	public EnumRarity getRarity(ItemStack stack) {
		//		return super.getRarity(stack);
		return EnumRarity.RARE;
	}
}