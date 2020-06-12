### Querying logs of filesystem processor

The processor runs as a `systemd` unit in a EC2 instance provisioned by the migration stack. The logs are collected by `systemd` and can be viewed using `journalctl`

The following command will list all logs processed by the consumer since start of the day without truncating any messages. 
```bash
journalctl -u  dc-migration-sqs-consumer.service --since today -o cat -r --no-pager
```