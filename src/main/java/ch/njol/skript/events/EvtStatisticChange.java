package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.Statistic;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.jetbrains.annotations.Nullable;

public class EvtStatisticChange extends SkriptEvent {

	static {
		Skript.registerEvent("Statistic Change", EvtStatisticChange.class, PlayerStatisticIncrementEvent.class,
				"[player] statistic[s] (change|increase) [of %statistics%]")
			.description("Called when a player statistic is incremented.",
				"You can use past/future event-number to the get the past/future new value, event-number is the difference between the old and the new value.",
				"NOTE: This event is not called for <a href='https://hub.spigotmc.org/stash/projects/SPIGOT/repos/craftbukkit/browse/src/main/java/org/bukkit/craftbukkit/event/CraftEventFactory.java#1456'>some high frequency statistics</a>, e.g. movement based statistics. However, note that the excluded statistics change from version to version, so you may find that some still work in some old versions, like 'TIME_SINCE_REST' before 1.17."
			)
			.examples(
				"on player statistic increase:",
				"\tsend \"Statistic increased: %event-statistic% from %past event-number% to %future event-number% (diff: %event-number%)\" to player",
				"\tif event-entitytype is set:",
				"\t\tsend \"Of entity: %event-entitytype%\" to player",
				"\telse if event-item is set:",
				"\t\tsend \"Of item: %event-item%\" to player",
				"",
				"on player statistic increase of chest opened:",
				"\tsend \"You just opened a chest!\" to player"
			)
			.since("INSERT VERSION");
	}

	private Statistic @Nullable [] statistics;

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
		if (args.length > 0 && args[0] != null) {
			statistics = ((Literal<Statistic>) args[0]).getArray();
		}
		return true;
	}

	@Override
	public boolean check(Event event) {
		PlayerStatisticIncrementEvent statEvent = (PlayerStatisticIncrementEvent) event;
		if (statistics == null) {
			return true;
		}
		for (Statistic statistic : statistics) {
			if (statEvent.getStatistic() == statistic) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "statistic change" + (statistics != null ? " of " + statistics : "");
	}
}
