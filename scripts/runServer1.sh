#!/usr/bin/env bash

# クラスタノード 1台目
sbt \
-DHOST=127.0.0.1 \
-DPORT=25521 \
-DAPP_HOST=127.0.0.1 \
-DAPP_PORT=9001 \
"SampleApp/run"
