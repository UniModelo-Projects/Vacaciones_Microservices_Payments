# Payment Service

## Description
Manages payment processing.

## Features
- Process payments
- Stores data in MongoDB
- Implements simulated failure for retry testing
- Sends retry jobs to Kafka topic `payments_retry_jobs`.

## Port
- Default: `8083`

## Endpoints
- `POST /pagos/procesar`: Process a payment
- `POST /pagos/retry`: Endpoint for Broker Service to retry payment processing.
