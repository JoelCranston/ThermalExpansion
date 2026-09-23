package cofh.thermal.expansion.init.data;

import cofh.thermal.expansion.init.data.providers.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

import static cofh.lib.util.constants.ModIds.ID_THERMAL_EXPANSION;

@EventBusSubscriber (modid = ID_THERMAL_EXPANSION)
public class TExpDataGen {

    @SubscribeEvent
    public static void gatherData(final GatherDataEvent.Client event) {

        PackOutput output = event.getGenerator().getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        TExpTagsProvider.Block blockTags = event.addProvider(new TExpTagsProvider.Block(output, lookup));
        event.addProvider(new TExpTagsProvider.Item(output, lookup, blockTags.contentsGetter()));

        event.addProvider(new TExpLootTableProvider(output, lookup));
        event.addProvider(new TExpRecipeProvider.Runner(output, lookup));
    }

}
