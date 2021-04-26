package fr.zankia.fallback;

import net.md_5.bungee.api.plugin.Plugin;

public class FallBack extends Plugin {

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, new FBListener(this));
    }

}