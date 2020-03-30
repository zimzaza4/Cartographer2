package io.github.bananapuncher714.cartographer.module.vanilla.providers;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.bananapuncher714.cartographer.core.renderer.PlayerSetting;
import io.github.bananapuncher714.cartographer.module.vanilla.NamedLocation;
import io.github.bananapuncher714.cartographer.module.vanilla.VanillaPlus;

public class CursorProviderDeathLocation implements ObjectProvider< NamedLocation > {
	private VanillaPlus main;
	
	public CursorProviderDeathLocation( VanillaPlus main ) {
		this.main = main;
	}
	
	@Override
	public Set< NamedLocation > getFor( Player player, PlayerSetting settings ) {
		Set< NamedLocation > locations = new HashSet< NamedLocation >();
		
		Location death = main.getDeathOf( player.getUniqueId() );
		if ( death != null ) {
			locations.add( new NamedLocation( "death", death.clone() ) );
		}
		
		return locations;
	}
}
