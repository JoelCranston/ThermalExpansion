package cofh.thermal.expansion.compat.jei.plugins;

import cofh.core.common.fluid.PotionFluid;
import cofh.lib.common.fluid.FluidIngredient;
import cofh.thermal.core.util.managers.machine.BottlerRecipeManager;
import cofh.thermal.core.util.recipes.machine.BottlerRecipe;
import cofh.thermal.expansion.compat.jei.machine.BottlerRecipeCategory;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.neoforge.NeoForgeTypes;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.advanced.IRecipeManagerPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static cofh.core.init.CoreFluids.POTION_FLUID;
import static cofh.lib.util.Constants.BOTTLE_VOLUME;
import static cofh.thermal.expansion.compat.jei.TExpJeiRecipeTypes.BOTTLER_TYPE;

public class PotionFluidRecipeManagerPlugin implements IRecipeManagerPlugin {

    public static final PotionFluidRecipeManagerPlugin INSTANCE = new PotionFluidRecipeManagerPlugin();

    private PotionFluidRecipeManagerPlugin() {

    }

    @Override
    public <V> List<RecipeType<?>> getRecipeTypes(IFocus<V> focus) {

        return List.of(BOTTLER_TYPE);
    }

    @Override
    public <T, V> List<T> getRecipes(IRecipeCategory<T> recipeCategory, IFocus<V> focus) {

        if (recipeCategory instanceof BottlerRecipeCategory) {
            List<BottlerRecipe> retList = new ArrayList<>();
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
    public <T> List<T> getRecipes(IRecipeCategory<T> recipeCategory) {

        if (recipeCategory instanceof BottlerRecipeCategory) {
            if (bottlerRecipes.isEmpty()) {
                // Potions are registry objects held by Holder since 1.21, and Potions.EMPTY is gone.
                for (Holder<Potion> potion : BuiltInRegistries.POTION.holders().toList()) {
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
    private final List<BottlerRecipe> bottlerRecipes = new ArrayList<>();

    @NotNull
    private BottlerRecipe getDynamicBottlerPotionRecipe(ItemStack item, FluidStack fluid) {

        return new BottlerRecipe(BottlerRecipeManager.instance().getDefaultEnergy(), 0.0F,
                List.of(Ingredient.of(Items.GLASS_BOTTLE)),
                List.of(FluidIngredient.of(fluid).setAmount(BOTTLE_VOLUME)),
                List.of(item),
                List.of(1.0F),
                List.of()
        );
    }
    // endregion
}
