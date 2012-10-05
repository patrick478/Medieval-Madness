package server.commands;

import initial3d.engine.Vec3;
import server.Command;
import server.Server;
import server.game.PlayerManager;
import server.game.ServerPlayer;

public class TpCommand extends Command {
	public TpCommand(Server server) {
		super(server);
	}

	@Override
	public void execute(String[] args) {
		String username = args[1];
		double x = Double.parseDouble(args[2]);
		double z = Double.parseDouble(args[2]);
		double y = this.parentServer.game.segQueue.getSegmentFromWorld(x,  z).getHeight(x, z);
		Vec3 target = Vec3.create(x, y, z);
		ServerPlayer sp = PlayerManager.getInstance().getPlayer(username);
		sp.teleportTo(target);
		PlayerManager.getInstance().ensureSegmentRange(sp);
	}
}
