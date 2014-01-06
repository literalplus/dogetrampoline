package io.github.xxyy.dogetrampoline;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 * DogeTrampoline JavaPlugin class.
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 04.01.14
 */
public class DogeTrampolinePlugin extends JavaPlugin {
    private Permission usePermission;
    private double trampolineAcceleration;
    private NMSBlockProxyFactory blockProxyFactory = new NMSBlockProxyFactory(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();

        usePermission = new Permission(getConfig().getString("permission"), "Allows to use DogeTrampolines.", PermissionDefault.OP);
        getServer().getPluginManager().addPermission(usePermission);
        trampolineAcceleration = getConfig().getDouble("factor");

        this.blockProxyFactory.tryHook();
    }

    @Override
    public void onDisable() {
        this.blockProxyFactory.getProxy().unhook();
    }

//    void kill() throws IllegalStateException {
//        super.setEnabled(false);
//        throw new IllegalStateException("DogeTrampolinePlugin killed! (Look for stacktraces in your log file or console)");
//    }

    public Permission getUsePermission(){
        return usePermission;
    }

    public double getTrampolineAcceleration() {
        return trampolineAcceleration;
    }

    public void sendFormattedMessage(final String configName, final CommandSender receiver){
        String str = ChatColor.translateAlternateColorCodes('&', getConfig().getString(configName));
        if(str.isEmpty()){
            return;
        }

        receiver.sendMessage(str.split("\n"));
    }

    public void tryApplyEffect(final Player plrTarget) {
        if (!plrTarget.hasPermission(this.getUsePermission())) {
            this.sendFormattedMessage("permission-message", plrTarget);
            return;
        }

        Vector velocity = plrTarget.getVelocity();
        velocity.setY(this.getTrampolineAcceleration());
        plrTarget.setVelocity(velocity);
    }
}
