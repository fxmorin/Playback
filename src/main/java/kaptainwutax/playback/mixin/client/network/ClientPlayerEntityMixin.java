package kaptainwutax.playback.mixin.client.network;

import com.mojang.authlib.GameProfile;
import kaptainwutax.playback.Playback;
import kaptainwutax.playback.replay.ReplayView;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends AbstractClientPlayerEntity {

	public ClientPlayerEntityMixin(ClientWorld world, GameProfile profile) {
		super(world, profile);
	}

	@Inject(method = "isCamera", at = @At("HEAD"), cancellable = true)
	private void isCamera(CallbackInfoReturnable<Boolean> ci) {
		//usually returns true, but setting the camera on the cameraplayer shouldn't modify behavior (movement code), so still return true
		if(Playback.getManager().replayPlayer != null && (Object) this == Playback.getManager().replayPlayer.getPlayer() && Playback.getManager().getView() == ReplayView.THIRD_PERSON) {
			ci.setReturnValue(true);
		}
	}

	/**
	 * Cancel sounds that only play for the first person when in third person mode
	 */
	@Inject(method = "updateWaterSubmersionState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;updateWaterSubmersionState()Z"), cancellable = true)
	private void cancelWaterSoundsIfNotCameraPlayer(CallbackInfoReturnable<Boolean> cir) {
		if (Playback.getManager().isReplaying() && (Playback.getManager().getView() == ReplayView.THIRD_PERSON) && ((PlayerEntity) this == Playback.getManager().replayPlayer.getPlayer())) {
			cir.setReturnValue(this.isSubmergedInWater);
		}
	}
	/**
	 * Cancel sounds that only play for the first person when in third person mode
	 */
	@Redirect(method = "onTrackedDataSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/sound/SoundManager;play(Lnet/minecraft/client/sound/SoundInstance;)V"))
	private void cancelElytraSoundsIfNotCameraPlayer(SoundManager soundManager, SoundInstance sound) {
		if (!Playback.getManager().isReplaying() || (Playback.getManager().getView() != ReplayView.THIRD_PERSON) || ((PlayerEntity) this != Playback.getManager().replayPlayer.getPlayer())) {
			soundManager.play(sound);
		}
	}

}
