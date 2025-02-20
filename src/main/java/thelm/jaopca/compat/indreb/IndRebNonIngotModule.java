package thelm.jaopca.compat.indreb;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import thelm.jaopca.api.JAOPCAApi;
import thelm.jaopca.api.config.IDynamicSpecConfig;
import thelm.jaopca.api.helpers.IMiscHelper;
import thelm.jaopca.api.ingredients.CompoundIngredientObject;
import thelm.jaopca.api.materials.IMaterial;
import thelm.jaopca.api.materials.MaterialType;
import thelm.jaopca.api.modules.IModule;
import thelm.jaopca.api.modules.IModuleData;
import thelm.jaopca.api.modules.JAOPCAModule;
import thelm.jaopca.utils.ApiImpl;
import thelm.jaopca.utils.MiscHelper;

@JAOPCAModule(modDependencies = "indreb")
public class IndRebNonIngotModule implements IModule {

	private static final Set<String> BLACKLIST = new TreeSet<>(Arrays.asList(
			"coal", "diamond", "emerald", "lapis", "quartz"));

	private Map<IMaterial, IDynamicSpecConfig> configs;

	@Override
	public String getName() {
		return "indreb_non_ingot";
	}

	@Override
	public Set<MaterialType> getMaterialTypes() {
		return EnumSet.of(MaterialType.GEM, MaterialType.CRYSTAL);
	}

	@Override
	public Set<String> getDefaultMaterialBlacklist() {
		return BLACKLIST;
	}

	@Override
	public void defineMaterialConfig(IModuleData moduleData, Map<IMaterial, IDynamicSpecConfig> configs) {
		this.configs = configs;
	}

	@Override
	public void onCommonSetup(IModuleData moduleData, FMLCommonSetupEvent event) {
		JAOPCAApi api = ApiImpl.INSTANCE;
		IndRebHelper helper = IndRebHelper.INSTANCE;
		IMiscHelper miscHelper = MiscHelper.INSTANCE;
		ResourceLocation deepslateOreLocation = new ResourceLocation("forge:ores_in_ground/deepslate");
		ResourceLocation netherrackOreLocation = new ResourceLocation("forge:ores_in_ground/netherrack");
		ResourceLocation endstoneOreLocation = new ResourceLocation("forge:ores_in_ground/end_stone");
		Item deepslateDust = ForgeRegistries.ITEMS.getValue(new ResourceLocation("indreb:deepslate_dust"));
		Item netherrack = Items.NETHERRACK;
		Item endstone = Items.END_STONE;
		for(IMaterial material : moduleData.getMaterials()) {
			ResourceLocation oreLocation = miscHelper.getTagLocation("ores", material.getName());
			ResourceLocation materialLocation = miscHelper.getTagLocation(material.getType().getFormName(), material.getName());

			IDynamicSpecConfig config = configs.get(material);
			String configByproduct = config.getDefinedString("indreb.byproduct", "indreb:stone_dust",
					s->ForgeRegistries.ITEMS.containsKey(new ResourceLocation(s)), "The byproduct material to output in Industrial Reborn's crushing.");
			Item byproduct = ForgeRegistries.ITEMS.getValue(new ResourceLocation(configByproduct));

			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "indreb.default_ore_to_material."+material.getName()),
					CompoundIngredientObject.difference(new Object[] {
							oreLocation,
							deepslateOreLocation, netherrackOreLocation, endstoneOreLocation,
					}), 1,
					materialLocation, 2,
					byproduct, 1, 50F,
					180, 8, 0.2F);
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "indreb.deepslate_ore_to_material."+material.getName()),
					CompoundIngredientObject.intersection(new Object[] {
							oreLocation, deepslateOreLocation,
					}), 1,
					materialLocation, 2,
					deepslateDust, 1, 50F,
					180, 8, 0.2F);
			helper.registerCrushingRecipe(
					new ResourceLocation("jaopca", "indreb.netherrack_ore_to_material."+material.getName()),
					CompoundIngredientObject.intersection(new Object[] {
							oreLocation, netherrackOreLocation,
					}), 1,
					materialLocation, 2,
					netherrack, 1, 75F,
					180, 8, 0.2F);
			if(api.getItemTags().contains(endstoneOreLocation)) {
				helper.registerCrushingRecipe(
						new ResourceLocation("jaopca", "indreb.end_stone_ore_to_material."+material.getName()),
						CompoundIngredientObject.intersection(new Object[] {
								oreLocation, endstoneOreLocation,
						}), 1,
						materialLocation, 2,
						endstone, 1, 75F,
						180, 8, 0.2F);
			}
		}
	}
}
