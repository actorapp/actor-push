# actor-push
Actor Push notification server - replacement for Google Cloud Messaging

# Registering endpoint

```
curl -XPOST https://push.actor.im/apps/31337/subscriptions

{
  "data": {
    "endpoint": "https://push.actor.im/apps/31337/subscriptions/gx1jfzIDjfyVPySxMABgHGofzCp%2F2pm0REovU0DdhHk%3D",
    "mqttServer": {
      "hosts": [
        "104.155.92.40"
      ],
      "virtualHost": "/",
      "username": "actor-client",
      "password": "ohgh5eeC",
      "port": 5672
    },
    "exchangeName": "amq.topic",
    "routingKey": "actor.31337.subscription.gx1jfzIDjfyVPySxMABgHGofzCp/2pm0REovU0DdhHk="
  }
}
```

`data.endpoint` value should be sent to Actor Server using `RequestRegisterActorPush(endpoint)`
