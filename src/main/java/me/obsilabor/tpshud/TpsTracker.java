package me.obsilabor.tpshud;

import me.obsilabor.alert.Subscribe;
import me.obsilabor.tpshud.config.ConfigManager;
import me.obsilabor.tpshud.event.GameJoinEvent;
import me.obsilabor.tpshud.event.PacketReceiveEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import java.util.Arrays;

public class TpsTracker {

    public static TpsTracker INSTANCE = new TpsTracker();

    private final float[] tickTimesMilli = new float[20];
    private int nextIndex = 0;
    private long timeLastTimeUpdate = -1;
    private long timeGameJoined;

    public float serverProvidedTps = -1;

    @Subscribe
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            long now = System.currentTimeMillis();
            long timeElapsed = now - timeLastTimeUpdate;

            // Packet is sent 1 time per second, so divide per 20 to get MSPT
            tickTimesMilli[nextIndex] = timeElapsed/20;
            nextIndex = (nextIndex + 1) % tickTimesMilli.length;
            timeLastTimeUpdate = now;
        }
    }

    @Subscribe
    public void onGameJoined(GameJoinEvent event) {
        serverProvidedTps = -1;
        Arrays.fill(tickTimesMilli, 0);
        nextIndex = 0;
        timeGameJoined = timeLastTimeUpdate = System.currentTimeMillis();
    }

    // Return milliseconds per tick (MSPT)
    public float getTickTime() {
        // get server side TPS if enabled and available
        if (serverProvidedTps != -1 && ConfigManager.INSTANCE.getConfig().getUseServerProvidedData()) {
            return serverProvidedTps;
        }

        MinecraftClient minecraft = MinecraftClient.getInstance();
        if (minecraft.player == null) return 0;
        if (System.currentTimeMillis() - timeGameJoined < 4000) return 20;

        // Calculate average tick time from network using the last 20 estimated tick times
        int numTicks = 0;
        float sumTickTimes = 0;
        for (float tickTime : tickTimesMilli) {
            if (tickTime > 0) {
                sumTickTimes += tickTime;
                numTicks++;
            }
        }
        return sumTickTimes / numTicks;
    }
}
