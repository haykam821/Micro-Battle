package io.github.haykam821.microbattle.game.kit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import xyz.nucleoid.plasmid.registry.TinyRegistry;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class Kit {
	public static final TinyRegistry<Kit> REGISTRY = TinyRegistry.newStable();

	private final int baseColor;
	private final int secondaryColor;

	public Kit(int baseColor, int secondaryColor) {
		this.baseColor = baseColor;
		this.secondaryColor = secondaryColor;
	}

	protected String[] getNeutrals() {
		return new String[0];
	}

	protected String[] getAdvantages() {
		return new String[0];
	}

	protected String[] getDisadvantages() {
		return new String[0];
	}

	private Text getTooltip() {
		MutableText text = new LiteralText("");
		text.append(new LiteralText("• Defeat the other players!").formatted(Formatting.GRAY));

		for (String line : this.getNeutrals()) {
			text.append(new LiteralText("\n• " + line).formatted(Formatting.GRAY));
		}
		for (String line : this.getAdvantages()) {
			text.append(new LiteralText("\n+ " + line).formatted(Formatting.GREEN));
		}
		for (String line : this.getDisadvantages()) {
			text.append(new LiteralText("\n- " + line).formatted(Formatting.RED));
		}

		return text;
	}

	private Text getName() {
		Identifier id = Kit.REGISTRY.getIdentifier(this);
		return new TranslatableText("kit." + id.getNamespace() + "." + id.getPath());
	}

	private Text getHoverableName() {
		return this.getName().shallowCopy().styled(style -> {
			return style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, this.getTooltip()));
		});
	}

	public Text getReceivedText() {
		return new TranslatableText("text.microbattle.kit_received", this.getHoverableName()).formatted(Formatting.GRAY);
	}

	private ItemStack createArmorStack(Item item, String type, boolean secondary) {
		return ItemStackBuilder.of(item)
			.addEnchantment(Enchantments.BINDING_CURSE, 1)
			.setColor(secondary ? this.secondaryColor : this.baseColor)
			.setName(new TranslatableText("text.microbattle.team_armor." + type, this.getName()))
			.build();
	}

	private List<ItemStack> getArmorStacks() {
		List<ItemStack> armorStacks = new ArrayList<>();
		armorStacks.add(this.createArmorStack(Items.LEATHER_HELMET, "helmet", true));
		armorStacks.add(this.createArmorStack(Items.LEATHER_CHESTPLATE, "chestplate", false));
		armorStacks.add(this.createArmorStack(Items.LEATHER_LEGGINGS, "leggings", false));
		armorStacks.add(this.createArmorStack(Items.LEATHER_BOOTS, "boots", true));
		return armorStacks;
	}

	public void tick(PlayerEntry entry) {
		return;
	}

	protected ItemStack getMainWeaponStack() {
		return unbreakableStack(Items.STONE_SWORD);
	}

	protected ItemStack getPickaxeToolStack() {
		return unbreakableStack(Items.STONE_PICKAXE);
	}

	protected ItemStack getAxeToolStack() {
		return unbreakableStack(Items.STONE_AXE);
	}

	protected ItemStack getShovelToolStack() {
		return unbreakableStack(Items.STONE_SHOVEL);
	}

	protected ItemStack getFoodStack() {
		return new ItemStack(Items.APPLE, 8);
	}

	protected void appendInitialStacks(List<ItemStack> stacks) {
		addIfNonNull(this::getMainWeaponStack, stacks);
		addIfNonNull(this::getPickaxeToolStack, stacks);
		addIfNonNull(this::getAxeToolStack, stacks);
		addIfNonNull(this::getShovelToolStack, stacks);
		addIfNonNull(this::getFoodStack, stacks);
	}

	protected StatusEffectInstance[] getStatusEffects() {
		return new StatusEffectInstance[0];
	}

	public final void applyInventory(ServerPlayerEntity player) {
		// Add status effects
		for (StatusEffectInstance effect : this.getStatusEffects()) {
			player.addStatusEffect(new StatusEffectInstance(effect.getEffectType(), effect.getDuration(), effect.getAmplifier(), true, false));
		}

		List<ItemStack> armorStacks = this.getArmorStacks();
		int index = 3;
		for (ItemStack stack : armorStacks) {
			player.inventory.armor.set(index, stack);
			index -= 1;
		}
	
		List<ItemStack> stacks = new ArrayList<>();
		this.appendInitialStacks(stacks);
		int slot = 0;
		for (ItemStack stack : stacks) {
			player.inventory.setStack(slot, stack);
			slot += 1;
		}

		// Update inventory
		player.currentScreenHandler.sendContentUpdates();
		player.playerScreenHandler.onContentChanged(player.inventory);
		player.updateCursorStack();
	}

	protected static ItemStack unbreakableStack(ItemConvertible item) {
		return ItemStackBuilder.of(item).setUnbreakable().build();
	}

	private static void addIfNonNull(Supplier<ItemStack> supplier, List<ItemStack> stacks) {
		ItemStack stack = supplier.get();
		if (stack != null) {
			stacks.add(stack);
		}
	}
}
