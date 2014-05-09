package io.github.xxyy.dogetrampoline.nms.v1_7_R1;

import io.github.xxyy.dogetrampoline.DogeTrampolinePlugin;
import io.github.xxyy.dogetrampoline.api.NMSBlockProxy;

/**
 * Block proxy for Minecraft version 1.7.2
 *
 * @author <a href="http://xxyy.github.io/">xxyy</a>
 * @since 05.01.14
 */
public class BlockSpongeProxy extends net.minecraft.server.v1_7_R1.BlockSponge implements NMSBlockProxy {
    private Object backupOriginal;
    private final DogeTrampolinePlugin dogeTrampolinePlugin;

    public BlockSpongeProxy(final DogeTrampolinePlugin plugin){
        this.dogeTrampolinePlugin = plugin;
    }

    @Override
    public void a(net.minecraft.server.v1_7_R1.World world, int i, int j, int k, net.minecraft.server.v1_7_R1.Entity entity, float f) { //Using FQN to avoid confusion with Bukkit's World and Entity interface
        //super is empty

        if(!(entity instanceof net.minecraft.server.v1_7_R1.EntityPlayer)) { //Avoid confusion
            return;
        }

        dogeTrampolinePlugin.tryApplyEffect(((net.minecraft.server.v1_7_R1.EntityPlayer) entity).getBukkitEntity());
    }

    @Override
    public void hook() { //net.minecraft.server.v1_7_R1.Block#119
        backupOriginal = net.minecraft.server.v1_7_R1.Block.e(19);
        dogeTrampolinePlugin.getLogger().info("Ignore the following warning message about duplicate IDs:");
        this.c(0.6F); //Returns a Block and we can't access protected methods on that :/
        this.a(h); //Same here
        this.c("sponge"); //Guess what
        net.minecraft.server.v1_7_R1.Block.REGISTRY.a(19, "sponge", this.d("sponge")); //Override default sponge with our proxy
    }

    @Override
    public void unhook() {
        dogeTrampolinePlugin.getLogger().info("Ignore the following warning message about duplicate IDs:");
        net.minecraft.server.v1_7_R1.Block.REGISTRY.a(19, "sponge", backupOriginal); //Good thing we made a backup ;)
        backupOriginal = null; //memory and stuff
        dogeTrampolinePlugin.getLogger().info("Successfully removed proxy!"); //Yay
    }
}
