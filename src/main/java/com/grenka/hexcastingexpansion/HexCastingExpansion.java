package com.grenka.hexcastingexpansion;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("hexcastingexpansion")
@Mod.EventBusSubscriber(modid = "hexcastingexpansion", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HexCastingExpansion {

    public static double maxExplosionPower = 10.0;
    public static double maxTeleportRange = 30.0;

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        dispatcher.register(Commands.literal("hexcastingexpansion")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("explosion")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0))
                                .executes(context -> {
                                    double value = DoubleArgumentType.getDouble(context, "value");
                                    CommandSourceStack source = context.getSource();
                                    maxExplosionPower = value;
                                    source.sendSuccess(() -> Component.literal("Сила взрыва изменена на: " + value), true);
                                    return 1;
                                })
                        )
                )
                .then(Commands.literal("teleport")
                        .then(Commands.argument("value", DoubleArgumentType.doubleArg(0.0))
                                .executes(context -> {
                                    double value = DoubleArgumentType.getDouble(context, "value");
                                    CommandSourceStack source = context.getSource();
                                    maxTeleportRange = value;
                                    source.sendSuccess(() -> Component.literal("Дальность телепортации изменена на: " + value), true);
                                    return 1;
                                })
                        )
                )
        );
    }
}


