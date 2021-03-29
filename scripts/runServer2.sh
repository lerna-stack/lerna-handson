#!/usr/bin/env bash

# クラスタノード 2台目
sbt \
-DHOST=127.0.0.1 \
-DPORT=25522 \
-DAPP_HOST=127.0.0.1 \
-DAPP_PORT=9002 \
"SampleApp/run"
