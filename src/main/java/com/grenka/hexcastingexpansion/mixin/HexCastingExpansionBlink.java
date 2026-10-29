package com.grenka.hexcastingexpansion.mixin;

import com.grenka.hexcastingexpansion.HexCastingExpansion;
import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.List;
import java.util.ArrayList;

@Mixin(at.petrak.hexcasting.common.casting.actions.spells.OpBlink.class)
public class HexCastingExpansionBlink {

    /**
     * @author Grenka
     * @reason replacement HC autor's explode
     */
    @Overwrite(remap = false)
    @NotNull
    public SpellAction.Result execute(@NotNull List<? extends Iota> args, @NotNull CastingEnvironment env) {
        Entity target = OperatorUtils.getEntity(args, 0, 2);
        double delta = OperatorUtils.getDouble(args, 1, 2);

        double maxAllowed = HexCastingExpansion.maxTeleportRange;
        if (Math.abs(delta) > maxAllowed) {
            delta = Math.copySign(maxAllowed, delta);
        }

        at.petrak.hexcasting.api.HexAPI hexAPI = at.petrak.hexcasting.api.HexAPI.instance();
        Vec3 dvec = hexAPI.getEntityLookDirSpecial(target).scale(delta);

        Vec3 endPos = target.position().add(dvec);
        long cost = Math.round(50000.0 * Math.abs(delta) * 0.5);

        List<ParticleSpray> particles = new ArrayList<>();
        Vec3 targetMiddlePos = target.position().add(0.0, target.getEyeHeight() / 2.0, 0.0);
        particles.add(ParticleSpray.Companion.cloud(targetMiddlePos, 2.0, 50));
        particles.add(ParticleSpray.Companion.burst(endPos, 2.0, 100));

        try {
            Class<?> spellClass = Class.forName("at.petrak.hexcasting.common.casting.actions.spells.OpBlink$Spell");
            java.lang.reflect.Constructor<?> constructor = spellClass.getDeclaredConstructor(Entity.class, Vec3.class);
            constructor.setAccessible(true);
            Object spellInstance = constructor.newInstance(target, dvec);

            return new SpellAction.Result((RenderedSpell) spellInstance, cost, particles, 0L);
        } catch (Exception e) {
            RenderedSpell dummySpell = new RenderedSpell() {
                @Override
                public void cast(@NotNull CastingEnvironment environment) {}
            };
            return new SpellAction.Result(dummySpell, 0L, particles, 0L);
        }
    }
}
