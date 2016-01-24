# actor-push
Actor Push notification server - replacement for Google Cloud Messaging

# Registering endpoint

```
curl -XPOST https://push.actor.im/apps/31337/subscriptions

{
  "data": {
    "endpoint": "https://push.actor.im/apps/31337/subscriptions/gx1jfzIDjfyVPySxMABgHGofzCp2pm0REovU0DdhHk",
    "mqttServer": {
      "hosts": [
        "tcp://104.155.92.40:5672"
      ]
      "username": "actor-client",
      "password": "ohgh5eeC",
    },
    "topic": "actor.31337.subscription.gx1jfzIDjfyVPySxMABgHGofzCp2pm0REovU0DdhHk"
  }
}
```

`data.endpoint` value should be sent to Actor Server using `RequestRegisterActorPush(endpoint)`
