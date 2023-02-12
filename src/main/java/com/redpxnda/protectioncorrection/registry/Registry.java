package com.redpxnda.protectioncorrection.registry;

import com.redpxnda.protectioncorrection.ProtectionCorrection;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.redpxnda.protectioncorrection.ProtectionCorrection.MODID;

public class Registry {
    public static DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, MODID);

    public static final RegistryObject<Attribute> PROTECTION = ATTRIBUTES
            .register("protection",
                    () -> new RangedAttribute(
                            "attribute.protectioncorrection.protection",
                            0,
                            0.0D,
                            10240.0D)
                            .setSyncable(true)
            );
    public static final RegistryObject<Attribute> FRONTAL_PROTECTION = ATTRIBUTES
            .register("frontal_protection",
                    () -> new RangedAttribute(
                            "attribute.protectioncorrection.frontal_protection",
                            0,
                            -10240.0D,
                            10240.0D)
                            .setSyncable(true)
            );
    public static final RegistryObject<Attribute> REARWARD_PROTECTION = ATTRIBUTES
            .register("rearward_protection",
                    () -> new RangedAttribute(
                            "attribute.protectioncorrection.rearward_protection",
                            0,
                            -10240.0D,
                            10240.0D)
                            .setSyncable(true)
            );
    public static final RegistryObject<Attribute> SIDEWARD_PROTECTION = ATTRIBUTES
            .register("sideward_protection",
                    () -> new RangedAttribute(
                            "attribute.protectioncorrection.sideward_protection",
                            0,
                            -10240.0D,
                            10240.0D)
                            .setSyncable(true)
            );
    public static final RegistryObject<Attribute> DEFENSE = ATTRIBUTES
            .register("defense",
                    () -> new RangedAttribute(
                            "attribute.protectioncorrection.defense",
                            0,
                            0.0D,
                            1024.0D)
                            .setSyncable(true)
            );


    public static void attachAttributes(EntityAttributeModificationEvent event) {
        ProtectionCorrection.LOGGER.debug("Attaching Attributes.");
        event.getTypes().forEach((e) -> {
            event.add(e, PROTECTION.get(), 0.0);
            event.add(e, FRONTAL_PROTECTION.get(), 0.0);
            event.add(e, REARWARD_PROTECTION.get(), 0.0);
            event.add(e, SIDEWARD_PROTECTION.get(), 0.0);
            event.add(e, DEFENSE.get(), 0.0);
        });
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
