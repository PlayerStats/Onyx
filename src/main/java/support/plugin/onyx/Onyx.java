package support.plugin.onyx;

import lombok.Getter;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import support.plugin.onyx.config.Configuration;
import support.plugin.onyx.factions.FactionManager;

/**
 * Created by eric on 31/08/2017.
 */
public class Onyx extends JavaPlugin {

    @Getter
    public static Onyx instance;

    @Getter
    private Configuration settings,locale;

    @Getter
    private FactionManager factionManager;

    public void onEnable(){

        instance = this;

        loadConfiguration();
        loadCommands();
        loadListeners();

        // Handlers...
        factionManager = new FactionManager(this);

    }

    public void loadConfiguration(){

        this.settings = new Configuration(this,"settings.yml");
        this.locale = new Configuration(this,"locale.yml");

    }

    public void loadCommands(){

    }

    public void loadListeners(){

        PluginManager pluginManager = getServer().getPluginManager();

    }

}
