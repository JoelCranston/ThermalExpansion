package cofh.thermal.expansion.init.registries;

import cofh.thermal.expansion.common.block.entity.dynamo.*;
import cofh.thermal.expansion.common.block.entity.machine.*;
import cofh.thermal.lib.common.block.entity.AugmentableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

import java.util.List;
import java.util.function.Supplier;

import static cofh.thermal.core.ThermalCore.BLOCKS;
import static cofh.thermal.core.ThermalCore.BLOCK_ENTITIES;
import static cofh.thermal.lib.util.ThermalIDs.*;

public class TExpBlockEntities {

    private TExpBlockEntities() {

    }

    public static void register() {

    }

    public static void capabilitySetup(RegisterCapabilitiesEvent event) {

        var entities = List.of(
                MACHINE_FURNACE_TILE.get(),
                MACHINE_SAWMILL_TILE.get(),
                MACHINE_PULVERIZER_TILE.get(),
                MACHINE_SMELTER_TILE.get(),
                MACHINE_INSOLATOR_TILE.get(),
                MACHINE_CENTRIFUGE_TILE.get(),
                MACHINE_PRESS_TILE.get(),
                MACHINE_CRUCIBLE_TILE.get(),
                MACHINE_CHILLER_TILE.get(),
                MACHINE_REFINERY_TILE.get(),
                MACHINE_PYROLYZER_TILE.get(),
                MACHINE_BOTTLER_TILE.get(),
                MACHINE_BREWER_TILE.get(),
                MACHINE_CRYSTALLIZER_TILE.get(),
                MACHINE_CRAFTER_TILE.get(),

                DYNAMO_STIRLING_TILE.get(),
                DYNAMO_COMPRESSION_TILE.get(),
                DYNAMO_MAGMATIC_TILE.get(),
                DYNAMO_NUMISMATIC_TILE.get(),
                DYNAMO_LAPIDARY_TILE.get(),
                DYNAMO_DISENCHANTMENT_TILE.get(),
                DYNAMO_GOURMAND_TILE.get()
        );

        for (var type : entities) {
            event.registerBlockEntity(Capabilities.Item.BLOCK, type, (blockEntity, side) -> ((AugmentableBlockEntity) blockEntity).getItemHandlerCapability(side));
            event.registerBlockEntity(Capabilities.Fluid.BLOCK, type, (blockEntity, side) -> ((AugmentableBlockEntity) blockEntity).getFluidHandlerCapability(side));
            event.registerBlockEntity(Capabilities.Energy.BLOCK, type, (blockEntity, side) -> ((AugmentableBlockEntity) blockEntity).getEnergyCapability(side));
        }
    }

