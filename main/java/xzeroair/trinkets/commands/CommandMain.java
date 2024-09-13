package xzeroair.trinkets.commands;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.capabilities.race.EntityProperties;
import xzeroair.trinkets.init.EntityRaces;
import xzeroair.trinkets.races.EntityRace;

public class CommandMain extends CommandBase {

	//formatter:off
	private final List<String> tabCompletionsCommands = Arrays.asList("help", "mana", "race");
	//formatter:on

	private final String commandUsage = "/xat help";

	private final String listOfCommands = "   help <command>\n"
			+ "mana set <Amount>\n"
			+ "mana setMax <Amount>";

	private final String warn_notPlayerAdmin = "You do not have permission, or are not a player ingame!";
	private final String warn_invalidArgs = "Invalid Arguments";

	@Override
	public List<String> getAliases() {
		return Arrays.asList("xat");
	}

	@Override
	public String getName() {
		return "Trinkets and Baubles";
	}

	@Override
	public int getRequiredPermissionLevel() {
		return 4;
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "/xat <PLAYER> <Mana | Race> <ARG>";
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		int length = args.length;
		if (length == 4) {
			switch (args[1].toLowerCase(Locale.ENGLISH)) {
			case "mana":
				switch (args[2].toLowerCase(Locale.ENGLISH)) {
				case "set":
					return getListOfStringsMatchingLastWord(args, "0", "100", "200", "300");
				case "resetBonus":
					return Collections.<String>emptyList();
				default:
					return Collections.<String>emptyList();
				}
			case "race":
				switch (args[2].toLowerCase(Locale.ENGLISH)) {
				case "reset":
					return Collections.<String>emptyList();
				case "resetImbued":
					return Collections.<String>emptyList();
				default:
					return getListOfStringsMatchingLastWord(args, EntityRace.Registry.getKeys());
				}
			default:
				return Collections.<String>emptyList();
			}
		} else if (length == 3) {
			switch (args[1].toLowerCase(Locale.ENGLISH)) {
			case "mana":
				return getListOfStringsMatchingLastWord(args, "refill", "set", "resetBonus");
			case "race":
				return getListOfStringsMatchingLastWord(args, "setRace", "setImbuedRace", "reset", "resetImbued");
			default:
				return Collections.<String>emptyList();
			}
		} else if (length == 2) {
			return getListOfStringsMatchingLastWord(args, "mana", "race");
		} else if (length == 1) {
			return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		} else {
			return Collections.<String>emptyList();
		}
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		int length = args.length;
		if (length == 0) {
			this.help(sender);
			return;
		}
		EntityPlayer entityplayer = getPlayer(server, sender, args[0]);
		if (length > 1) {
			switch (args[1].toLowerCase(Locale.ENGLISH)) {
			case "mana":
				this.doMagic(entityplayer, server, sender, args);
				break;
			case "race":
				this.entityRace(entityplayer, server, sender, args);
				break;
			default:
				this.help(sender);
				break;
			}
		} else {
			this.help(sender);
		}
	}

	private void helpCommand(MinecraftServer server, ICommandSender sender, String[] args) {
		if (args.length < 2) {
			this.message(sender, listOfCommands);
			return;
		}

		switch (args[1].toLowerCase()) {
		case "help":
			this.message(
					sender,
					"If you need more help, you can contact the mod author on CurseForge or GitHub"
			);
			return;

		default:
			this.message(sender, "/xat help <command> \n(Replace <command> with a command name)");
			return;
		}
	}

	private void doMagic(EntityPlayer target, MinecraftServer server, ICommandSender sender, String[] args) {
		if (this.isAdminPlayer(sender)) {
			try {
				int i = 0;
				MagicStats capability = Capabilities.getMagicStats(target);
				if (args.length > 2) {
					switch (args[2].toLowerCase(Locale.ENGLISH)) {
					case "refill":
						capability.refillMana();
						break;
					case "set":
						if (args.length > 3) {
							capability.setMana(Float.parseFloat(args[3]));
						}
						break;
					case "resetbonus":
						capability.setBonusMana(0);
						break;
					default:
						break;
					}
				}
			} catch (NumberFormatException e) {
				this.message(sender, warn_invalidArgs + " <MP>");
				return;
			}
		}
	}

	private void entityRace(EntityPlayer target, MinecraftServer server, ICommandSender sender, String[] args) {
		if (this.isAdminPlayer(sender)) {
			try {
				int length = args.length;
				if (length < 2) {
					this.message(sender, warn_invalidArgs + " <modid>");
					return;
				}

				EntityProperties capability = Capabilities.getEntityProperties(target);

				String entity = args[0];
				if (length > 1) {
					String prefixCommand = args[1];
					if (length > 2) {
						String command = args[2].toLowerCase(Locale.ENGLISH);
						String race = "";
						if (length > 3) {
							race = args[3].toLowerCase(Locale.ENGLISH);
						}
						EntityRace r = EntityRace.getByNameOrId(race);
						//						System.out.println("??? " + race + "|" + r + "|" + command);
						switch (command) {
						case "setrace":
							if (r != null) {
								capability.setOriginalRace(r);
							}
							break;
						case "setimbuedrace":
							if (r != null) {
								capability.setImbuedRace(r);
							}
							break;
						case "reset":
							capability.setOriginalRace(EntityRaces.none);
							break;
						case "resetimbued":
							capability.setImbuedRace(EntityRaces.none);
							break;
						default:
							break;
						}
					}
				}
			} catch (NumberFormatException e) {
				this.message(sender, warn_invalidArgs + " <MP>");
				return;
			}
		}
	}

	private int getMetadataFromStack(ItemStack stack) {
		return stack.getHasSubtypes() ? stack.getMetadata() : -1;
	}

	private String getRegistryName(ItemStack stack) {
		return stack.getItem().getRegistryName().toString();
	}

	private boolean isAdminPlayer(ICommandSender sender) {
		if (this.hasPermissionLevel(sender, 4)) {
			if (sender.getCommandSenderEntity() instanceof EntityPlayer) {
				return true;
			}
		}

		return false;
	}

	private void help(ICommandSender sender) {
		sender.sendMessage(new TextComponentString(this.getUsage(sender)));
	}

	private void message(ICommandSender sender, String message) {
		sender.sendMessage(new TextComponentString(message));
	}

	private boolean hasPermissionLevel(ICommandSender sender, int permLevel) {
		return sender.canUseCommand(permLevel, "Trinkets and Baubles");
	}

	private boolean hasNBTArgument(String[] input) {
		return this.hasArgument("--nbt", input);
	}

	private boolean hasClearArgument(String[] input) {
		return this.hasArgument("--clear", input);
	}

	private boolean hasArgument(String argument, String[] input) {
		if (input == null) {
			return false;
		}

		for (String s : input) {
			if (s.equals(argument)) {
				return true;
			}
		}

		return false;
	}

}
