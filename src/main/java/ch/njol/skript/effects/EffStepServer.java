/**
 *   This file is part of Skript.
 *
 *  Skript is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Skript is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Skript.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright Peter Güttinger, SkriptLang team and contributors
 */
package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.ServerUtils;
import ch.njol.skript.doc.RequiredPlugins;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Timespan;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@Name("Step Server")
@Description({
	"Makes the server \"step\" for a certain amount of time",
	"The server can only step when it's ticking state is frozen.",
	"When you step, the server goes forward that amount of time in ticks."
})
@Examples({
	"make server step for 5 seconds",
	"make server stop stepping"
})
@Since("INSERT VERSION")
@RequiredPlugins("Minecraft 1.20.4+")
public class EffStepServer extends Effect {

	static {
		if (ServerUtils.isServerTickManagerPresent())
			Skript.registerEffect(EffStepServer.class,
				"make [the] server step for %timespan%",
				"make [the] server stop stepping");
	}

	private Expression<Timespan> timespan;


	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (matchedPattern == 0)
			timespan = (Expression<Timespan>) exprs[0];
		return true;
	}

	@Override
	protected void execute(Event event) {
		if (timespan != null) {
			Timespan timespan = this.timespan.getSingle(event);
			if (timespan != null)
				ServerUtils.getServerTickManager().stepGameIfFrozen((int) timespan.getTicks());
		} else {
			ServerUtils.getServerTickManager().stopStepping();
		}
	}


	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return timespan == null ? "make the server stop stepping" : "make the server step for " + timespan.toString(event, debug);
	}

}
