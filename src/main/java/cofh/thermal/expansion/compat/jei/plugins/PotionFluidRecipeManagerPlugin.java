package cofh.thermal.expansion.compat.jei.plugins;

import cofh.core.common.fluid.PotionFluid;
import cofh.lib.common.fluid.FluidIngredient;
import cofh.thermal.core.util.managers.machine.BottlerRecipeManager;
import cofh.thermal.core.util.recipes.machine.BottlerRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static cofh.core.init.CoreFluids.POTION_FLUID;
import static cofh.lib.util.Constants.BOTTLE_VOLUME;
import static cofh.lib.util.constants.ModIds.ID_THERMAL;
import static cofh.thermal.expansion.compat.jei.TExpJeiRecipeTypes.BOTTLER_TYPE;

public class PotionFluidRecipeManagerPlugin implements IRecipeManagerPlugin {

    public static final PotionFluidRecipeManagerPlugin INSTANCE = new PotionFluidRecipeManagerPlugin();

    private PotionFluidRecipeManagerPlugin() {

    }

    @Override
    public <V> List<IRecipeType<?>> getRecipeTypes(IFocus<V> focus) {

        return List.of(BOTTLER_TYPE);
    }

    @Override
    public <T, V> List<T> getRecipes(IRecipeType<T> recipeType, IFocus<V> focus) {

        if (recipeType.equals(BOTTLER_TYPE)) {
            List<RecipeHolder<BottlerRecipe>> retList = new ArrayList<>();
            if (focus.getRole() == RecipeIngredientRole.INPUT) {
                var fluidIngredient = focus.getTypedValue().getIngredient(NeoForgeTypes.FLUID_STACK);
                if (fluidIngredient.isPresent() && fluidIngredient.get().getFluid() == POTION_FLUID.get()) {
                    FluidStack fluid = fluidIngredient.get();
                    if (fluid.has(DataComponents.POTION_CONTENTS)) {
                        retList.add(getDynamicBottlerPotionRecipe(PotionFluid.getItemFromPotionFluid(fluid), fluid));
                    }
                }
            } else if (focus.getRole() == RecipeIngredientRole.OUTPUT) {
                var ingredient = focus.getTypedValue().getIngredient(VanillaTypes.ITEM_STACK);
                if (ingredient.isPresent() && ingredient.get().getItem() == Items.POTION) {
                    ItemStack item = ingredient.get();
                    FluidStack fluid = PotionFluid.getPotionFluidFromItem(BOTTLE_VOLUME, item);
                    if (!fluid.isEmpty()) {
                        retList.add(getDynamicBottlerPotionRecipe(item, fluid));
                    }
                }
            }
            return (List<T>) retList;
        }
        return List.of();
    }

    @Override
    public <T> List<T> getRecipes(IRecipeType<T> recipeType) {

        if (recipeType.equals(BOTTLER_TYPE)) {
            if (bottlerRecipes.isEmpty()) {
                for (Holder<Potion> potion : BuiltInRegistries.POTION.listElements().toList()) {
                    if (!potion.is(Potions.WATER)) {
                        FluidStack fluid = PotionFluid.getPotionAsFluid(250, potion);
                        if (fluid.isEmpty()) {
                            continue;
                        }
                        bottlerRecipes.add(getDynamicBottlerPotionRecipe(PotionFluid.getItemFromPotionFluid(fluid), fluid));
                    }
                }
            }
            return (List<T>) bottlerRecipes;
        }
        return List.of();
    }

    // region HELPERS
    private final List<RecipeHolder<BottlerRecipe>> bottlerRecipes = new ArrayList<>();

    @NotNull
    private RecipeHolder<BottlerRecipe> getDynamicBottlerPotionRecipe(ItemStack item, FluidStack fluid) {

        BottlerRecipe recipe = new BottlerRecipe(BottlerRecipeManager.instance().getDefaultEnergy(), 0.0F,
                List.of(Ingredient.of(Items.GLASS_BOTTLE)),
                List.of(FluidIngredient.of(fluid).setAmount(BOTTLE_VOLUME)),
                List.of(ItemStackTemplate.fromNonEmptyStack(item)),
                List.of(1.0F),
                List.of()
        );
        return new RecipeHolder<>(ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(ID_THERMAL, "bottler_potion_" + getPotionName(fluid))), recipe);
    }

    private static String getPotionName(FluidStack fluid) {

        PotionContents contents = fluid.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        return contents.potion().flatMap(Holder::unwrapKey).map(key -> key.identifier().toString().replace(':', '_')).orElse("custom");
    }
    // endregion
}
