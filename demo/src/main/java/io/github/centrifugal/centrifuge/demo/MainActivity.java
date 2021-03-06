package io.github.centrifugal.centrifuge.demo;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import io.github.centrifugal.centrifuge.Client;
import io.github.centrifugal.centrifuge.ConnectEvent;
import io.github.centrifugal.centrifuge.DisconnectEvent;
import io.github.centrifugal.centrifuge.EventListener;
import io.github.centrifugal.centrifuge.Options;
import io.github.centrifugal.centrifuge.PublishEvent;
import io.github.centrifugal.centrifuge.SubscribeErrorEvent;
import io.github.centrifugal.centrifuge.SubscribeSuccessEvent;
import io.github.centrifugal.centrifuge.Subscription;
import io.github.centrifugal.centrifuge.SubscriptionEventListener;
import io.github.centrifugal.centrifuge.UnsubscribeEvent;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tv = findViewById(R.id.text);

        EventListener listener = new EventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onConnect(Client client, ConnectEvent event) {
                MainActivity.this.runOnUiThread(() -> tv.setText("Connected with client ID " + event.getClient()));
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onDisconnect(Client client, DisconnectEvent event) {
                MainActivity.this.runOnUiThread(() -> tv.setText("Disconnected: " + event.getReason()));
            }
        };

        Client client = new Client(
                "ws://192.168.1.35:8000/connection/websocket?format=protobuf",
                new Options(),
                listener
        );
        client.setToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0c3VpdGVfand0In0.hPmHsVqvtY88PvK4EmJlcdwNuKFuy3BGaF7dMaKdPlw");
        client.connect();

        SubscriptionEventListener subListener = new SubscriptionEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSubscribeSuccess(Subscription sub, SubscribeSuccessEvent event) {
                MainActivity.this.runOnUiThread(() -> tv.setText("Subscribed to " + sub.getChannel()));
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onSubscribeError(Subscription sub, SubscribeErrorEvent event) {
                MainActivity.this.runOnUiThread(() -> tv.setText("Subscribe error " + sub.getChannel() + ": " + event.getMessage()));
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onPublish(Subscription sub, PublishEvent event) {
                String data = new String(event.getData(), UTF_8);
                MainActivity.this.runOnUiThread(() -> tv.setText("Message from " + sub.getChannel() + ": " + data));
            }
            @SuppressLint("SetTextI18n")
            @Override
            public void onUnsubscribe(Subscription sub, UnsubscribeEvent event) {
                MainActivity.this.runOnUiThread(() -> tv.setText("Unsubscribed from " + sub.getChannel()));
            }
        };

        try {
            client.subscribe("chat:index", subListener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
