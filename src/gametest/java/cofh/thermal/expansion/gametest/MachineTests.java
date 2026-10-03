package cofh.thermal.expansion.gametest;

import cofh.thermal.expansion.common.block.entity.machine.MachineFurnaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.EnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;

import javax.annotation.Nullable;

import static cofh.lib.api.control.IReconfigurable.SideConfig.SIDE_BOTH;
import static cofh.thermal.core.ThermalCore.BLOCKS;
import static cofh.thermal.lib.util.ThermalIDs.ID_MACHINE_FURNACE;

public class MachineTests {

    private static final BlockPos MACHINE = new BlockPos(3, 1, 3);
    private static final ItemResource COBBLESTONE = ItemResource.of(Items.COBBLESTONE);

    // Power and items go in, and the product comes out, all through capabilities.
    public static void furnaceProcessesThroughCapabilities(GameTestHelper helper) {

        MachineFurnaceBlockEntity furnace = placeFurnace(helper);
        ResourceHandler<ItemResource> items = itemHandler(helper, Direction.UP);
        fillEnergy(helper, 4000);
        try (Transaction tx = Transaction.openRoot()) {
            helper.assertValueEqual(items.insert(0, COBBLESTONE, 2, tx), 2, "cobblestone inserted");
            helper.assertValueEqual(items.extract(0, COBBLESTONE, 2, tx), 0, "cobblestone extracted from a restricted input");
            tx.commit();
        }
        helper.succeedWhen(() -> {
            helper.assertTrue(items.getResource(1).is(Items.STONE), "The furnace should have produced stone");
            try (Transaction tx = Transaction.openRoot()) {
                helper.assertValueEqual(items.extract(1, ItemResource.of(Items.STONE), 1, tx), 1, "stone extracted from the output");
            }
        });
    }

    // A pipe that simulates pulling a running machine's input must not stop it.
    public static void abortedPullKeepsMachineRunning(GameTestHelper helper) {

        MachineFurnaceBlockEntity furnace = placeFurnace(helper);
        ResourceHandler<ItemResource> accessible = itemHandler(helper, null);
        fillEnergy(helper, 20000);
        try (Transaction tx = Transaction.openRoot()) {
            accessible.insert(0, COBBLESTONE, 8, tx);
            tx.commit();
        }
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(furnace.isActive, "The furnace should start"))
                .thenExecute(() -> {
                    try (Transaction tx = Transaction.openRoot()) {
                        helper.assertTrue(accessible.extract(0, COBBLESTONE, 64, tx) > 0, "The input should be extractable without a side");
                    }
                    helper.assertTrue(accessible.getResource(0).is(Items.COBBLESTONE), "An aborted extract should restore the input");
                })
                .thenExecuteAfter(5, () -> helper.assertTrue(furnace.isActive, "An aborted extract should not stop the furnace"))
                .thenSucceed();
    }

    // region HELPERS
    private static MachineFurnaceBlockEntity placeFurnace(GameTestHelper helper) {

        helper.setBlock(MACHINE, BLOCKS.get(ID_MACHINE_FURNACE));
        MachineFurnaceBlockEntity furnace = helper.getBlockEntity(MACHINE, MachineFurnaceBlockEntity.class);
        furnace.reconfigControl().setSideConfig(Direction.UP, SIDE_BOTH);
        return furnace;
    }

    private static ResourceHandler<ItemResource> itemHandler(GameTestHelper helper, @Nullable Direction side) {

        ResourceHandler<ItemResource> handler = helper.getLevel().getCapability(Capabilities.Item.BLOCK, helper.absolutePos(MACHINE), side);
        helper.assertTrue(handler != null, "The furnace should expose an item handler");
        return handler;
    }

    private static void fillEnergy(GameTestHelper helper, int amount) {

        EnergyHandler energy = helper.getLevel().getCapability(Capabilities.Energy.BLOCK, helper.absolutePos(MACHINE), Direction.UP);
        helper.assertTrue(energy != null, "The furnace should expose an energy handler");
        int filled = 0;
        try (Transaction tx = Transaction.openRoot()) {
            int inserted;
            while (filled < amount && (inserted = energy.insert(amount - filled, tx)) > 0) {
                filled += inserted;
            }
            tx.commit();
        }
        helper.assertValueEqual(filled, amount, "energy inserted");
    }
    // endregion
}
