package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.aliases.ItemType;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.util.Kleenean;
import org.bukkit.Material;
import org.bukkit.event.Event;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.eclipse.jdt.annotation.Nullable;

import java.util.Locale;
import java.util.regex.Matcher;

@Name("Dyed")
@Description("An expression to return colored items.")
@Examples({
	"give player leather chestplate dyed red",
	"give player potion of invisibility dyed rgb 200, 70, 88",
	"give player filled map colored rgb(20, 60, 70)",
	"give player wool painted red"
})
@Since("INSERT VERSION")
public class ExprDyed extends SimpleExpression<ItemType> {

	static {
		Skript.registerExpression(ExprDyed.class, ItemType.class, ExpressionType.COMBINED, "%itemtypes% (dyed|painted|colo[u]red) %color%");
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<ItemType> items;
	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Color> color;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] vars, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		items = (Expression<ItemType>) vars[0];
		color = (Expression<Color>) vars[1];
		return true;
	}
	
	@Override
	protected ItemType @Nullable [] get(Event event) {
		Color color = this.color.getSingle(event);
		if (color == null)
			return new ItemType[0];

		ItemType[] items = this.items.getArray(event);
		org.bukkit.Color bukkitColor = color.asBukkitColor();

		for (ItemType item : items) {
			ItemMeta meta = item.getItemMeta();

			if (meta instanceof LeatherArmorMeta) {
				LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) meta;
				leatherArmorMeta.setColor(bukkitColor);
				item.setItemMeta(leatherArmorMeta);
			} else if (meta instanceof MapMeta) {
				MapMeta mapMeta = (MapMeta) meta;
				mapMeta.setColor(bukkitColor);
				item.setItemMeta(mapMeta);
			} else if (meta instanceof PotionMeta) {
				PotionMeta potionMeta = (PotionMeta) meta;
				potionMeta.setColor(bukkitColor);
				item.setItemMeta(potionMeta);
			} else {
				if (color instanceof ColorRGB) // currently blocks don't support RGB
					continue;

				Material material = item.getMaterial();
				Matcher matcher = ExprColorOf.MATERIAL_COLORS_PATTERN.matcher(material.name());
				if (!matcher.matches())
					continue;
				try {
					Material newItem = Material.valueOf(material.name().replace(matcher.group(1), color.getName().toUpperCase(Locale.ENGLISH)));
					item.setTo(new ItemType(newItem));
				} catch (IllegalArgumentException ignored) {}
			}
		}
		return items;
	}

	@Override
	public boolean isSingle() {
		return items.isSingle();
	}

	@Override
	public Class<? extends ItemType> getReturnType() {
		return ItemType.class;
	}
	
	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return items.toString(event, debug) + " dyed " + color.toString(event, debug);
	}
	
}
