package com.ironsword.gtportal.common.machine.multiblock.part;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.ironsword.gtportal.api.portal.DimensionData;
import com.ironsword.gtportal.api.portal.DimensionInfo;
import com.ironsword.gtportal.common.item.component.DimensionDataComponent;
import com.ironsword.gtportal.common.machine.multiblock.PortalControllerMachine;
import com.lowdragmc.lowdraglib.gui.texture.IGuiTexture;
import com.lowdragmc.lowdraglib.gui.texture.ResourceTexture;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

public class DimensionDataHatchMachine extends MultiblockPartMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(DimensionDataHatchMachine.class,MultiblockPartMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    public final NotifiableItemStackHandler importItems;

    public DimensionDataHatchMachine(IMachineBlockEntity holder) {
        super(holder);
        this.importItems = createImportItemHandler();
    }

    protected NotifiableItemStackHandler createImportItemHandler() {
        return new NotifiableItemStackHandler(this,1, IO.BOTH){

            @Override
            public void onContentsChanged() {
                super.onContentsChanged();
                PortalControllerMachine machine = getPortalController();
                if (machine != null) {
                    machine.refreshDimensionData();
                }
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (stack.getItem() instanceof IComponentItem metaItem){
                    for (IItemComponent behaviour: metaItem.getComponents()){
                        if (behaviour instanceof DimensionDataComponent){
                            return super.insertItem(slot, stack, simulate);
                        }
                    }
                }
                return stack;
            }
        };
    }

    public DimensionData readData(){
        if (getLevel() == null || getLevel().isClientSide()) return null;
        ItemStack stack = importItems.getStackInSlot(0);
        if (stack.getOrCreateTag().contains("dim_data")){
            DimensionData data = DimensionData.fromNbt(stack.getOrCreateTag().getCompound("dim_data"));
            if (!data.info().equals(DimensionInfo.EMPTY)){
                return data;
            }
        }
        return null;
    }

    private PortalControllerMachine getPortalController(){
        for (var controller: getControllers()){
            if (controller instanceof PortalControllerMachine portalControllerMachine){
                return portalControllerMachine;
            }
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
