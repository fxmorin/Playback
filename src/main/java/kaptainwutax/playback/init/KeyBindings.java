package kaptainwutax.playback.init;

import kaptainwutax.playback.Playback;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class KeyBindings {

	public static Set<FabricKeyBinding> KEYS = new HashSet<>();

	public static FabricKeyBinding TOGGLE_VIEW = FabricKeyBinding.Builder.create(Playback.createIdentifier("toggle_view"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, "key.categories.playback").build();
	public static FabricKeyBinding TOGGLE_PAUSE = FabricKeyBinding.Builder.create(Playback.createIdentifier("toggle_pause"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.categories.playback").build();
	public static FabricKeyBinding PLAY_CAMERA_PATH = FabricKeyBinding.Builder.create(Playback.createIdentifier("toggle_pause"), InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, "key.categories.playback").build();

	static {
		KEYS.add(TOGGLE_VIEW);
		KEYS.add(TOGGLE_PAUSE);
		KEYS.add(PLAY_CAMERA_PATH);
	}

	public static void registerKeyCategories() {
		KeyBindingRegistry.INSTANCE.addCategory("key.categories.playback");
	}

	public static void registerKeyBindings() {
		KeyBindingRegistry.INSTANCE.register(TOGGLE_VIEW);
		KeyBindingRegistry.INSTANCE.register(TOGGLE_PAUSE);
		KeyBindingRegistry.INSTANCE.register(PLAY_CAMERA_PATH);
	}


}