    public static final Supplier<BlockEntityType<?>> MACHINE_FURNACE_TILE = BLOCK_ENTITIES.register(ID_MACHINE_FURNACE, () -> new BlockEntityType<>(MachineFurnaceBlockEntity::new, BLOCKS.get(ID_MACHINE_FURNACE)));
    public static final Supplier<BlockEntityType<?>> MACHINE_SAWMILL_TILE = BLOCK_ENTITIES.register(ID_MACHINE_SAWMILL, () -> new BlockEntityType<>(MachineSawmillBlockEntity::new, BLOCKS.get(ID_MACHINE_SAWMILL)));
    public static final Supplier<BlockEntityType<?>> MACHINE_PULVERIZER_TILE = BLOCK_ENTITIES.register(ID_MACHINE_PULVERIZER, () -> new BlockEntityType<>(MachinePulverizerBlockEntity::new, BLOCKS.get(ID_MACHINE_PULVERIZER)));
    public static final Supplier<BlockEntityType<?>> MACHINE_SMELTER_TILE = BLOCK_ENTITIES.register(ID_MACHINE_SMELTER, () -> new BlockEntityType<>(MachineSmelterBlockEntity::new, BLOCKS.get(ID_MACHINE_SMELTER)));
    public static final Supplier<BlockEntityType<?>> MACHINE_INSOLATOR_TILE = BLOCK_ENTITIES.register(ID_MACHINE_INSOLATOR, () -> new BlockEntityType<>(MachineInsolatorBlockEntity::new, BLOCKS.get(ID_MACHINE_INSOLATOR)));
    public static final Supplier<BlockEntityType<?>> MACHINE_CENTRIFUGE_TILE = BLOCK_ENTITIES.register(ID_MACHINE_CENTRIFUGE, () -> new BlockEntityType<>(MachineCentrifugeBlockEntity::new, BLOCKS.get(ID_MACHINE_CENTRIFUGE)));
    public static final Supplier<BlockEntityType<?>> MACHINE_PRESS_TILE = BLOCK_ENTITIES.register(ID_MACHINE_PRESS, () -> new BlockEntityType<>(MachinePressBlockEntity::new, BLOCKS.get(ID_MACHINE_PRESS)));
    public static final Supplier<BlockEntityType<?>> MACHINE_CRUCIBLE_TILE = BLOCK_ENTITIES.register(ID_MACHINE_CRUCIBLE, () -> new BlockEntityType<>(MachineCrucibleBlockEntity::new, BLOCKS.get(ID_MACHINE_CRUCIBLE)));
    public static final Supplier<BlockEntityType<?>> MACHINE_CHILLER_TILE = BLOCK_ENTITIES.register(ID_MACHINE_CHILLER, () -> new BlockEntityType<>(MachineChillerBlockEntity::new, BLOCKS.get(ID_MACHINE_CHILLER)));
    public static final Supplier<BlockEntityType<?>> MACHINE_REFINERY_TILE = BLOCK_ENTITIES.register(ID_MACHINE_REFINERY, () -> new BlockEntityType<>(MachineRefineryBlockEntity::new, BLOCKS.get(ID_MACHINE_REFINERY)));
    public static final Supplier<BlockEntityType<?>> MACHINE_PYROLYZER_TILE = BLOCK_ENTITIES.register(ID_MACHINE_PYROLYZER, () -> new BlockEntityType<>(MachinePyrolyzerBlockEntity::new, BLOCKS.get(ID_MACHINE_PYROLYZER)));
    public static final Supplier<BlockEntityType<?>> MACHINE_BOTTLER_TILE = BLOCK_ENTITIES.register(ID_MACHINE_BOTTLER, () -> new BlockEntityType<>(MachineBottlerBlockEntity::new, BLOCKS.get(ID_MACHINE_BOTTLER)));
    public static final Supplier<BlockEntityType<?>> MACHINE_BREWER_TILE = BLOCK_ENTITIES.register(ID_MACHINE_BREWER, () -> new BlockEntityType<>(MachineBrewerBlockEntity::new, BLOCKS.get(ID_MACHINE_BREWER)));
    public static final Supplier<BlockEntityType<?>> MACHINE_CRYSTALLIZER_TILE = BLOCK_ENTITIES.register(ID_MACHINE_CRYSTALLIZER, () -> new BlockEntityType<>(MachineCrystallizerBlockEntity::new, BLOCKS.get(ID_MACHINE_CRYSTALLIZER)));
    public static final Supplier<BlockEntityType<?>> MACHINE_CRAFTER_TILE = BLOCK_ENTITIES.register(ID_MACHINE_CRAFTER, () -> new BlockEntityType<>(MachineCrafterBlockEntity::new, BLOCKS.get(ID_MACHINE_CRAFTER)));

    public static final Supplier<BlockEntityType<?>> DYNAMO_STIRLING_TILE = BLOCK_ENTITIES.register(ID_DYNAMO_STIRLING, () -> new BlockEntityType<>(DynamoStirlingBlockEntity::new, BLOCKS.get(ID_DYNAMO_STIRLING)));
    public static final Supplier<BlockEntityType<?>> DYNAMO_COMPRESSION_TILE = BLOCK_ENTITIES.register(ID_DYNAMO_COMPRESSION, () -> new BlockEntityType<>(DynamoCompressionBlockEntity::new, BLOCKS.get(ID_DYNAMO_COMPRESSION)));
    public static final Supplier<BlockEntityType<?>> DYNAMO_MAGMATIC_TILE = BLOCK_ENTITIES.register(ID_DYNAMO_MAGMATIC, () -> new BlockEntityType<>(DynamoMagmaticBlockEntity::new, BLOCKS.get(ID_DYNAMO_MAGMATIC)));
    public static final Supplier<BlockEntityType<?>> DYNAMO_NUMISMATIC_TILE = BLOCK_ENTITIES.register(ID_DYNAMO_NUMISMATIC, () -> new BlockEntityType<>(DynamoNumismaticBlockEntity::new, BLOCKS.get(ID_DYNAMO_NUMISMATIC)));
    public static final Supplier<BlockEntityType<?>> DYNAMO_LAPIDARY_TILE = BLOCK_ENTITIES.register(ID_DYNAMO_LAPIDARY, () -> new BlockEntityType<>(DynamoLapidaryBlockEntity::new, BLOCKS.get(ID_DYNAMO_LAPIDARY)));
    public static final Supplier<BlockEntityType<?>> DYNAMO_DISENCHANTMENT_TILE = BLOCK_ENTITIES.register(ID_DYNAMO_DISENCHANTMENT, () -> new BlockEntityType<>(DynamoDisenchantmentBlockEntity::new, BLOCKS.get(ID_DYNAMO_DISENCHANTMENT)));
    public static final Supplier<BlockEntityType<?>> DYNAMO_GOURMAND_TILE = BLOCK_ENTITIES.register(ID_DYNAMO_GOURMAND, () -> new BlockEntityType<>(DynamoGourmandBlockEntity::new, BLOCKS.get(ID_DYNAMO_GOURMAND)));

}
