package io.github.bananapuncher714.cartographer.module.griefdefender;

import com.griefdefender.api.permission.flag.Flag;
import io.github.bananapuncher714.cartographer.core.api.events.minimap.MinimapLoadEvent;
import io.github.bananapuncher714.cartographer.core.api.setting.SettingState;
import io.github.bananapuncher714.cartographer.core.api.setting.SettingStateBoolean;
import io.github.bananapuncher714.cartographer.core.map.Minimap;
import io.github.bananapuncher714.cartographer.core.map.palette.PaletteManager;
import io.github.bananapuncher714.cartographer.core.module.Module;
import io.github.bananapuncher714.cartographer.core.util.FileUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.awt.*;
import java.io.File;
import java.util.Optional;

public class GriefDefenderModule extends Module implements Listener {
	public static final SettingStateBoolean GRIEFDEFENDER_CLAIMS = SettingStateBoolean.of( "griefdefender_show_claims", false, true );
	public static Flag CARTOGRAPHER_PUBLIC_SHOW;

	protected Optional<Color> owner, none, accessor, resident, container, builder, manager;


	@Override
	public void onEnable() {
		registerSettings();

		FileUtil.saveToFile( getResource( "config.yml" ), new File( getDataFolder(), "/config.yml" ), false );
		FileUtil.saveToFile( getResource( "README.md" ), new File( getDataFolder(), "/README.md" ), false );

		loadConfig();

		for ( Minimap minimap : getCartographer().getMapManager().getMinimaps().values() ) {
			init( minimap );
		}

		registerListener( this );

		CARTOGRAPHER_PUBLIC_SHOW = Flag.builder()
				.id("cartographer:show_to_public")
				.name("cartographer-public")
				.permission("griefdefender.flag.cartographer.public")
				.build();
	}
	
	@Override
	public SettingState< ? >[] getSettingStates() {
		return new SettingState< ? >[] {
			GRIEFDEFENDER_CLAIMS
		};
	}
	
	@EventHandler
	private void onEvent( MinimapLoadEvent event ) {
		init( event.getMinimap() );
	}

	private void init( Minimap minimap ) {
		minimap.register( new ClaimBorderShader( this ) );
	}
	
	private void loadConfig() {
		owner = Optional.empty();
		
		FileConfiguration config = YamlConfiguration.loadConfiguration( new File( getDataFolder(), "config.yml" ) );
		ConfigurationSection section = config.getConfigurationSection( "colors" );
		
		if ( section == null ) {
			getLogger().warning( "No 'colors' section found!" );
		} else {
			owner = PaletteManager.fromString(section.getString("owner", ""));
			none = PaletteManager.fromString(section.getString("public", ""));
			accessor = PaletteManager.fromString(section.getString("accessor", ""));
			resident = PaletteManager.fromString(section.getString("resident", ""));
			container = PaletteManager.fromString(section.getString("container", ""));
			builder = PaletteManager.fromString(section.getString("builder", ""));
			manager = PaletteManager.fromString(section.getString("manager", ""));
		}
	}

	public Optional<Color> getOwnerColor() {
		return owner;
	}
	public Optional<Color> getPublicColor() {
		return none;
	}
	public Optional<Color> getAccessorColor() {
		return accessor;
	}
	public Optional<Color> getResidentColor() {
		return resident;
	}
	public Optional<Color> getContainerColor() {
		return container;
	}
	public Optional<Color> getBuilderColor() {
		return builder;
	}
	public Optional<Color> getManagerColor() {
		return manager;
	}
}