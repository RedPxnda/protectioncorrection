package com.redpxnda.protectioncorrection;

import com.mojang.logging.LogUtils;
import com.redpxnda.protectioncorrection.events.ModEvents;
import com.redpxnda.protectioncorrection.registry.Registry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(ProtectionCorrection.MODID)
public class ProtectionCorrection {
    public static final String MODID = "protectioncorrection";
    public static final Logger LOGGER = LogUtils.getLogger();

    public ProtectionCorrection() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Registry.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(ModEvents.class);

        modEventBus.addListener(Registry::attachAttributes);
    }
}
