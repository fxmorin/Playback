package kaptainwutax.playback.replay.action;

import io.netty.buffer.Unpooled;
import kaptainwutax.playback.util.PlaybackSerializable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.PacketByteBuf;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;

public abstract class Action implements PlaybackSerializable {
	private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

	protected static MinecraftClient client = MinecraftClient.getInstance();

	public Action() {

	}

	public abstract void play();

	public static void writeAction(PacketByteBuf buf, Action action) throws IOException {
		buf.writeVarInt(Type.of(action).ordinal());
		PacketByteBuf tmp = new PacketByteBuf(Unpooled.buffer());
		action.write(tmp);
		buf.writeVarInt(tmp.writerIndex());
		buf.writeBytes(tmp);
		tmp.release();
	}

	public static Action readAction(PacketByteBuf buf) throws IOException {
		Action action = Type.values[buf.readVarInt()].instantiate();
		int size = buf.readVarInt();
		PacketByteBuf actionBuf = new PacketByteBuf(buf.slice(buf.readerIndex(), size));
		action.read(actionBuf);
		buf.readerIndex(buf.readerIndex() + size);
		return action;
	}

	public enum Type {
		DEBUG(DebugAction.class),
		PACKET(PacketAction.class),
		KEY(KeyAction.class),
		MOUSE(MouseAction.class),
		WINDOW_FOCUS(WindowFocusAction.class),
		GAME_OPTIONS(GameOptionsAction.class),
		CLIPBOARD_READ(ClipboardReadAction.class);

		static final Type[] values = values();
		private static final Map<Class<?>, Type> classToTypeMap = new HashMap<>();

		static {
			for (Type t : values) classToTypeMap.put(t.cls, t);
		}

		public final Class<? extends Action> cls;
		private final MethodHandle constr;
		Type(Class<? extends Action> cls) {
			this.cls = cls;
			try {
				this.constr = LOOKUP.findConstructor(cls, MethodType.methodType(void.class));
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("Default action constructor does not exist", e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException("Default action constructor is not accessible", e);
			}
		}

		public Action instantiate() {
			try {
				return (Action) constr.invoke();
			} catch (Throwable throwable) {
				throw new RuntimeException(throwable);
			}
		}

		public static Type of(Action action) {
			Type t = classToTypeMap.get(action.getClass());
			if (t == null) throw new IllegalStateException("Invalid action " + action + " has no corresponding type");
			return t;
		}
	}
}
