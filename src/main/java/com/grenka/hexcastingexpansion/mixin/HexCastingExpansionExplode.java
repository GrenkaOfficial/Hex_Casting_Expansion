package com.grenka.hexcastingexpansion.mixin;

import com.grenka.hexcastingexpansion.HexCastingExpansion;
import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import java.util.List;
import java.util.ArrayList;

@Mixin(at.petrak.hexcasting.common.casting.actions.spells.OpExplode.class)
public class HexCastingExpansionExplode {

    /**
     * @author Grenka
     * @reason replacement HC autor's explode
     */
    @Overwrite(remap = false)
    @NotNull
    public SpellAction.Result execute(@NotNull List<? extends Iota> args, @NotNull CastingEnvironment env) {
        Vec3 pos = OperatorUtils.getVec3(args, 0, 2);

        double strength = 1.0;
        if (!args.isEmpty() && args.size() > 1 && args.get(1) instanceof DoubleIota doubleIota) {
            strength = Math.max(0.0, doubleIota.getDouble());
        }

        double maxAllowed = HexCastingExpansion.maxExplosionPower;
        double clampedStrength = Mth.clamp(strength, 0.0, maxAllowed);

        double cost = 10000.0 * (3.0 * clampedStrength + 0.125);

        List<ParticleSpray> particles = new ArrayList<>();
        particles.add(ParticleSpray.Companion.burst(pos, clampedStrength, 50));

        try {
            Class<?> spellClass = Class.forName("at.petrak.hexcasting.common.casting.actions.spells.OpExplode$Spell");
            java.lang.reflect.Constructor<?> constructor = spellClass.getDeclaredConstructor(Vec3.class, double.class, boolean.class);
            constructor.setAccessible(true);
            Object spellInstance = constructor.newInstance(pos, clampedStrength, false);

            return new SpellAction.Result((RenderedSpell) spellInstance, (long) cost, particles, 0L);
        } catch (Exception e) {
            RenderedSpell dummySpell = new RenderedSpell() {
                @Override
                public void cast(@NotNull CastingEnvironment environment) {}
            };
            return new SpellAction.Result(dummySpell, 0L, particles, 0L);
        }
    }
}
