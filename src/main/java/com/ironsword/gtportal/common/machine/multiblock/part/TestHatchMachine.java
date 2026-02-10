package com.ironsword.gtportal.common.machine.multiblock.part;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.ironsword.gtportal.api.portal.DimensionData;
import com.ironsword.gtportal.api.portal.DimensionInfo;
import com.ironsword.gtportal.common.block.TestPortalBlock;
import com.ironsword.gtportal.common.item.component.DimensionDataComponent;
import com.ironsword.gtportal.common.machine.multiblock.PortalControllerMachine;
import com.ironsword.gtportal.common.machine.multiblock.TestPortalMachine;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TestHatchMachine extends MultiblockPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(TestHatchMachine.class,MultiblockPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    public final NotifiableItemStackHandler importItems;

    public TestHatchMachine(IMachineBlockEntity holder, NotifiableItemStackHandler importItems) {
        super(holder);
        this.importItems = createImportItemHandler();
    }

    protected NotifiableItemStackHandler createImportItemHandler() {
        return new NotifiableItemStackHandler(this,1, IO.BOTH){

            @Override
            public void onContentsChanged() {
                super.onContentsChanged();
                TestPortalMachine machine = getTestController();
                if (machine != null) {
                    //
                }
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (stack.getOrCreateTag().contains("dim_data")){
                    return super.insertItem(slot,stack,simulate);
                }
                return stack;
            }
        };
    }

    public Pair<ResourceLocation, Vec3i> readNbtFromItem(){
        if (getLevel() == null || getLevel().isClientSide()) return null;
        ItemStack stack = importItems.getStackInSlot(0);
        if (stack.getOrCreateTag().contains("dim_data")){
            DimensionData data = DimensionData.fromNbt(stack.getOrCreateTag().getCompound("dim_data"));
            if (!data.info().equals(DimensionInfo.EMPTY)){
                return Pair.of(data.info().getId(),data.pos());
            }
        }
        return Pair.of(null,null);
    }

    private TestPortalMachine getTestController(){
        for (var controller:getControllers()){
            if (controller instanceof TestPortalMachine machine)
                return machine;
        }
        return null;
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 18, 18);

        group.addWidget(new SlotWidget(importItems,0,0,0,true,true).setBackgroundTexture(GuiTextures.SLOT));
        return group;
    }


    @Override
    public boolean canShared() {
        return false;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

}
