package xzeroair.trinkets.items.base;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.util.interfaces.IDescriptionInterface;
import xzeroair.trinkets.util.interfaces.IsModelLoaded;

public class ItemBase extends Item implements IsModelLoaded, IDescriptionInterface {
	public ItemBase(String name) {
		this.setTranslationKey(name);
		this.setRegistryName(name);
		this.setCreativeTab(Trinkets.trinketstab);
		ModItems.crafting.ITEMS.add(this);
	}
	@Override
	public void registerModels() {
		Trinkets.proxy.registerItemRenderer(this, 0, "inventory");
	}
	@Override
	public boolean hasDiscription(ItemStack stack) {
		return false;
	}
}
