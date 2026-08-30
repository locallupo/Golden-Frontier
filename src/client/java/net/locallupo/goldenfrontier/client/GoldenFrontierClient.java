package net.locallupo.goldenfrontier.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.locallupo.goldenfrontier.wire.WirePayloads;

public class GoldenFrontierClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(WirePayloads.WireState.TYPE, (payload, context) ->
				WireClientState.applyState(payload));
		ClientPlayNetworking.registerGlobalReceiver(WirePayloads.Selection.TYPE, (payload, context) ->
				WireClientState.setSelection(payload.position()));
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> WireClientState.clear());
		WireClientRenderer.initialize();
	}
}
