# Payment Microservice

Servicio encargado del procesamiento de pagos y gestión de reembolsos.

## Detalles Técnicos
- **Puerto:** 8083
- **Base de Datos:** MongoDB (colección `pagos`).
- **Logs:** Envía logs al log group `pagos-log-group` en CloudWatch (LocalStack).

## Endpoints (vía Gateway)
| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `POST` | `/pagos/procesar` | Procesar un nuevo pago para una orden. |
| `GET` | `/pagos/{id}` | Obtener un pago por su ID. |
| `GET` | `/pagos/orden/{ordenId}` | Obtener el pago asociado a una orden. |
| `PUT` | `/pagos/{id}/reembolso` | Procesar el reembolso de un pago. |
