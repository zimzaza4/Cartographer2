package io.github.bananapuncher714.cartographer.module.griefdefender;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.Tristate;
import com.griefdefender.api.claim.Claim;
import com.griefdefender.api.claim.TrustTypes;
import com.griefdefender.lib.flowpowered.math.vector.Vector3i;
import io.github.bananapuncher714.cartographer.core.api.WorldPixel;
import io.github.bananapuncher714.cartographer.core.api.map.WorldPixelProvider;
import io.github.bananapuncher714.cartographer.core.map.MapViewer;
import io.github.bananapuncher714.cartographer.core.map.Minimap;
import io.github.bananapuncher714.cartographer.core.renderer.PlayerSetting;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;
import java.util.*;

public class ClaimBorderShader implements WorldPixelProvider{
	protected GriefDefenderModule module;
	public ClaimBorderShader(GriefDefenderModule module) { this.module = module; }

	@Override
	public Collection< WorldPixel > getWorldPixels(Player player, Minimap map, PlayerSetting setting) {
		Set< WorldPixel > pixels = new HashSet<>();
		MapViewer viewer = module.getCartographer().getPlayerManager().getViewerFor(setting.getUUID());
		if (viewer.getSetting(GriefDefenderModule.GRIEFDEFENDER_CLAIMS)) {
			List<Claim> claims = GriefDefender.getCore().getAllClaims();
			for (Claim claim : claims) {
				Optional<Color> optionalColor = module.getPublicColor();
				if(claim.getUserTrusts().contains(player.getUniqueId())) {
					if (claim.isUserTrusted(player.getUniqueId(), TrustTypes.ACCESSOR)) optionalColor = module.getAccessorColor();
					else if (claim.isUserTrusted(player.getUniqueId(), TrustTypes.MANAGER)) optionalColor = module.getManagerColor();
					else if (claim.isUserTrusted(player.getUniqueId(), TrustTypes.BUILDER)) optionalColor = module.getBuilderColor();
					else if (claim.isUserTrusted(player.getUniqueId(), TrustTypes.CONTAINER)) optionalColor = module.getContainerColor();
					else if (claim.isUserTrusted(player.getUniqueId(), TrustTypes.RESIDENT)) optionalColor = module.getResidentColor();
				} else if (claim.getOwnerUniqueId().equals(player.getUniqueId())) {
					optionalColor = module.getOwnerColor();
				} else if (claim.getFlagPermissionValue(GriefDefenderModule.CARTOGRAPHER_PUBLIC_SHOW, null) == Tristate.FALSE) {
					continue;
				}


				if (optionalColor.isPresent()) {
					Color color = optionalColor.get();
					Vector3i minCorner = claim.getLesserBoundaryCorner();
					World world = Bukkit.getWorld(claim.getWorldUniqueId());
					int pixelWidth = setting.getScale() < 1 ? 2 : 1;
					// Width = x / Length = z
					double thickness = Math.min(setting.getScale(), claim.getWidth()) * pixelWidth;
					double thicknessHeight = Math.min( setting.getScale(), claim.getLength()) * pixelWidth;

						WorldPixel north = new WorldPixel(world, minCorner.getX(), minCorner.getZ(), color);
						north.setWidth(claim.getWidth());
						north.setHeight(thicknessHeight);
						pixels.add(north);

						WorldPixel south = new WorldPixel(world, minCorner.getX(), minCorner.getZ() + claim.getLength() - thicknessHeight, color);
						south.setWidth(claim.getWidth());
						south.setHeight(thicknessHeight);
						pixels.add(south);

						WorldPixel west = new WorldPixel(world, minCorner.getX(), minCorner.getZ(), color);
						west.setWidth(thickness);
						west.setHeight(claim.getLength());
						pixels.add(west);

						WorldPixel east = new WorldPixel(world, minCorner.getX() + claim.getWidth() - thickness, minCorner.getZ(), color);
						east.setWidth(thickness);
						east.setHeight(claim.getLength());
						pixels.add(east);
					}
				}
			}
		return pixels;
	}
}
