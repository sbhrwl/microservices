# Brokers and Networks mapping
- [Brokers](#brokers)
- [Networks](#networks)
## Brokers
- IEC connector to GFC core
- `fijyvvrhessw12.conf`
```text
brokers {
  fijyvvrhessw12 {
    broker-url = "tcp://fijyvvrhessw12.eu.bm.net:61616"
    broker-url-options = "?jms.prefetchPolicy.all=1&jms.redeliveryPolicy.maximumRedeliveries=3",
    username = "broker"
    password = "broker"
  }
}
```
## Networks
- `fijyvvrhessw12-test-nw.conf`
```text
networks {
  gfc1-dev {
    network-id = "TEST_NW",
    broker-ref = "fijyvvrhessw12"
    queues {
      request = "IEC20_IN_GFC_TEST_NW"
      response = "IEC20_OUT_GFC_TEST_NW_RAHUL"
      events = "IEC20_PUSH"
    }
  }
}
```
