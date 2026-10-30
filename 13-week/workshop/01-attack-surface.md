# CP1 — Attack surface of Property

Sources: property-docs 05-architecture/overview.md, 05-architecture/deployment.md and the
OpenAPI contracts in 07-api/contracts/. My boundary, in red: **api-gateway -> property-workflow**.

```mermaid
flowchart LR
  subgraph TB1["TB1 internet / gateway"]
      user([Guest / Host / Admin])
      front[property-front + 5 portals]
  end
  subgraph TB2["TB2 gateway / services"]
      wf[property-workflow :3006]
      booking[property-booking-api :3001]
      catalog[property-catalog-api :3002]
      payment[property-payment-api :3003]
      identity[property-identity-api :3004]
      notif[property-notification-api :3005]
      worker[property-worker]
  end
  subgraph TB3["TB3 broker"]
      mq[[RabbitMQ]]
  end
  subgraph TB4["TB4 data stores"]
      pg[(PostgreSQL 16: booking, payment, identity, notification, workflow)]
      mongo[(MongoDB 7: catalog)]
  end
    gw[property-api-gateway :8080]
    stripe[(Stripe sandbox)]
  user --> front
  front -->|HTTPS| gw
  gw -->|/api/v1/sagas| wf
  gw -->|/register /login /refresh /jwks| identity
  gw --> booking
  gw --> catalog
  gw --> payment
  gw --> notif
  wf -->|crear-reserva / confirmar| booking
  wf -->|cobrar / reembolso| payment
  payment -->|charge, refund| stripe
  worker -->|expire| booking
  booking -->|outbox| mq
  payment -->|outbox| mq
  mq --> notif
  mq --> catalog
  booking -->|booking_app| pg
  payment -->|payment_app| pg
  identity -->|identity_app| pg
  notif -->|notification_app| pg
  wf -->|workflow_app| pg
  catalog -->|catalog_app| mongo
  linkStyle 2 stroke:#d00,stroke-width:4px
```

## Assets of my boundary

| Asset | Where it lives | Classification |
|---|---|---|
| Access token (JWT RS256: sub, roles) | Authorization header, gateway -> workflow | restricted |
| tokenPago (card token) | body of POST /sagas/reservar; passed to payment-api, never stored or logged | restricted |
| Idempotency-Key of the Saga | header; workflow.saga_instance.idempotency_key | internal |
| saga_instance (status, guest_id, reservation_id, payment_id) | PostgreSQL, schema workflow | confidential |
| Booking request (propiedadId, fechaInicio, fechaFin, huespedes) | body of POST /sagas/reservar | internal |
| X-Correlation-Id / traceId | headers, JSON logs, error envelope | internal |

## Entry points of my boundary

| Entry point | Who calls it | Authentication today |
|---|---|---|
| POST /api/v1/sagas/reservar (E-23) | front, through the gateway | gateway: Authorization header present; workflow: JWT RS256 (signature, exp, sub). Idempotency-Key required |
| GET /api/v1/sagas/{id} (E-24) | front, through the gateway | JWT; only the guest who started the Saga, or ADMIN |
| GET /health | gateway, operators | none |
| Port 3006 in the network platform | any container in platform | not published to the host (expose); JWT still required |
