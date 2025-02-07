package thelm.jaopca.fluids;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.FluidAttributes;
import thelm.jaopca.api.fluids.IFluidFormSettings;
import thelm.jaopca.api.fluids.IMaterialFormFluid;
import thelm.jaopca.api.fluids.PlaceableFluid;
import thelm.jaopca.api.fluids.PlaceableFluidBlock;
import thelm.jaopca.api.forms.IForm;
import thelm.jaopca.api.materials.IMaterial;

public class JAOPCAFluid extends PlaceableFluid implements IMaterialFormFluid {

	private final IForm form;
	private final IMaterial material;
	protected final IFluidFormSettings settings;

	protected OptionalInt tickRate = OptionalInt.empty();
	protected OptionalDouble explosionResistance = OptionalDouble.empty();
	protected Optional<Boolean> canSourcesMultiply = Optional.empty();
	protected Optional<Boolean> canFluidBeDisplaced = Optional.empty();
	protected OptionalInt levelDecreasePerBlock = OptionalInt.empty();

	public JAOPCAFluid(IForm form, IMaterial material, IFluidFormSettings settings) {
		super(settings.getMaxLevelFunction().applyAsInt(material));
		this.form = form;
		this.material = material;
		this.settings = settings;
	}

	@Override
	public IForm getForm() {
		return form;
	}

	@Override
	public IMaterial getMaterial() {
		return material;
	}

	@Override
	public int getTickDelay(LevelReader world) {
		if(!tickRate.isPresent()) {
			tickRate = OptionalInt.of(settings.getTickRateFunction().applyAsInt(material));
		}
		return tickRate.getAsInt();
	}

	@Override
	protected float getExplosionResistance() {
		if(!explosionResistance.isPresent()) {
			explosionResistance = OptionalDouble.of(settings.getExplosionResistanceFunction().applyAsDouble(material));
		}
		return (float)explosionResistance.getAsDouble();
	}

	@Override
	protected boolean canConvertToSource() {
		if(!canSourcesMultiply.isPresent()) {
			canSourcesMultiply = Optional.of(settings.getCanSourcesMultiplyFunction().test(material));
		}
		return canSourcesMultiply.get();
	}

	@Override
	protected int getDropOff(LevelReader world) {
		if(!levelDecreasePerBlock.isPresent()) {
			levelDecreasePerBlock = OptionalInt.of(settings.getLevelDecreasePerBlockFunction().applyAsInt(material));
		}
		return levelDecreasePerBlock.getAsInt();
	}

	@Override
	protected FluidAttributes createAttributes() {
		return settings.getFluidAttributesCreator().create(this, settings);
	}

	@Override
	public Item getBucket() {
		return FluidFormType.INSTANCE.getMaterialFormInfo(form, material).getBucketItem();
	}

	@Override
	protected PlaceableFluidBlock getFluidBlock() {
		return (PlaceableFluidBlock)FluidFormType.INSTANCE.getMaterialFormInfo(form, material).getMaterialFormFluidBlock().asBlock();
	}

	@Override
	public FluidState getSourceState() {
		return defaultFluidState().setValue(levelProperty, maxLevel);
	}
}
