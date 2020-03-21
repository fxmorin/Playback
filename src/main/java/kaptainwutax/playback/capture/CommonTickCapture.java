package kaptainwutax.playback.capture;

import kaptainwutax.playback.capture.action.IAction;
import kaptainwutax.playback.capture.action.PacketAction;
import net.minecraft.network.Packet;

public class CommonTickCapture extends TickCapture {

	public CommonTickCapture() {

	}

	public void addPacketAction(Packet<?> packet) {
		this.addAction(new PacketAction(packet));
	}

	public void addDebugAction(IAction action){
		this.addAction(action);
	}

}
