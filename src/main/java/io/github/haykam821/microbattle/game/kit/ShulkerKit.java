package io.github.haykam821.microbattle.game.kit;

import java.util.Collection;
import java.util.List;

import io.github.haykam821.microbattle.game.PlayerEntry;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import xyz.nucleoid.plasmid.util.ItemStackBuilder;

public class ShulkerKit extends ArcherKit {
	public ShulkerKit(PlayerEntry entry) {
		super(KitTypes.SHULKER, entry);
	}

	@Override
	protected int getBaseColor() {
		return DyeColor.MAGENTA.getFireworkColor();
	}

	@Override
	protected int getSecondaryColor() {
		return DyeColor.MAGENTA.getFireworkColor();
	}

	@Override
	protected String[] getAdvantages() {
		return new String[] {
			"Your shulker arrows give others levitation",
		};
	}

	@Override
	protected String[] getDisadvantages() {
		return new String[] {
			"You can only hold one arrow at a time",
			"Your arrows restock slowly",
		};
	}

	@Override
	protected int getArrowRestockDelay() {
		return 20 * 10;
	}

	@Override
	protected int getMaxArrows() {
		return 1;
	}

	@Override
	protected ItemStack getArrowStack() {
		ItemStack stack = ItemStackBuilder.of(Items.TIPPED_ARROW)
			.setName(Text.translatable("item.microbattle.shulker_arrow"))
			.build();

		StatusEffectInstance effect = new StatusEffectInstance(StatusEffects.LEVITATION, 3 * 20);
		Collection<StatusEffectInstance> effects = List.of(effect);

		PotionUtil.setCustomPotionEffects(stack, effects);
		stack.getNbt().putInt("CustomPotionColor", 0xCEFFFF);

		return stack;
	}

	protected ItemStack getFoodStack() {
		return new ItemStack(Items.CHORUS_FRUIT, 8);
	}

	@Override
	public SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SHULKER_DEATH;
	}

	@Override
	public SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SHULKER_HURT;
	}
}
