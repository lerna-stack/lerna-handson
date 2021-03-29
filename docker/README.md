# Docker コンテナ利用ガイド

開発用の Docker コンテナを利用するためのガイドです。

## 使い方

開発用の Docker コンテナは docker-compose を使って管理します。

docker-compose の詳細な使い方は [Docker Compose CLI reference](https://docs.docker.com/compose/reference/overview/) を参照してください。

よくある使い方は下記を参照してください。

### コンテナを全てバックグラウンドで起動する

```bash
docker-compose up -d
```

### 特定のコンテナのログを監視

```bash
docker-compose logs -f --tail=10 cassandra    # cassandra サービスのログを監視
```

### コンテナを全て停止・破棄する

データベースに永続化されたデータは残ります。

```bash
docker-compose down
```

データベースに永続化されたデータも一緒に破棄する場合は以下のコマンドを実行します。

```bash
docker-compose down --volumes
```
