package com.redpxnda.protectioncorrection.events;

import com.ezylang.evalex.EvaluationException;
import com.ezylang.evalex.Expression;
import com.ezylang.evalex.parser.ParseException;
import com.redpxnda.protectioncorrection.registry.Registry;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.math.BigDecimal;

public class ModEvents {

    // Attribute Replacement
    @SubscribeEvent
    public static void onItemAttributeModifierEvent(ItemAttributeModifierEvent event) {
        event.getModifiers().forEach((att, modifier) -> {
            if (att.getDescriptionId().equals(Attributes.ARMOR.getDescriptionId())) {
                event.removeModifier(att, modifier);
                att = Registry.PROTECTION.get();
                event.addModifier(att, modifier);
            } else if (att.getDescriptionId().equals(Attributes.ARMOR_TOUGHNESS.getDescriptionId())) {
                event.removeModifier(att, modifier);
                att = Registry.DEFENSE.get();
                event.addModifier(att, modifier);
            }
        });
    }

    // Protection Attribute implementation
    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent event) throws EvaluationException, ParseException {
        LivingEntity entity = event.getEntity(); // victim entity
        double overallProtectionValue = entity.getAttributeValue(Registry.PROTECTION.get()); // overall protection attribute
        double frontalProtectionValue = entity.getAttributeValue(Registry.FRONTAL_PROTECTION.get()); // frontal protection attribute
        double rearwardProtectionValue = entity.getAttributeValue(Registry.REARWARD_PROTECTION.get()); // rearwards protection attribute
        double sidewardProtectionValue = entity.getAttributeValue(Registry.SIDEWARD_PROTECTION.get()); // sidewards protection attribute
        if (event.getSource().isBypassArmor()) return; // stop if the damage type bypasses armor
        Entity directEntity = event.getSource().getDirectEntity(); // attacker entity, if present
        float side = -1; // initializing side variable
        if (directEntity != null) { // if there is an attacker entity
            float yLookDE = directEntity.getYRot(); // Y Rot of attacker
            if (yLookDE > 180) yLookDE-=360; // if 270, turn to -90
            else if (yLookDE <= -180) yLookDE+=360; // if -270, turn to 90
            float yLookEntity = entity.getYRot(); // Y Rot of victim
            if (yLookEntity > 180) yLookEntity-=360;
            else if (yLookEntity <= -180) yLookDE+=360;
            side = Math.abs(yLookDE-yLookEntity); // getting the difference between the two - this is how I detect which side the attack is from
            if (side > 180) side = Math.abs(side-360); // if 270, turn to |-90|
            side/=90f; // dividing to get smaller numbers
            side = Math.round(side*1000f)/1000f; // rounding to thousandths
            // 2=front, 0=back, 1=side (and obviously there can be in-betweens)
        }
        Expression expression = new Expression("x/(20+x)"); // using EvalEx so that in the future I can allow players to modify equations
        double ttlProtection = overallProtectionValue; // initializing ttlProtection variable, which is the total of all protection attributes, if applicable
        if (side >= 0 && side < 1) ttlProtection = overallProtectionValue+(rearwardProtectionValue*(1-side))+(sidewardProtectionValue*side); // if from back or side
        else if (side >= 1 && side <= 2) ttlProtection = overallProtectionValue+(frontalProtectionValue*side)+(sidewardProtectionValue*(2-side)); // if from front or side
        float reductionPercent = expression.with("x", ttlProtection).evaluate().getNumberValue().floatValue(); // actually evaluating the equation, with x = ttlProtection
        float newDamage = event.getAmount() - event.getAmount()*reductionPercent; // new damage
        event.setAmount(newDamage); // setting event's damage to new damage
    }
}
