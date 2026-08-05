package net.locallupo.goldenfrontier.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.locallupo.goldenfrontier.wire.WirePayloads;

public class GoldenFrontierClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientPlayNetworking.registerGlobalReceiver(WirePayloads.Connections.TYPE, (payload, context) ->
				WireClientState.setConnections(payload.connections()));
		ClientPlayNetworking.registerGlobalReceiver(WirePayloads.Selection.TYPE, (payload, context) ->
				WireClientState.setSelection(payload.position()));
		WireClientRenderer.initialize();
	}
}
