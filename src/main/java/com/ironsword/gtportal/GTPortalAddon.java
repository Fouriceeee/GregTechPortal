package com.ironsword.gtportal;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.FoodStats;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.registry.GTRegistration;
import com.gregtechceu.gtceu.data.recipe.CustomTags;
import com.ironsword.gtportal.common.data.GTPRecipes;
import com.ironsword.gtportal.common.item.component.DimensionDataComponent;
import com.ironsword.gtportal.common.registry.GTPRegistries;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

import java.util.function.Consumer;

import static com.gregtechceu.gtceu.common.data.GTCreativeModeTabs.ITEM;
import static com.gregtechceu.gtceu.common.registry.GTRegistration.REGISTRATE;

@GTAddon
public class GTPortalAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return GTPRegistries.REGISTRATE;
    }

    @Override
    public void initializeAddon() {
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        GTPRecipes.init(provider);
    }

    @Override
    public String addonModId() {
        return GTPortal.MODID;
    }
}
