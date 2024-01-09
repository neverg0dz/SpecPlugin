package dev.nevergodz.specplugin;

import dev.nevergodz.specplugin.commands.SpecCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SpecPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        SpecCommand specCommand = new SpecCommand(this);
        getCommand("spec").setExecutor(specCommand);
    }

    @Override
    public void onDisable() {

    }
}
