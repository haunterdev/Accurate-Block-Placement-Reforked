package net.clayborn.accurateblockplacement.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
public class AccurateBlockPlacementPlugin implements IFMLLoadingPlugin, IEarlyMixinLoader {

    // MixinBooter owns Mixin. Calling MixinBootstrap.init() from this constructor
    // loads a second MixinBootstrap in the LaunchClassLoader and crashes the
    // client at startup with a loader-constraint LinkageError on
    // GlobalProperties$Keys, so the config is handed to MixinBooter instead.
    @Override
    public List<String> getMixinConfigs() {
        return Collections.singletonList("mixins.accurateblockplacement.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
