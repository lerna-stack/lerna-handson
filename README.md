# Lerna ハンズオン

## 概要

Lernaハンズオンに必要なサンプルアプリや演習問題が含まれています。  
ハンズオン資料は次の2つです。
- [Lernaハンズオン事前準備](docs/hands-on-preparation.pdf)
- [Lernaハンズオン](docs/hands-on.pdf)

## クイックスタート

サンプルアプリを動かしてみましょう。  
サンプルアプリは REST API を提供するサーバです。  
コンサートのチケット購入サービス を想定したサンプルとなります。

ターミナル1
```sh
cd docker
docker-compose up
```

ターミナル2
```sh
./scripts/runServer1.sh
```

ターミナル3
```sh
# コンサート作成 (コンサート名=RHCP, チケット枚数=3)
curl --silent --noproxy '*' -X POST -H "Content-Type: application/json" -d '{"tickets":3}' localhost:9001/concerts/RHCP
# コンサート取得 (コンサート名=RHCP)
curl --silent --noproxy '*' localhost:9001/concerts/RHCP
# コンサートチケット購入 (コンサート名=RHCP, チケット購入枚数=1)
curl --silent --noproxy '*' -X POST -H "Content-Type: application/json" -d '{"tickets":1}' localhost:9001/concerts/RHCP/tickets
# コンサートキャンセル (コンサート名=RHCP)
curl --silent --noproxy '*' -X POST localhost:9001/concerts/RHCP/cancel
# コンサート一覧  
# 一覧に反映されるまでは遅延時間があります。
curl --silent --noproxy '*' localhost:9001/concerts
```

## フォルダ構成

- docker  
サンプルアプリの動作に必要な Cassandra と MariaDB の Dockerfileがあります。
- exercise-accord-basic  
Accord のサンプルコードがあります。
- exercise-akka-basic  
Akka のサンプルコードがあります。  
演習問題も含まれています。
- exercise-akka-http-basic  
Akka HTTP のサンプルコードがあります。  
演習問題も含まれています。
- exercise-scala-basic  
Scala のサンプルコードがあります。  
演習問題も含まれています。
- exercise-slick-basic  
Slick のサンプルコードがあります。
- lerna-library  
Lernaのライブラリの一部です。
- lerna-testkit  
Lernaのテストライブラリの一部です。
- sample-app  
サンプルアプリです。
- scripts  
サンプルアプリを起動するためのスクリプトです。

## exercise-*** について

技術トピックごとにディレクトリがあり、  
ハンズオン説明資料で使用したサンプルコードが含まれています。  
また、演習問題が含まれているものもあります。  
各ディレクトリには、example, exercise, answer というパッケージがあります。  
- example  
サンプルコード(スライドなどに載せているものになります)
- exercise  
演習問題に必要なファイルがあります。
- answer  
演習問題の答えがあります。

## サンプルアプリ

サンプルアプリは REST API を提供するサーバです。  
コンサートのチケット購入サービス を想定したサンプルとなります。

### 実行方法

まず、次の2つのサービスを起動します。
- Cassandra
- MariaDB

```sh
cd docker
docker-compose up
# (停止するには Ctrl+C)
```

サンプルアプリ(APIサーバ)を起動します。
クラスタで起動できます。
最低1台のノードを起動する必要があります。
```sh
./scripts/runServer1.sh
# 次の２つはオプション
./scripts/runServer2.sh
./scripts/runServer3.sh
```

### テストの実行方法

演習向けテストを除外してテストする方法を準備しています。  
```sh
sbt testAll
```

演習ごとに特別なテスト用sbtコマンドを準備しています。
```sh
sbt testMyConcertActorBinding
sbt testMyConcertActor
sbt testMyBoxOfficeServiceBinding
sbt testMyBoxOfficeService
sbt testMyBoxOfficeResourceBinding
sbt testMyBoxOfficeResource
sbt testMyConcertRepositoryBinding
sbt testMyConcertRepository
```

### API リクエスト例

ポート 9001 の HTTPサーバ (runServer1.shで起動する) にリクエストしています。  
(runServer2.shやrunServer3.shで複数台起動している場合は)、  
9002,9003 のサーバにリクエストしても問題ありません。  

```sh
# コンサート作成 (コンサート名=RHCP, チケット枚数=3)
curl --silent --noproxy '*' -X POST -H "Content-Type: application/json" -d '{"tickets":3}' localhost:9001/concerts/RHCP

# コンサート取得 (コンサート名=RHCP)
curl --silent --noproxy '*' localhost:9001/concerts/RHCP

# コンサートチケット購入 (コンサート名=RHCP, チケット購入枚数=1)
curl --silent --noproxy '*' -X POST -H "Content-Type: application/json" -d '{"tickets":1}' localhost:9001/concerts/RHCP/tickets

# コンサートキャンセル (コンサート名=RHCP)
curl --silent --noproxy '*' -X POST localhost:9001/concerts/RHCP/cancel

# コンサート一覧  
# 一覧に反映されるまでは遅延時間があります。
curl --silent --noproxy '*' localhost:9001/concerts
```

### データの削除方法
次の２つのサービスのデータを削除します。
- Cassandra
- MariaDB

```sh
docker-compose down --volumes
```

### パッケージ構成

主要なパッケージは次の通りです。

- example.application.rmu  
リードモデルアップデータ
- example.application.http  
HTTP API サーバ
- example.model  
コマンド(ライト)側モデル
- example.readmodel  
コマンド(リード)側モデル
- example.usecase  
ユースケース
- example.serialization  
シリアライザ等


### テストに失敗する場合には?

コンピュータのスペックによってはタイムアウト値が短すぎてテストに失敗する場合があります。
[sample-app/src/test/resources/application.conf](sample-app/src/test/resources/application.conf)
にある次の設定値をすることで、タイムアウト値を変更することができます。

```config
akka.test.default-timeout = 5s
akka.test.timefactor = 1.0
```

## License
*lerna-handson*, except for files in the [docs](docs) directory, is released under the terms of the [Apache License Version 2.0](LICENSE).
The files in the [docs](docs) is released under the [Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0)](https://creativecommons.org/licenses/by-sa/4.0/).

© 2021 TIS Inc.
