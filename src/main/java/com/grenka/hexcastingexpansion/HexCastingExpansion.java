package com.grenka.hexcastingexpansion;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

@Mod("hexcastingexpansion")
@Mod.EventBusSubscriber(modid = "hexcastingexpansion", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HexCastingExpansion {

    public static double maxExplosionPower = 10.0;
    public static double maxTeleportRange = 30.0;
    private static final File CONFIG_FILE = FMLPaths.CONFIGDIR.get().resolve("hexcastingexpansion.properties").toFile();

    public HexCastingExpansion() {
        loadConfig();
    }

    private static void loadConfig() {
        Properties props = new Properties();
        if (CONFIG_FILE.exists()) {
            try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
                props.load(in);
                maxExplosionPower = Double.parseDouble(props.getProperty("maxExplosionPower", "10.0"));
                maxTeleportRange = Double.parseDouble(props.getProperty("maxTeleportRange", "30.0"));
            } catch (IOException | NumberFormatException e) {
                saveConfig();
            }
        } else {
            saveConfig();
        }
    }

    private static synchronized void saveConfig() {
        Properties props = new Properties();
        props.setProperty("maxExplosionPower", String.valueOf(maxExplosionPower));
        props.setProperty("maxTeleportRange", String.valueOf(maxTeleportRange));
        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            props.store(out, "Hex Casting Expansion Config");
        } catch (IOException ignored) {}
    }

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
                                    saveConfig();
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
                                    saveConfig();
                                    source.sendSuccess(() -> Component.literal("Дальность телепортации изменена на: " + value), true);
                                    return 1;
                                })
                        )
                )
        );
    }
}



